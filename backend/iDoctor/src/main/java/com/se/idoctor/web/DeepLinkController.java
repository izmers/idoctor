package com.se.idoctor.web;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;

@Controller
public class DeepLinkController {

    @GetMapping("/reset-password/{email}")
    public void redirectToApp(@PathVariable String email, HttpServletResponse response) throws IOException {
        String deepLink = "myapp://reset-password/" + email;

        response.sendRedirect(deepLink);
    }
}