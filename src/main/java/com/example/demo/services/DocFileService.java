package com.example.demo.services;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.example.demo.models.Classes;
import com.example.demo.models.SchoolClass;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DocFileService {
    private static final Logger LOGGER = Logger.getLogger(DocFileService.class.getName());

    @Value("${file.name}")
    private String filePath;


    @Value("${formatted.file.name}")
    private String formattedFilePath;

    private XWPFDocument document;
    private XWPFTable table;
    @Getter
    private String paragraph;

    @Getter
    private final List<SchoolClass> schoolClasses = new ArrayList<>();

    private final HashMap<Integer, List<String>> tableRowContent = new HashMap<>();

    // Конструктор на случай если понадобятся другие поля
    public DocFileService(){

    }

    // Инициализация через конструктор (чтобы отработал Value у filePath)
    @PostConstruct
    public void init() {
        try {
            readWordFile();
            formatWordFile();
            createSchoolClasses();
            createClasses();
        } catch (Exception e) {
            LOGGER.info("Ошибка: " + e);
        }
    }

    private void readWordFile() {
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

    // TODO написать комментарий
    private void formatWordFile() throws IOException {
        // Вставить строку после второй строки (индекс 1)
        int maxSize = 54;
        for (int rowIndex=2; rowIndex< table.getRows().size()-1; rowIndex++){
            if (table.getRows().size() >= maxSize) break;

            XWPFTableRow oldRow = table.getRow(rowIndex);
            XWPFTableRow oldNextRow = table.getRow(rowIndex+1);

            String oldCellNum = oldRow.getCell(0).getText();
            String oldNextCellNum = oldNextRow.getCell(0).getText();

            if (oldCellNum.isBlank() || oldCellNum.isEmpty()) continue;
            if (Integer.parseInt(oldCellNum) < 8 &&
                    (oldNextCellNum.isEmpty() || oldNextCellNum.isBlank())){

                try {
                    CTRow ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());

                    XWPFTableRow newRow = new XWPFTableRow(ctrow, table);

                    for (XWPFTableCell cell : newRow.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
                            for (XWPFRun run : paragraph.getRuns()) {
                                run.setText("", 0);
                            }
                        }
                    }
                    XWPFTableCell cell = newRow.getCell(0);
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        for (XWPFRun run : paragraph.getRuns()) {
                            run.setText(String.valueOf(
                                    Integer.parseInt(oldCellNum)+1
                            ), 0);
                        }
                    }

                    table.addRow(newRow, rowIndex+1);

                } catch (XmlException e) {
                    throw new RuntimeException(e);
                }

            }
        }
        String lastCellNum = table.getRow(table.getRows().size()-1).getCell(0).getText();
        if (Integer.parseInt(lastCellNum) < 8){
            table.createRow();

            XWPFTableRow newLastRow = table.getRow(table.getRows().size()-1);
            XWPFTableRow lastRow = table.getRow(table.getRows().size()-2);
            lastCellNum = lastRow.getCell(0).getText();

            XWPFTableCell cell = newLastRow.getCell(0);
            cell.setText(String.valueOf(Integer.parseInt(lastCellNum)+1));

        }



        //Сохраняем файл
        try (FileOutputStream fos = new FileOutputStream("formatted_file.doc")) {
            document.write(fos);  // Сохраняем документ в файл
            LOGGER.info("Успешно сохранен");
            document = new XWPFDocument(new FileInputStream(formattedFilePath));
        }
    }

    // Заполнение таблицы построчно
    private void setTableRowContent() {
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
        int id = 0;

        // Т.к. названия классов пишутся через каждые 9 строк то нам нет необходимости проходить по всем строкам,
        // а только каждую 9-ю строку
        for (int rowIndex = 0; rowIndex < table.getRows().size(); rowIndex+=9) {

            // Т.к. названия пишутся через колонку то нам нет необходимости проверять каждую колонку
            for (int cellId = 1; cellId < table.getRow(rowIndex).getTableCells().size(); cellId+=2) {
                // Берем клетку, создаем класс и добавляем в список классов
                XWPFTableCell cell = table.getRow(rowIndex).getCell(cellId);
                SchoolClass schoolClass = SchoolClass.builder()
                        .id(id++)
                        .name(cell.getText())
                        .build();
                schoolClasses.add(schoolClass);
            }
        }
        if (!schoolClasses.isEmpty()) {
            schoolClasses.removeLast(); // Удаление последнего класса
        }
    }

    // Создание уроков и привязка к классам
    public void createClasses(){
        int id = 0;

        // Начинаем со второй строки, т.к. первая не нужна
        for (int rowIndex = 1; rowIndex < table.getRows().size(); rowIndex++) {
            for (int cellId = 1; cellId < table.getRow(rowIndex).getTableCells().size(); cellId++) {
                XWPFTableCell cell = table.getRow(rowIndex).getCell(cellId);

                if (rowIndex % 9 != 0 && cellId % 2 == 1) {
                    processClassCell(rowIndex, cellId, id++);
                }
            }
        }
    }

    private void processClassCell(int rowIndex, int cellId, int id){
        XWPFTableRow row = table.getRow(rowIndex);
        XWPFTableCell cell = row.getCell(cellId);
        XWPFTableCell cellCab = row.getCell(cellId+1);

        int column = rowIndex - (rowIndex % 9);
        String columnName = table.getRow(column).getCell(cellId).getText();
        SchoolClass schoolClass = schoolClasses.stream()
                .filter(p -> p.getName().equals(columnName))
                .findFirst()
                .orElse(null);

        if (schoolClass != null){
            Classes classes = Classes.builder()
                    .id(id)
                    .name(cell.getText())
                    .cab(cellCab.getText())
                    .number(String.valueOf(rowIndex%9)).build();
            schoolClass.AddClass(classes);
        }
    }
}