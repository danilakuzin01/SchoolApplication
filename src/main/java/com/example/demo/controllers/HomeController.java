package com.example.demo.controllers;

import com.example.demo.services.DocFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final DocFileService docFileService;

    @Autowired
    public HomeController(DocFileService docFileService) {
        this.docFileService = docFileService;
    }

    @GetMapping
    public String home(
//            @RequestParam(name="name", required = false, defaultValue = "ничего не написано") String name,
            Model model){
        model.addAttribute("paragraph", docFileService.getParagraph());
        model.addAttribute("classes", docFileService.getSchoolClasses());
//        model.addAttribute("name",name);
        return "pages/Home";
    }
}
