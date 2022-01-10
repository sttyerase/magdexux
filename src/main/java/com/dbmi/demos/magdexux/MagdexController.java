package com.dbmi.demos.magdexux;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MagdexController {
    private final Logger myLogger = LoggerFactory.getLogger(MagdexController.class);

    @RequestMapping("/")
    public String home(Model aModel, HttpServletRequest request) {
        myLogger.trace("Requested root page.");
        request.getSession().invalidate();
        request.getSession(true);
        aModel.addAttribute("today", new Date().toString());
        return "index";
    } // HOME

    @RequestMapping("/newerror")
    public String error(Model aModel, HttpServletRequest request, String errorMessage) {
        myLogger.trace("NEWERROR: requested.");
        aModel.addAttribute("err", errorMessage);
        aModel.addAttribute("today", new Date().toString());
        return "error";
    } // ERROR(MODEL,HTTPSERVLETREQUEST,HTTPSERVLETRESPONSE)

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("application");  // basename = name of file that contains the properties, e.g., application.properties
        return messageSource;
    } // MESSAGESOURCE()

} // CLASS
