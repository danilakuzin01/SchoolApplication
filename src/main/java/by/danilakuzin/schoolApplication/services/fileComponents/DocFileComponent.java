package by.danilakuzin.schoolApplication.services.fileComponents;

import by.danilakuzin.schoolApplication.models.Lesson;
import by.danilakuzin.schoolApplication.models.LessonPlan;
import by.danilakuzin.schoolApplication.models.SchoolClass;
import by.danilakuzin.schoolApplication.models.SchoolDate;
import by.danilakuzin.schoolApplication.services.LessonPlanService;
import by.danilakuzin.schoolApplication.services.LessonService;
import by.danilakuzin.schoolApplication.services.SchoolClassService;
import by.danilakuzin.schoolApplication.services.SchoolDateService;
import lombok.Getter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Component
public class DocFileComponent {
    private static final Logger LOGGER = Logger.getLogger(DocFileComponent.class.getName());

    // Сервисы
    @Autowired
    private YandexDiskDownloader yandexDiskDownloader;
    @Autowired
    private SchoolDateService schoolDateService;
    @Autowired
    private SchoolClassService schoolClassService;
    @Autowired
    private LessonService lessonService;
    @Autowired
    private LessonPlanService lessonPlanService;

    // Путь до файла
    private String filePath = "src/main/resources/files/downloaded_file.docx";

    // Документ word, таблица, параграф и список классов
    private XWPFDocument document;
    private XWPFTable table;
    @Getter
    private String paragraph;

    private final List<String> classNumbers = Arrays.asList("5", "6", "7", "8", "9", "10", "11");
    LocalDate date = LocalDate.of(2000, 01, 01);
    boolean isExist = false;

    // Инициализация через конструктор (чтобы отработал Value у filePath)
    public void reDownload() throws IOException {
        yandexDiskDownloader.download(filePath);
        makeDate();
//        createSchoolClasses();
        moveFile();
    }

    // Обновление информации для выбранного файла
    public void updateInfoFile(String path) throws IOException {
        filePath = path;
        makeDate();
        createSchoolClasses();
    }

    public void makeDate() {
        try {
            readWordFile();
            getDate();
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

    // Получение даты из файла
    private void getDate() {
        LocalDateTime dateTime = null;
        // Регулярное выражение для поиска даты
        String regex = "\\b(\\d{2})[.,](\\d{2})[.,](\\d{2,4})\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(paragraph);

        if (matcher.find()) {
            String day = matcher.group(1);
            String month = matcher.group(2);
            String year = matcher.group(3);

            // Обработка года
            if (year.length() == 2) {
                year = "20" + year; // Допустим, что даты относятся к 21 веку
            }

            // Форматирование даты
            String dateString = String.format("%s-%s-%s", year, month, day);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            try {
                LocalDate localDate = LocalDate.parse(dateString, formatter);
                dateTime = localDate.atStartOfDay(); // Конвертируем в LocalDateTime
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Неверный формат даты: " + paragraph, e);
            }
        } else {
            throw new IllegalArgumentException("Дата не найдена в строке: " + paragraph);
        }

        date = dateTime.toLocalDate();

        LessonPlan lessonPlan = lessonPlanService.getByDate(date);
        if (lessonPlan == null) {
            LOGGER.info("Такой даты еще нет");
            lessonPlan = new LessonPlan();
            lessonPlanService.save(lessonPlan);
        }

        // Если такой даты в БД нет, то создание такой записи
        if (schoolDateService.getSchoolDateByDate(date) == null) {
            isExist = false;
            LOGGER.info("В БД нет записей на этот день");
            createSchoolDateAndPlan(dateTime, lessonPlan);
        } else {
            isExist = true;
            if (lessonPlan.getSchoolDate() == null) {
                lessonPlan.setSchoolDate(schoolDateService.getSchoolDateByDate(date));
                lessonPlanService.save(lessonPlan);
            }
            LOGGER.info("В БД уже есть такая запись ");
        }
    }

    // Создание списка школьных классов
    // TODO сделать чтобы работало только при запуске
    public void createSchoolClasses() {

//        SchoolDate schoolDate = schoolDateService.getSchoolClassesToday().getFirst();

//        if (schoolDate != null && schoolDate.getSchoolClasses().size() < 0)

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
                                            .name(cell.getText())
                                            .build();
                                    if (schoolClassService.getByName(cell.getText()).isEmpty()) {
                                        schoolClassService.save(schoolClass);

                                    } else LOGGER.info("Такой класс уже есть в БД");
                                });
                    });
        });
    }

    // Создание уроков и привязка к классам
    public void createClasses() {
        List<Lesson> lessons = new ArrayList<>();
        LessonPlan lessonPlan = lessonPlanService.getByDate(date);

        int classNumber = 1;  // Порядковый номер для уроков
        boolean isCreated = false;

        // Начинаем с первой строки таблицы
        for (int rowIndex = 1; rowIndex < table.getRows().size(); rowIndex++) {
            // Перебираем ячейки в строках с шагом 2 (столбцы с классами)
            for (int cellId = 1; cellId < table.getRow(rowIndex).getTableCells().size(); cellId += 2) {

                XWPFTableCell cellName = table.getRow(rowIndex).getCell(cellId);
                XWPFTableCell cellCab = table.getRow(rowIndex).getCell(cellId + 1);

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
                        .lessonPlan(lessonPlan)
                        .number(String.valueOf(classNumber))
                        .name(cellName.getText())
                        .cab(cellCab.getText())
                        .build();
                Lesson lessonFromDB = lessonService.getByNameAndSchoolClassAndNumber(lesson.getName(), lesson.getSchoolClass(), lesson.getNumber());
                if (lessonFromDB != null) {
                    if (lessonService.equals(lesson, lessonFromDB)) {
                        lesson = lessonFromDB;
                    }
                    else {
                        lessonService.save(lesson);
                    }
                } else {
                    lessonService.save(lesson);
                }
                LOGGER.info(lesson.getNumber() + " " + lesson.getName() + " " + lesson.getSchoolClass().getName());
                lessons.add(lesson);
//                schoolClass.AddLesson(lesson);
                isCreated = true;

            }
            if (isCreated) classNumber++;
        }
        lessonPlan.setLessons(lessons);
        lessonPlanService.save(lessonPlan);
    }

    // Проверка класса, который находится над уроком
    private SchoolClass checkClassAbove(int rowIndex, int cellId) {
        // Проверка ячеек выше текущей строки на наличие значений из classNumbers


        // Перебираем строки выше текущей
        for (int aboveRowIndex = rowIndex - 1; aboveRowIndex >= 0; aboveRowIndex--) {
            XWPFTableRow aboveRow = table.getRow(aboveRowIndex);

            // Перебираем ячейки в строках выше (с шагом 2, так как нам нужно только те ячейки, где могут быть классы)
            XWPFTableCell aboveCell = aboveRow.getCell(cellId);

            // Проверяем, содержит ли ячейка из строки выше одно из значений из classNumbers
            if (classNumbers.stream().anyMatch(aboveCell.getText()::contains)) {
                // Если класс найден, ищем объект SchoolClass по имени
                String columnName = aboveCell.getText(); // Имя найденного класса
                return schoolClassService.getByName(columnName).orElseThrow(); // Получаем объект класса, если найден
            }
        }

        // Возвращаем найденный класс или null, если класс не найден
        //TODO обработать null
        return null;
    }

    private void createSchoolDateAndPlan(LocalDateTime dateTime, LessonPlan lessonPlan) {
        SchoolDate schoolDate = SchoolDate.builder()
                .date(dateTime.toLocalDate())
                .dateTime(dateTime)
                .filePath("src/main/resources/files/" + dateTime.toLocalDate())
                .name(paragraph)
                .build();

        lessonPlan.setSchoolDate(schoolDate);

        schoolDateService.save(schoolDate);
        lessonPlanService.save(lessonPlan);

    }

    // Выдача даты файлу
    private void moveFile() throws IOException {
        // Создание папки для файла
        Path dirPath = Paths.get("src/main/resources/files/" + date.toString());
//        if (!Files.exists(dirPath)) {
//            Files.createDirectory(dirPath);
//        }

        // Перенос файла в новую папку
        Path oldFilePath = Paths.get(filePath);
//        Path newFilePath = Paths.get("src/main/resources/files/"+ date.toString()+"/document.docx");
        Path newFilePath = Paths.get("src/main/resources/files/" + date + ".docx");
        Files.copy(oldFilePath, newFilePath, REPLACE_EXISTING);
    }
}