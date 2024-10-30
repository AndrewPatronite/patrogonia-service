package com.patronite.service.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {
    @GetMapping(value = "/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String res = status != null && Integer.parseInt(status.toString()) == HttpStatus.NOT_FOUND.value() ?
                "404" : "uh-oh";
        return String.format("redirect:/error/%s", res);
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
