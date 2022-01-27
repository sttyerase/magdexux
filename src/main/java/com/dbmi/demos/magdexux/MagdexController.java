package com.dbmi.demos.magdexux;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
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
    @Value("${magdex.api.location}")
    private String apiLocation;

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("application");  // basename = name of file that contains the properties, e.g., application.properties
        return messageSource;
    } // MESSAGESOURCE()

    @RequestMapping("/")
    public String home(Model aModel, HttpServletRequest request) {
        myLogger.trace("Requested root page.");
        request.getSession().invalidate();
        request.getSession(true);
        textMessage = "Welcome to the Magazine Article Index Entry Form. Add / Search / Find / Update magazine article references here.";
        // myArticle = myGson.fromJson(jsonString,Article.class); // EXAMPLE USING GSON
        aModel.addAttribute("myarticle",new Article());
        aModel.addAttribute("textmessage",textMessage);
        aModel.addAttribute("today", new Date().toString());
        return "index";
    } // HOME

    @RequestMapping("/newerror")
    public String error(Model aModel, HttpServletRequest request, String errorMessage) {
        myLogger.trace("NEWERROR: requested.");
        textMessage = request.getParameter("errormessage" + errorMessage);
        aModel.addAttribute("textmessage", textMessage);
        aModel.addAttribute("today", new Date().toString());
        return "error";
    } // ERROR(MODEL,HTTPSERVLETREQUEST,HTTPSERVLETRESPONSE)

    @RequestMapping("/findrecordbyid")
    public String findRecordById(Model aModel, HttpServletRequest request) {
        myLogger.trace("FIND RECORD BY ID: requested.");
        String idNum = request.getParameter("articleId");
        myLogger.trace("FIND RECORD BY ID url: " + apiLocation + "/find/id/" + idNum);
        textMessage = "Found article id#: " + idNum;
        WebClient myWebClient = WebClient.create(apiLocation);
        Mono<Article> entityMono = myWebClient.get()
                .uri("/find/id/" + idNum)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Article.class)
                .doOnError(error -> textMessage = "Unable to find requested article: " + error.getMessage())
                .onErrorReturn(new Article());
        Article myArticle = entityMono.block();  // HOLY CRAP, IT WORKED!! :D
        aModel.addAttribute("textmessage", textMessage);
        aModel.addAttribute("myarticle", myArticle);
        aModel.addAttribute("today", new Date().toString());
        return "index";
    } // ERROR(MODEL,HTTPSERVLETREQUEST,HTTPSERVLETRESPONSE)

    @RequestMapping("/addrecord")
    public String addRecord(Model aModel, HttpServletRequest request) {
        myLogger.trace("ADD RECORD: requested.");
        Article myArticle = new Article();
        // GET THE INPUT VALUES FROM THE FORM
        myArticle.articleTitle = request.getParameter("articleTitle");
        myArticle.articleAuthor = request.getParameter("articleAuthor");
        myArticle.articleSynopsis = request.getParameter("articleSynopsis");
        myArticle.articleCategory = request.getParameter("articleCategory");
        myArticle.articleKeywords = request.getParameter("articleKeywords");
        myArticle.articleMonth = Integer.parseInt(request.getParameter("articleMonth"));
        myArticle.articleYear = Integer.parseInt(request.getParameter("articleYear"));
        myLogger.trace("Article inputs: " + myArticle.articleTitle);
        myLogger.trace("ADD RECORD url: " + apiLocation + "/new");
        textMessage = "Added article: " + myArticle.articleTitle;
        WebClient myWebClient = WebClient.create(apiLocation);
        Mono<Void> entityMono = myWebClient.post()
                .uri("/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(myArticle)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(error -> textMessage = "Unable to add new article: " + error.getMessage());
        entityMono.block();
        aModel.addAttribute("textmessage", textMessage);
        aModel.addAttribute("myarticle", myArticle);
        aModel.addAttribute("today", new Date().toString());
        return "index";
    } // ERROR(MODEL,HTTPSERVLETREQUEST,HTTPSERVLETRESPONSE)

} // CLASS
