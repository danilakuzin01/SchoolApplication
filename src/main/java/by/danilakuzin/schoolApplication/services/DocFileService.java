package by.danilakuzin.schoolApplication.services;

import by.danilakuzin.schoolApplication.models.Classes;
import by.danilakuzin.schoolApplication.models.SchoolClass;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@Service
public class DocFileService {
    private static final Logger LOGGER = Logger.getLogger(DocFileService.class.getName());
//    @Value("${file.name}")
//    @Value("src/main/resources/files/downloaded_file.doc")
    private String filePath;

    private XWPFDocument document;
    private XWPFTable table;
    @Getter
    private String paragraph;

    @Getter
    private final List<SchoolClass> schoolClasses = new ArrayList<>();

    private final HashMap<Integer, List<String>> tableRowContent = new HashMap<>();
    private final List<String> classNumbers = Arrays.asList("5", "6", "7", "8", "9", "10", "11");

    // Конструктор на случай если понадобятся другие поля
    public DocFileService() {

    }

    // Инициализация через конструктор (чтобы отработал Value у filePath)
    @PostConstruct
    public void makeClasses() {

        try {
            filePath = "src/main/resources/files/downloaded_file.doc";
            readWordFile();
            createSchoolClasses();
            createClasses();
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
            setTableRowContent();
        } catch (IOException e) {
            e.printStackTrace();
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
        AtomicInteger id = new AtomicInteger(0);

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
        int id = 0;
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


                Classes classes = Classes.builder()
                        .id(id++)
                        .number(String.valueOf(classNumber))
                        .name(cellName.getText())
                        .cab(cellCab.getText())
                        .build();
                LOGGER.info(schoolClass.getName());
                schoolClass.AddClass(classes);
                isCreated = true;

            }
            if (isCreated) classNumber++;
        }
//        LOGGER.info(schoolClasses.stream().filter(p -> p.getName().contains("9В")).findFirst().toString());
    }

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
}