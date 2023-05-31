package com.magadiflo.secured.webapp.controllers;

import com.magadiflo.secured.webapp.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageController {

    @Autowired
    private ProductService productService;

    @GetMapping(path = "/main")
    public String main(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("products", this.productService.findAll());
        return "main.html";
    }

}
