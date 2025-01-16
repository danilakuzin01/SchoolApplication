package by.danilakuzin.schoolApplication.controllers;

import by.danilakuzin.schoolApplication.services.fileServices.DocFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.logging.Logger;

@Controller
public class HomeController {
    private final DocFileService docFileService;
    private final Logger LOGGER = Logger.getLogger(HomeController.class.getName());

    @Autowired
    public HomeController(DocFileService docFileService) {
        this.docFileService = docFileService;
    }

    @GetMapping("/")
    public String home(Model model) {
//        LOGGER.info(docFileService.getParagraph());
//        LOGGER.info(docFileService.getSchoolClasses().toString());

//        model.addAttribute("paragraph", docFileService.getParagraph());
//        model.addAttribute("classes", docFileService.getSchoolClasses());

        // Возврат имени шаблона (без суффикса .html)
        return "pages/Home";
    }
}
