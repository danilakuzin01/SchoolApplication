package by.danilakuzin.schoolApplication.controllers;

import by.danilakuzin.schoolApplication.services.fileServices.DocFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final DocFileService docFileService;

    @Autowired
    public HomeController(DocFileService docFileService) {
        this.docFileService = docFileService;
    }

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("paragraph", docFileService.getParagraph());
        model.addAttribute("classes", docFileService.getSchoolClasses());

        // Возврат имени шаблона (без суффикса .html)
        return "pages/Home";
    }
}
