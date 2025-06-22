package com.bookexchange.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class LoginController {

    @Value("${client.url}")
    private String clientUrl;

    @GetMapping("/login")
    public RedirectView login() {
        return new RedirectView(clientUrl);
    }
} 