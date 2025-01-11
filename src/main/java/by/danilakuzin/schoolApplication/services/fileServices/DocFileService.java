package by.danilakuzin.schoolApplication.services.fileServices;

import by.danilakuzin.schoolApplication.models.Lesson;
import by.danilakuzin.schoolApplication.models.SchoolClass;
import by.danilakuzin.schoolApplication.models.SchoolClassDate;
import lombok.Getter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
public class DocFileService {
    private static final Logger LOGGER = Logger.getLogger(DocFileService.class.getName());

    private final YandexDiskDownloader yandexDiskDownloader;  // Внедрение зависимости

    private String filePath = "src/main/resources/files/downloaded_file.docx";
    LocalDate date = LocalDate.of(2000, 01, 01);

    private XWPFDocument document;
    private XWPFTable table;
    @Getter
    private String paragraph;

    @Getter
    private List<SchoolClass> schoolClasses = new ArrayList<>();

    private HashMap<Integer, List<String>> tableRowContent = new HashMap<>();
    private final List<String> classNumbers = Arrays.asList("5", "6", "7", "8", "9", "10", "11");

    // Конструктор на случай если понадобятся другие поля
    // ✅ Внедрение YandexDiskDownloader через конструктор
    public DocFileService(YandexDiskDownloader yandexDiskDownloader) {
        this.yandexDiskDownloader = yandexDiskDownloader;
    }


    // Инициализация через конструктор (чтобы отработал Value у filePath)
    public void reDownload() throws IOException {
        filePath = "src/main/resources/files/downloaded_file.docx";

        yandexDiskDownloader.download(filePath);

        getData();
        moveFile();
    }

    // Обновление информации для выбранного файла
    public void updateInfoFile(String path) throws IOException {
        filePath = path;
        getData();
    }

    public void getData(){
        readWordFile();
        getDate();

        SchoolClassDate schoolClassDate = SchoolClassDate.builder()
                .date(date)
                .name(paragraph)
                .build();

        createSchoolClasses();
        createClasses();
    }

    // Чтение файла
    public void readWordFile() {
        StringBuilder text = new StringBuilder();

        try (FileInputStream fis = new FileInputStream(filePath)) {
            // Открытие документа Word
            document = new XWPFDocument(fis);

            // Получение всех параграфов и таблиц документа
            paragraph = document.getParagraphs().getFirst().getText();
            table = document.getTables().getFirst();

            // Проход по всем параграфам и добавление текста в StringBuilder
            setTableRowContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Получение даты из файла
    private void getDate(){
        LOGGER.info(paragraph);
        // Регулярное выражение для поиска даты в формате дд.мм.гггг или дд,мм,гггг
        Pattern pattern = Pattern.compile("(\\d{2})[.,](\\d{2})[.,](\\d{4})");
        Matcher matcher = pattern.matcher(paragraph);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        while (matcher.find()) {
            // Приводим к формату с точками для парсинга
            String dateString = matcher.group().replace(',', '.');
            date = LocalDate.parse(dateString, formatter);
            LOGGER.info("Найдена дата: " + date);
        }

    }

    // Заполнение таблицы построчно
    public void setTableRowContent() {
        for (int rowIndex = 0; rowIndex < table.getRows().size(); rowIndex++) {
            List<String> rowContentList = new ArrayList<>();
            for (int cellId = 0; cellId < table.getRow(rowIndex).getTableCells().size(); cellId++) {
                XWPFTableCell cell = table.getRow(rowIndex).getCell(cellId);
                rowContentList.add(cell.getText());
            }
            tableRowContent.put(rowIndex, rowContentList);
        }
    }

    // Создание списка школьных классов
    public void createSchoolClasses() {
        schoolClasses.clear();
        AtomicLong id = new AtomicLong(0);

        table.getRows().forEach(row -> {
            row.getTableCells().stream()
                .skip(1)
                .filter(cell -> row.getTableCells().indexOf(cell) % 2 != 0)
                .forEach(cell -> {
                    // Для каждой клетки проверяем, содержит ли она номер класса
                    classNumbers.stream()
                        .filter(number -> cell.getText().contains(number))
                        .findFirst() // Ищем первое совпадение
                        .ifPresent(number -> {
                            // Если нашли номер, создаем новый класс
                            SchoolClass schoolClass = SchoolClass.builder()
                                    .id(id.getAndIncrement())
                                    .name(cell.getText())
                                    .build();
                            schoolClasses.add(schoolClass);
                        });
                });
        });
//        LOGGER.info(schoolClasses.toString());
    }

    // Создание уроков и привязка к классам
    public void createClasses() {
        long id = 0L;
        int classNumber = 1;  // Порядковый номер для уроков
        boolean isCreated = false;

        // Начинаем с первой строки таблицы
        for (int rowIndex = 1; rowIndex < table.getRows().size(); rowIndex++) {
            // Перебираем ячейки в строках с шагом 2 (столбцы с классами)
            for (int cellId = 1; cellId < table.getRow(rowIndex).getTableCells().size(); cellId += 2) {

                XWPFTableCell cellName = table.getRow(rowIndex).getCell(cellId);
                XWPFTableCell cellCab = table.getRow(rowIndex).getCell(cellId+1);

                if (classNumbers.stream().anyMatch(cellName.getText()::contains)) {
                    // Если значение найдено, пропускаем эту ячейку
                    isCreated = false;
                    classNumber = 1;
                    break; // Переходим к следующей ячейке
                }

                if (cellName.getText().isBlank() || cellName.getText().isBlank()) continue;

                SchoolClass schoolClass = checkClassAbove(rowIndex, cellId);

                Lesson lesson = Lesson.builder()
                        .id(id++)
                        .number(String.valueOf(classNumber))
                        .name(cellName.getText())
                        .cab(cellCab.getText())
                        .build();
//                LOGGER.info(schoolClass.getName());
                schoolClass.AddLesson(lesson);
                isCreated = true;

            }
            if (isCreated) classNumber++;
        }
//        LOGGER.info(schoolClasses.stream().filter(p -> p.getName().contains("9В")).findFirst().toString());
    }

    // Проверка класса, который находится над уроком
    private SchoolClass checkClassAbove(int rowIndex, int cellId){
        // Список номеров классов

        // Проверка ячеек выше текущей строки на наличие значений из classNumbers
        SchoolClass foundClass = null;

        // Перебираем строки выше текущей
        for (int aboveRowIndex = rowIndex - 1; aboveRowIndex >= 0; aboveRowIndex--) {
            XWPFTableRow aboveRow = table.getRow(aboveRowIndex);

            // Перебираем ячейки в строках выше (с шагом 2, так как нам нужно только те ячейки, где могут быть классы)
                XWPFTableCell aboveCell = aboveRow.getCell(cellId);

                // Проверяем, содержит ли ячейка из строки выше одно из значений из classNumbers
                if (classNumbers.stream().anyMatch(aboveCell.getText()::contains)) {
                    // Если класс найден, ищем объект SchoolClass по имени
                    String columnName = aboveCell.getText(); // Имя найденного класса
                    foundClass = schoolClasses.stream()
                            .filter(p -> p.getName().equals(columnName))
                            .findFirst()
                            .orElse(null); // Получаем объект класса, если найден
                    break; // Прерываем внутренний цикл
                }
            if (foundClass != null) break; // Прерываем внешний цикл, если класс найден
        }

        // Возвращаем найденный класс или null, если класс не найден
        return foundClass;
    }

    // Перемещение файла в папку с его датой
    private void moveFile() throws IOException {
        // Создание папки для файла
        Path dirPath = Paths.get("src/main/resources/files/"+ date.toString());
        if (!Files.exists(dirPath)) {
            Files.createDirectory(dirPath);
        }

        // Перенос файла в новую папку
        Path oldFilePath = Paths.get(filePath);
        Path newFilePath = Paths.get("src/main/resources/files/"+ date.toString()+"/document.docx");
        if (!Files.exists(newFilePath)) {
            Files.copy(oldFilePath, newFilePath, REPLACE_EXISTING);
        }
    }
}