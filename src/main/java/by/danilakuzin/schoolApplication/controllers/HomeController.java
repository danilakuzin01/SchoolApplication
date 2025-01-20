package by.danilakuzin.schoolApplication.controllers;

import by.danilakuzin.schoolApplication.models.Lesson;
import by.danilakuzin.schoolApplication.models.LessonPlan;
import by.danilakuzin.schoolApplication.models.SchoolClass;
import by.danilakuzin.schoolApplication.services.LessonPlanService;
import by.danilakuzin.schoolApplication.services.LessonService;
import by.danilakuzin.schoolApplication.services.SchoolClassService;
import by.danilakuzin.schoolApplication.services.fileComponents.DocFileComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

@Controller
public class HomeController {
    private final Logger LOGGER = Logger.getLogger(HomeController.class.getName());
    private final DocFileComponent docFileComponent;

    @Autowired
    private LessonPlanService lessonPlanService;
    @Autowired
    private LessonService lessonService;
    @Autowired
    private SchoolClassService schoolClassService;

    public HomeController(DocFileComponent docFileComponent) {
        this.docFileComponent = docFileComponent;
    }

    @GetMapping("/")
    public String home(Model model) {
//        LOGGER.info(docFileService.getParagraph());
//        LOGGER.info(docFileService.getSchoolClasses().toString());
        LessonPlan lessonPlan = lessonPlanService.getByDate(LocalDate.now());
        List<SchoolClass> schoolClasses = schoolClassService.getAll(); // Загрузка всех классов
        for (var schoolClass : schoolClasses){
            schoolClass.getLessons().sort(Comparator.comparing(Lesson::getNumber));
        }
        model.addAttribute("schoolClasses", schoolClasses);
        model.addAttribute("lessonPlan", lessonPlan);

        // Возврат имени шаблона (без суффикса .html)
        return "pages/Home";
    }

    @GetMapping("/tomorrow")
    public String tomorrow(Model model){
        LessonPlan lessonPlan = lessonPlanService.getTomorrow();
        List<SchoolClass> schoolClasses = schoolClassService.getAll(); // Загрузка всех классов

        model.addAttribute("schoolClasses", schoolClasses);
        model.addAttribute("lessonPlan", lessonPlan);

        // Возврат имени шаблона (без суффикса .html)
        return "pages/Home";
    }

    @GetMapping("/update_by_date/")
    public String update(Model model) throws IOException {
        docFileComponent.updateInfoFile("src/main/resources/files/2025-01-20.docx");
        LessonPlan lessonPlan = lessonPlanService.getByDate(LocalDate.of(2025,01,20));
        List<SchoolClass> schoolClasses = schoolClassService.getAll(); // Загрузка всех классов

        model.addAttribute("schoolClasses", schoolClasses);
        model.addAttribute("lessonPlan", lessonPlan);

        return "pages/Home";
    }
}
