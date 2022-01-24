package com.dbmi.demos.magdexux;

import java.util.Date;
import java.util.HashMap;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MagdexController {
    private final Logger myLogger = LoggerFactory.getLogger(MagdexController.class);
    private String textMessage    = "Text message goes here.";
    private String apiUrl;
    private Gson   myGson         = new Gson();
    private HashMap<String,String> myHashMap = new HashMap<>();
    private String jsonString = "{\n" +
            "    \"articleId\": 7,\n" +
            "    \"articleTitle\": \"Alaska Burning\",\n" +
            "    \"articleAuthor\": \"Randi Jandt and Alison York\",\n" +
            "    \"articleSynopsis\": \"Wildfire is transforming the landscape of the high north and amplifying climate change\",\n" +
            "    \"articleCategory\": \"Climate Change\",\n" +
            "    \"articleKeywords\": \"global warming climate wildfire ecology\",\n" +
            "    \"articleMonth\": 10,\n" +
            "    \"articleYear\": 2021\n" +
            "}";

    @RequestMapping("/")
    public String home(Model aModel, HttpServletRequest request) {
        Article myArticle = new Article();
        myLogger.trace("Requested root page.");
        request.getSession().invalidate();
        request.getSession(true);
        textMessage = "Welcome to the Magazine Article Index Entry Form. Add / Search / Find / Update magazine article references here.";
        myArticle = myGson.fromJson(jsonString,Article.class);
        aModel.addAttribute("myarticle",myArticle);
        aModel.addAttribute("textmessage",textMessage);
        aModel.addAttribute("today", new Date().toString());
        return "index";
    } // HOME

    @RequestMapping("/newerror")
    public String error(Model aModel, HttpServletRequest request, String errorMessage) {
        myLogger.trace("NEWERROR: requested.");
        aModel.addAttribute("textmessage", textMessage);
        aModel.addAttribute("today", new Date().toString());
        return "error";
    } // ERROR(MODEL,HTTPSERVLETREQUEST,HTTPSERVLETRESPONSE)

    @RequestMapping("/findrecordbyid")
    public String findRecordById(Model aModel, HttpServletRequest request, String errorMessage) {
        myLogger.trace("FINDRECORDBYID: requested.");
        String idNum = request.getParameter("articleId");
        apiUrl = "http://dans-mbp:8080/magdex/articles/find/id/" + idNum;
        myLogger.trace("FINDRECORDBYID url: " + apiUrl);
        WebClient myWebClient = WebClient.create(apiUrl);
        Mono<Article> entityMono = myWebClient.get()
                .retrieve()
                .bodyToMono(Article.class)
                .onErrorReturn(new Article());
        Article myArticle = entityMono.block();  // HOLY CRAP, IT WORKED!! :D
        textMessage = "This is find by id.";
        aModel.addAttribute("textmessage", textMessage);
        aModel.addAttribute("myarticle", myArticle);
        aModel.addAttribute("today", new Date().toString());
        return "index";
    } // ERROR(MODEL,HTTPSERVLETREQUEST,HTTPSERVLETRESPONSE)

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("application");  // basename = name of file that contains the properties, e.g., application.properties
        return messageSource;
    } // MESSAGESOURCE()

} // CLASS
