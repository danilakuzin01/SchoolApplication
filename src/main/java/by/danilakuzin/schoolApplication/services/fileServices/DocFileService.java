package by.danilakuzin.schoolApplication.services.fileServices;

import by.danilakuzin.schoolApplication.models.Lesson;
import by.danilakuzin.schoolApplication.models.SchoolClass;
import by.danilakuzin.schoolApplication.models.SchoolDate;
import by.danilakuzin.schoolApplication.services.impl.LessonServiceImpl;
import by.danilakuzin.schoolApplication.services.impl.SchoolClassServiceImpl;
import by.danilakuzin.schoolApplication.services.impl.SchoolDateServiceImpl;
import lombok.Getter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

@Component
public class DocFileService {
    private static final Logger LOGGER = Logger.getLogger(DocFileService.class.getName());

    // Сервисы
    @Autowired
    private YandexDiskDownloader yandexDiskDownloader;
    @Autowired
    private SchoolDateServiceImpl schoolDateService;
    @Autowired
    private SchoolClassServiceImpl schoolClassService;
    @Autowired
    private LessonServiceImpl lessonService;

    // Путь до файла
    private String filePath = "src/main/resources/files/downloaded_file.docx";

    // Документ word, таблица, параграф и список классов
    private XWPFDocument document;
    private XWPFTable table;
    @Getter
    private String paragraph;
    @Getter
    private final List<SchoolClass> schoolClasses = new ArrayList<>();

    private final HashMap<Integer, List<String>> tableRowContent = new HashMap<>();
    private final List<String> classNumbers = Arrays.asList("5", "6", "7", "8", "9", "10", "11");
    LocalDate date = LocalDate.of(2000, 01, 01);
    boolean isExist = false;

    // Инициализация через конструктор (чтобы отработал Value у filePath)
    public void reDownload() throws IOException {
        yandexDiskDownloader.download(filePath);
        makeLessons();
        moveFile();
    }

    // Обновление информации для выбранного файла
    public void updateInfoFile(String path) throws IOException {
        filePath = path;
        makeLessons();
    }

    public void makeLessons() {
        try {
            readWordFile();
            getDate();

            createSchoolClasses();
            createClasses();
            isExist = false;
        } catch (Exception e) {
            LOGGER.info("Ошибка: " + e);
        }
    }

    public void readWordFile() {
        StringBuilder text = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            // Открытие документа Word
            document = new XWPFDocument(fis);
            // Получение всех параграфов и таблиц документа
            paragraph = document.getParagraphs().getFirst().getText();
            table = document.getTables().getFirst();

            // Проход по всем параграфам и добавление текста в StringBuilder
//            setTableRowContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Создание списка школьных классов
    public void createSchoolClasses() {
        schoolClasses.clear();
        AtomicLong id = new AtomicLong(0);

        SchoolDate schoolDate = schoolDateService.getSchoolClassesToday().getFirst();

        if (schoolDate != null && schoolDate.getSchoolClasses().size() < 0)

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
                                    .date(schoolDate)
                                    .build();
                            schoolClassService.save(schoolClass);
                            schoolClasses.add(schoolClass);
                        });
                });
        });
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
                        .schoolClass(schoolClass)
                        .number(String.valueOf(classNumber))
                        .name(cellName.getText())
                        .cab(cellCab.getText())
                        .build();

                schoolClass.AddLesson(lesson);
                isCreated = true;

            }
            if (isCreated) classNumber++;
        }
    }
    // Проверка класса, который находится над уроком
    private SchoolClass checkClassAbove(int rowIndex, int cellId){
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

    // Получение даты из файла
    private void getDate(){
        // Регулярное выражение для поиска даты в формате дд.мм.гггг или дд,мм,гггг
        Pattern pattern = Pattern.compile("(\\d{2})[.,](\\d{2})[.,](\\d{4})");
        Matcher matcher = pattern.matcher(paragraph);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDateTime dateTime = LocalDateTime.now();

        while (matcher.find()) {
            // Приводим к формату с точками для парсинга
            String dateString = matcher.group().replace(',', '.');
            dateTime = LocalDateTime.parse(dateString + "T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        LOGGER.info("Найдена дата: " + dateTime);
        date = dateTime.toLocalDate();

        // Если такой даты в БД нет, то создание такой записи
        if (schoolDateService.getSchoolClassesByDate(date).size() < 1){
            isExist = false;
            LOGGER.info("В БД нет записей на этот день");
            createSchoolDate(dateTime);
        } else {
            isExist = true;
            LOGGER.info("В БД уже есть такая запись");
        }
    }

    private void createSchoolDate(LocalDateTime dateTime){
        SchoolDate schoolDate = SchoolDate.builder()
                .date(dateTime.toLocalDate())
                .dateTime(dateTime)
                .filePath("src/main/resources/files/"+ dateTime.toLocalDate())
                .name(paragraph)
                .build();
        LOGGER.info(schoolDate.toString());
        schoolDateService.save(schoolDate);
    }

    private void moveFile() throws IOException {
        // Создание папки для файла
        Path dirPath = Paths.get("src/main/resources/files/"+ date.toString());
//        if (!Files.exists(dirPath)) {
//            Files.createDirectory(dirPath);
//        }

        // Перенос файла в новую папку
        Path oldFilePath = Paths.get(filePath);
//        Path newFilePath = Paths.get("src/main/resources/files/"+ date.toString()+"/document.docx");
        Path newFilePath = Paths.get("src/main/resources/files/"+ date + ".docx");
        if (!Files.exists(newFilePath)) {
            Files.copy(oldFilePath, newFilePath, REPLACE_EXISTING);
        }


    }
}