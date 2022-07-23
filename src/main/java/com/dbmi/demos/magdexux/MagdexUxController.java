package com.dbmi.demos.magdexux;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MagdexUxController {
    private final Logger myLogger = LoggerFactory.getLogger(MagdexUxController.class);
    private String textMessage = "Text message goes here.";
    @Value("${magdex.api.location}")
    private String apiLocation;
    private List<Article> aList = new Stack<>();

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("application");  // basename = name of file that contains the properties, e.g., application.properties
        return messageSource;
    } // MESSAGESOURCE()

    @RequestMapping("/")
    public String home(Model aModel, HttpServletRequest request) {
        textMessage = "Form cleared.";
        aList.clear();
        myLogger.debug("Requested root page.");
        request.getSession().invalidate();
        request.getSession(true);
        if(request.getSession().isNew()) {
            textMessage = "Welcome to the Magazine Article Index Entry Form. Add / Search / Find / Update magazine article references here.";
        } // IF
        aModel.addAttribute("myarticle", new Article());
        aModel.addAttribute("textmessage", textMessage);
        aModel.addAttribute("today", new Date().toString());
        aModel.addAttribute("myarticlelist",aList);
        return "index";
    } // HOME(MODEL,HTTPSERVLETREQUEST)

    @RequestMapping("/findrecordbyid")
    public String findRecordById(Model aModel, HttpServletRequest request) {
        aList.clear();
        Article theArticle;
        String theUri = "/find/id/";
        myLogger.debug("FIND RECORD BY ID: requested.");
        String idNum = request.getParameter("articleId");
        myLogger.debug("FIND RECORD BY ID url: " + apiLocation + "/find/id/" + idNum);
        textMessage = "Found article id#: " + idNum;
        WebClient myWebClient = WebClient.create(apiLocation);
        theArticle = myWebClient.get()
                .uri(theUri + idNum)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Article.class)
                .timeout(Duration.ofMillis(5000))
                .doOnError(error -> textMessage = "Unable to find requested article id#: " + idNum + " error: " + error.getMessage())
                .onErrorReturn(new Article()).block();
        aList.add(theArticle);
        aModel.addAttribute("textmessage", textMessage);
        aModel.addAttribute("myarticle", theArticle);
        aModel.addAttribute("today", new Date().toString());
        aModel.addAttribute("myarticlelist",aList);
        return "index";
    } // FINDRECORDBYID(MODEL,HTTPSERVLETREQUEST,HTTPSERVLETRESPONSE)

    @RequestMapping("/findrecordbytitle")
    public String findRecordByTitle(Model aModel, HttpServletRequest request) {
        aList.clear();
        Article theArticle;
        String theUri = "/find/title/";
        myLogger.debug("FIND RECORD BY TITLE: requested.");
        String title = request.getParameter("articleTitle");
        myLogger.debug("FIND RECORD BY TITLE url: " + apiLocation + "/find/title/" + title);
        textMessage = "Found article title: " + title;
        WebClient myWebClient = WebClient.create(apiLocation);
        theArticle = myWebClient.get()
                .uri(theUri + title)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Article.class)
                .timeout(Duration.ofMillis(5000))
                .doOnError(error -> textMessage = "Unable to find requested article title#: " + title + " error: " + error.getMessage())
                .onErrorReturn(new Article()).block();
        aList.add(theArticle);
        aModel.addAttribute("textmessage", textMessage);
        aModel.addAttribute("myarticle", theArticle);
        aModel.addAttribute("today", new Date().toString());
        aModel.addAttribute("myarticlelist",aList);
        return "index";
    } // FINDRECORDBYID(MODEL,HTTPSERVLETREQUEST,HTTPSERVLETRESPONSE)

    @RequestMapping("/findrecordslike")
    public String findRecordsLike(Model aModel, HttpServletRequest request) {
        myLogger.debug("FIND RECORDS LIKE: requested.");
        aList.clear();
        String theUri = "/find/like";
        Article exampleArticle = new Article();
        Object[] returnedObjects = new Object[1];
        ObjectMapper myMapper = new ObjectMapper();
        // GET THE INPUT VALUES FROM THE FORM
        exampleArticle.articleTitle = request.getParameter("articleTitle");
        exampleArticle.articleAuthor = request.getParameter("articleAuthor");
        exampleArticle.articleSynopsis = request.getParameter("articleSynopsis");
        exampleArticle.articleCategory = request.getParameter("articleCategory");
        exampleArticle.articleKeywords = request.getParameter("articleKeywords");
        exampleArticle.articleMonth = Integer.parseInt(request.getParameter("articleMonth"));
        exampleArticle.articleYear = Integer.parseInt(request.getParameter("articleYear"));
        myLogger.debug("FIND RECORDS LIKE: " + exampleArticle.articleTitle + " -- url: " + apiLocation + theUri);
        // USE WEBCLIENT TO SEND REQUEST AND RESOLVE RESPONSE
        WebClient myWebClient = WebClient.create(apiLocation);
        try {
            Mono<Object[]> myMono = myWebClient.post()
                    .uri(theUri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(exampleArticle)
                    .retrieve()
                    .bodyToMono(Object[].class)
                    .timeout(Duration.ofMillis(5000))
                    .doOnError(throwable -> textMessage = "System error finding values: " + throwable.getMessage())
                    .onErrorReturn(returnedObjects)
                    .log();
            returnedObjects = myMono.block();
        } catch (WebClientRequestException wre) {
            textMessage = "Error in web client: " + wre.getMessage();
        } catch (RuntimeException runtimeException) {
            textMessage = "Error finding records like: " + runtimeException.getMessage();
        } // TRY-CATCH
        if (returnedObjects[0] == null) {
            textMessage = "No articles returned from query.";
        } else {
            aList = Arrays.stream(returnedObjects)
                    .map(object -> myMapper.convertValue(object, Article.class))
                    .collect(Collectors.toList());
            textMessage = "Found " + aList.size() + " records like query.";
        } // IF-ELSE
        aModel.addAttribute("textmessage", textMessage);
        aModel.addAttribute("myarticle", aList.get(0));
        aModel.addAttribute("today", new Date().toString());
        aModel.addAttribute("myarticlelist",aList);
        return "index";
    } // FINDRECORDSLIKE(MODEL,HTTPSERVLETREQUEST,HTTPSERVLETRESPONSE)

    @RequestMapping("/addrecord")
    public String addRecord(Model aModel, HttpServletRequest request) {
        aList.clear();
        String theUri = "/new";
        myLogger.debug("ADD RECORD: requested.");
        Article myArticle = new Article();
        // GET THE INPUT VALUES FROM THE FORM
        myArticle.articleTitle = request.getParameter("articleTitle");
        myArticle.articleAuthor = request.getParameter("articleAuthor");
        myArticle.articleSynopsis = request.getParameter("articleSynopsis");
        myArticle.articleCategory = request.getParameter("articleCategory");
        myArticle.articleKeywords = request.getParameter("articleKeywords");
        myArticle.articleMonth = Integer.parseInt(request.getParameter("articleMonth"));
        myArticle.articleYear = Integer.parseInt(request.getParameter("articleYear"));
        myLogger.debug("ADD RECORD: " + myArticle.articleTitle + " -- url: " + apiLocation + theUri);
        // USE WEBCLIENT TO SEND REQUEST AND RESOLVE RESPONSE
        WebClient myWebClient = WebClient.create(apiLocation);
        Article savedArticle = new Article();
        try {
            savedArticle = myWebClient.post()
                    .uri(theUri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(myArticle)
                    .retrieve()
                    .onStatus(HttpStatus::is5xxServerError, error -> Mono.error(new RuntimeException("SERVER ERROR: " + error.statusCode())))
                    .bodyToMono(Article.class).block();
            textMessage = "Added article: " + savedArticle.getArticleTitle() + " -- ID: " + savedArticle.getArticleId();
        } catch (RuntimeException runtimeException) {
            textMessage = "Error while adding record: " + runtimeException.getMessage();
        } // TRY-CATCH
        aList.add(savedArticle);
        aModel.addAttribute("textmessage", textMessage);
        aModel.addAttribute("myarticle", savedArticle);
        aModel.addAttribute("today", new Date().toString());
        aModel.addAttribute("myarticlelist",aList);
        return "index";
    } // ADDRECORD(MODEL,HTTPSERVLETREQUEST,HTTPSERVLETRESPONSE)

    @RequestMapping("/updaterecord")
    public String updateRecord(Model aModel, HttpServletRequest request) {
        aList.clear();
        String theUri = "/update/";
        myLogger.debug("UPDATE RECORD BY ID: requested.");
        Article myArticle = new Article();
        String idNum = request.getParameter("articleId");
        myLogger.debug("UPDATE RECORD BY ID url: " + apiLocation + "/find/id/" + idNum);
        // GET THE INPUT VALUES FROM THE FORM
        myArticle.articleTitle = request.getParameter("articleTitle");
        myArticle.articleAuthor = request.getParameter("articleAuthor");
        myArticle.articleSynopsis = request.getParameter("articleSynopsis");
        myArticle.articleCategory = request.getParameter("articleCategory");
        myArticle.articleKeywords = request.getParameter("articleKeywords");
        myArticle.articleMonth = Integer.parseInt(request.getParameter("articleMonth"));
        myArticle.articleYear = Integer.parseInt(request.getParameter("articleYear"));
        myLogger.debug("UPDATE RECORD: " + myArticle.articleTitle + " -- url: " + apiLocation + theUri);
        // USE WEBCLIENT TO SEND REQUEST AND RESOLVE RESPONSE
        WebClient myWebClient = WebClient.create(apiLocation);
        Article savedArticle = new Article();
        try {
            savedArticle = myWebClient.put()
                    .uri(theUri + idNum)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(myArticle)
                    .retrieve()
                    .onStatus(HttpStatus::is5xxServerError, error -> Mono.error(new RuntimeException("SERVER ERROR: " + error.statusCode())))
                    .bodyToMono(Article.class).block();
            textMessage = "Updated article: " + savedArticle.getArticleTitle() + " -- ID: " + savedArticle.getArticleId();
        } catch (RuntimeException runtimeException) {
            textMessage = "Error while updating record: " + runtimeException.getMessage();
        } // TRY-CATCH
        aList.add(savedArticle);
        aModel.addAttribute("textmessage", textMessage);
        aModel.addAttribute("myarticle", savedArticle);
        aModel.addAttribute("today", new Date().toString());
        aModel.addAttribute("myarticlelist",aList);
        return "index";
    } // UPDATERECORD(MODEL,HTTPSERVLETREQUEST,HTTPSERVLETRESPONSE)

    @RequestMapping("/deleterecordbyid")
    public String deleteRecordById(Model aModel, HttpServletRequest request) {
        aList.clear();
        Article delArticle;
        String theUri = "/delete/id/";
        myLogger.debug("DELETE RECORD: requested.");
        String idNum = request.getParameter("articleId");
        myLogger.debug("DELETE RECORD BY ID url: " + apiLocation + "/delete/id/" + idNum);
        textMessage = "Deleted article id#: " + idNum;
        WebClient myWebClient = WebClient.create(apiLocation);
        delArticle = myWebClient.delete()
                .uri(theUri + idNum)
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, error -> Mono.error(new RuntimeException("SERVER ERROR: " + error.statusCode())))
                .bodyToMono(Article.class).block();
        aList.add(delArticle);
        aModel.addAttribute("textmessage", textMessage);
        aModel.addAttribute("myarticle", delArticle);
        aModel.addAttribute("today", new Date().toString());
        aModel.addAttribute("myarticlelist",aList);
        return "index";
    } // DELETERECORDBYID(MODEL,HTTPSERVLETREQUEST,HTTPSERVLETRESPONSE)
} // CLASS
