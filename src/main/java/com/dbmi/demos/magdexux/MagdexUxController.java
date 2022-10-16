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

    // METHODS THAT INTERACT WITH THE API
    @RequestMapping("/findarticlebyid")
    public String findArticleById(Model aModel, HttpServletRequest request) {
        aList.clear();
        Article theArticle = new Article();
        textMessage = "NONSENSE";
        String theUri = "/find/id/";
        myLogger.debug("FIND RECORD BY ID: requested.");
        String idNum = request.getParameter("articleId");
        myLogger.debug("FIND RECORD BY ID url: " + apiLocation + "/find/id/" + idNum);
        try {
            theArticle = this.retrieveSingleArticleFromAPI(theUri,idNum);
            textMessage = "Found article id#: " + theArticle.getArticleId();
        } catch (Exception e) {
            textMessage = e.getMessage();
            myLogger.debug("FAILED TO RETRIEVE ARTICLE: " + e.getMessage());
        } // TRY-CATCH
        aList.add(theArticle);
        aModel.addAttribute("textmessage", textMessage);
        aModel.addAttribute("myarticle", theArticle);
        aModel.addAttribute("today", new Date().toString());
        aModel.addAttribute("myarticlelist",aList);
        return "index";
    } // FINDRECORDBYID(MODEL,HTTPSERVLETREQUEST,HTTPSERVLETRESPONSE)

    @RequestMapping("/findprevious")
    public String findPreviousArticleById(Model aModel, HttpServletRequest request) {
        aList.clear();
        Article theArticle = new Article();
        textMessage = "NONSENSE";
        String theUri = "/find/id/";
        myLogger.debug("FIND PREVIOUS RECORD BY ID: requested.");
        long idno = Long.parseLong(request.getParameter("articleId")) - 1; // DECREMENT ARTICLE ID BY 1
        String idNum = Long.toString(idno);
        myLogger.debug("FIND PREVIOUS RECORD BY ID url: " + apiLocation + "/find/id/" + idNum);
        try {
            theArticle = this.retrieveSingleArticleFromAPI(theUri,idNum);
            textMessage = "Found article id#: " + theArticle.getArticleId();
        } catch (Exception e) {
            textMessage = e.getMessage();
            myLogger.debug("FAILED TO RETRIEVE ARTICLE: " + e.getMessage());
        } // TRY-CATCH
        aList.add(theArticle);
        aModel.addAttribute("textmessage", textMessage);
        aModel.addAttribute("myarticle", theArticle);
        aModel.addAttribute("today", new Date().toString());
        aModel.addAttribute("myarticlelist",aList);
        return "index";
    } // FINDPREVIOUSRECORDBYID(MODEL,HTTPSERVLETREQUEST,HTTPSERVLETRESPONSE)

    @RequestMapping("/findnext")
    public String findNextArticleById(Model aModel, HttpServletRequest request) {
        aList.clear();
        Article theArticle = new Article();
        textMessage = "NONSENSE";
        String theUri = "/find/id/";
        myLogger.debug("FIND NEXT RECORD BY ID: requested.");
        long idno = Long.parseLong(request.getParameter("articleId")) + 1; // INCREMENT ARTICLE ID BY 1
        String idNum = Long.toString(idno);
        myLogger.debug("FIND NEXT RECORD BY ID url: " + apiLocation + "/find/id/" + idNum);
        try {
            theArticle = this.retrieveSingleArticleFromAPI(theUri,idNum);
            textMessage = "Found article id#: " + theArticle.getArticleId();
        } catch (Exception e) {
            textMessage = e.getMessage();
            myLogger.debug("FAILED TO RETRIEVE ARTICLE: " + e.getMessage());
        } // TRY-CATCH
        aList.add(theArticle);
        aModel.addAttribute("textmessage", textMessage);
        aModel.addAttribute("myarticle", theArticle);
        aModel.addAttribute("today", new Date().toString());
        aModel.addAttribute("myarticlelist",aList);
        return "index";
    } // FINDNEXTRECORDBYID(MODEL,HTTPSERVLETREQUEST,HTTPSERVLETRESPONSE)

    @RequestMapping("/findarticlebytitle")
    public String findArticleByTitle(Model aModel, HttpServletRequest request) {
        aList.clear();
        Article theArticle = new Article();
        textMessage = "NONSENSE";
        String theUri = "/find/title/";
        myLogger.debug("FIND RECORD BY TITLE: requested.");
        String title = request.getParameter("articleTitle");
        myLogger.debug("FIND RECORD BY TITLE url: " + apiLocation + "/find/title/" + title);
        textMessage = "Found article title: " + title;
        try {
            theArticle = this.retrieveSingleArticleFromAPI(theUri,title);
            textMessage = "Found article id#: " + theArticle.getArticleId();
        } catch (Exception e) {
            textMessage = e.getMessage();
            myLogger.debug("FAILED TO RETRIEVE ARTICLE: " + e.getMessage());
        } // TRY-CATCH
        aList.add(theArticle);
        aModel.addAttribute("textmessage", textMessage);
        aModel.addAttribute("myarticle", theArticle);
        aModel.addAttribute("today", new Date().toString());
        aModel.addAttribute("myarticlelist",aList);
        return "index";
    } // FINDRECORDBYTITLE(MODEL,HTTPSERVLETREQUEST,HTTPSERVLETRESPONSE)

    @RequestMapping("/findarticleslike")
    public String findArticlesLike(Model aModel, HttpServletRequest request) {
        myLogger.debug("FIND RECORDS LIKE: requested.");
        aList.clear();
        String theUri = "/find/like";
        Article exampleArticle;
        Object[] returnedObjects = new Object[0];
        ObjectMapper myMapper = new ObjectMapper();
        // GET THE INPUT VALUES FROM THE FORM
        exampleArticle = this.getHTTPRequestData(request);
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
        if (returnedObjects.length == 0) {
            textMessage = "No articles returned from query.";
            aList.add(new Article()); // ADD SOMETHING TO THE LIST TO RETURN TO THE PAGE
        } else { // CONVERT THE OBJECTS TO ARTICLES AND ADD TO LIST TO RETURN TO WEB PAGE
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

    @RequestMapping("/addarticle")
    public String addArticle(Model aModel) {
        aList.clear();
        String theUri = "/new";
        myLogger.debug("ADD RECORD: requested.");
        Article myArticle = new Article();
        myLogger.debug("ADD RECORD: " + myArticle.articleTitle + " -- url: " + apiLocation + theUri);
        textMessage = "READY TO ADD ARTICLE.  PLEASE ENTER DATA AND COMMIT CHANGES";
        aList.add(myArticle);
        aModel.addAttribute("textmessage", textMessage);
        aModel.addAttribute("myarticle", myArticle);
        aModel.addAttribute("today", new Date().toString());
        aModel.addAttribute("myarticlelist",aList);
        aModel.addAttribute("theUri", theUri);
        aModel.addAttribute("theMethod","POST");
        return "index";
    } // ADDRECORD(MODEL,HTTPSERVLETREQUEST,HTTPSERVLETRESPONSE)

    @RequestMapping("/updatearticle")
    public String updateArticle(Model aModel, HttpServletRequest request) {
        aList.clear();
        String theUri = "/update/" + request.getParameter("articleId");
        myLogger.debug("ADD RECORD: requested.");
        Article myArticle = new Article();
        myLogger.debug("ADD RECORD: " + myArticle.articleTitle + " -- url: " + apiLocation + theUri);
        textMessage = "READY TO UPDATE ARTICLE.  PLEASE ENTER CHANGES TO THE DATA AND COMMIT.";
        myArticle = this.getHTTPRequestData(request);
        aList.add(myArticle);
        aModel.addAttribute("textmessage", textMessage);
        aModel.addAttribute("myarticle", myArticle);
        aModel.addAttribute("today", new Date().toString());
        aModel.addAttribute("myarticlelist",aList);
        aModel.addAttribute("theUri", theUri);
        aModel.addAttribute("theMethod","PUT");
        return "index";
    } // UPDATERECORD(MODEL,HTTPSERVLETREQUEST,HTTPSERVLETRESPONSE)

    @RequestMapping("/deletearticlebyid")
    public String deleteArticleById(Model aModel, HttpServletRequest request) {
        aList.clear();
        Article delArticle;
        textMessage = "NONSENSE";
        String theUri = "/delete/id/";
        myLogger.debug("DELETE RECORD: requested.");
        String idNum = request.getParameter("articleId");
        myLogger.debug("DELETE RECORD BY ID url: " + apiLocation + "/delete/id/" + idNum);
        textMessage = "Deleted article id#: " + idNum;
        WebClient myWebClient = WebClient.create(apiLocation);
        delArticle = myWebClient.delete()
                .uri(theUri + idNum)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, error -> Mono.error(new RuntimeException("API NOT FOUND: " + error.statusCode())))
                .onStatus(HttpStatus::is5xxServerError, error -> Mono.error(new RuntimeException("SERVER ERROR: " + error.statusCode())))
                .bodyToMono(Article.class).block();
        aList.add(delArticle);
        aModel.addAttribute("textmessage", textMessage);
        aModel.addAttribute("myarticle", delArticle);
        aModel.addAttribute("today", new Date().toString());
        aModel.addAttribute("myarticlelist",aList);
        return "index";
    } // DELETERECORDBYID(MODEL,HTTPSERVLETREQUEST,HTTPSERVLETRESPONSE)

    @RequestMapping("/commitchanges")
    public String commitChanges(Model aModel, HttpServletRequest request) {
        aList.clear();
        Article myArticle;
        textMessage = " COMMIT RETURNED NULL.  PLEASE SELECT ADD/UPDATE TO PREPARE COMMIT. ";
        String theUri = request.getParameter("theUri");
        String theMethod = request.getParameter("theMethod");
        // GET THE INPUT VALUES FROM THE FORM
        myArticle = this.getHTTPRequestData(request);
        myLogger.debug("COMMIT CHANGES requested");
        myLogger.debug("COMMIT CHANGES: " + myArticle.getArticleTitle() + " -- url: " + apiLocation + theUri);
        // USE WEBCLIENT TO SEND REQUEST AND RESOLVE RESPONSE
        WebClient myWebClient = WebClient.create(apiLocation);
        Article savedArticle = new Article();
            try {
                if(theMethod.compareTo("POST") == 0) { // IF-ELSE
                    savedArticle = myWebClient.post()
                            .uri(theUri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(myArticle)
                            .retrieve()
                            .onStatus(HttpStatus::is4xxClientError, error -> Mono.error(new RuntimeException("API NOT FOUND: " + error.statusCode())))
                            .onStatus(HttpStatus::is5xxServerError, error -> Mono.error(new RuntimeException("SERVER ERROR: " + error.statusCode())))
                            .bodyToMono(Article.class).block();
                    if(savedArticle != null) textMessage = "Added article: " + savedArticle.getArticleTitle() + " -- ID: " + savedArticle.getArticleId();
                } else if(theMethod.compareTo("PUT") == 0) {
                    savedArticle = myWebClient.put()
                            .uri(theUri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(myArticle)
                            .retrieve()
                            .onStatus(HttpStatus::is4xxClientError, error -> Mono.error(new RuntimeException("API NOT FOUND: " + error.statusCode())))
                            .onStatus(HttpStatus::is5xxServerError, error -> Mono.error(new RuntimeException("SERVER ERROR: " + error.statusCode())))
                            .bodyToMono(Article.class).block();
                    if(savedArticle != null) textMessage = "Updated article: " + savedArticle.getArticleTitle() + " -- ID: " + savedArticle.getArticleId();
                } // IF-ELSE
            } catch (RuntimeException runtimeException) {
                textMessage = "Error while committing record: " + runtimeException.getMessage();
                myLogger.debug("Error while committing changes: " + runtimeException.getMessage());
            } // TRY-CATCH
        aList.add(savedArticle);
        aModel.addAttribute("textmessage", textMessage);
        aModel.addAttribute("myarticle", savedArticle);
        aModel.addAttribute("today", new Date().toString());
        aModel.addAttribute("myarticlelist",aList);
        return "index";
    } // COMMITCHANGES(MODEL,HTTPSERVLETREQUEST,HTTPSERVLETRESPONSE)

    // UTILITY METHODS

    @RequestMapping("/clearform")
    public String clearForm(Model aModel) {
        aList.clear();
        Article theArticle = new Article();
        textMessage = "FORM CLEARED.";
        aList.add(theArticle);
        aModel.addAttribute("textmessage", textMessage);
        aModel.addAttribute("myarticle", theArticle);
        aModel.addAttribute("today", new Date().toString());
        aModel.addAttribute("myarticlelist",aList);
        return "index";
    } // CLEARFORM(MODEL,HTTPSERVLETREQUEST)

    private Article getHTTPRequestData(HttpServletRequest request) {
        Article article = new Article();
        article.setArticleId(Integer.parseInt(request.getParameter("articleId")));
        article.setArticleTitle(request.getParameter("articleTitle"));
        article.setArticleAuthor(request.getParameter("articleAuthor"));
        article.setArticleSynopsis(request.getParameter("articleSynopsis"));
        article.setArticleCategory(request.getParameter("articleCategory"));
        article.setArticleKeywords(request.getParameter("articleKeywords"));
        article.setArticleMonth(Integer.parseInt(request.getParameter("articleMonth")));
        article.setArticleYear(Integer.parseInt(request.getParameter("articleYear")));
        return article;
    } // GETHTTPREQUESTDATA(HTTPSERVLETREQUEST)

    private Article retrieveSingleArticleFromAPI(String aUri, String anIdNum) throws Exception {
        Article anArticle;
        Article errorArticle = new Article();
        errorArticle.setArticleTitle("ERROR: NO ARTICLE RETRIEVED FROM QUERY.");
        WebClient myWebClient = WebClient.create(apiLocation);
        anArticle = myWebClient.get()
                .uri(aUri + anIdNum)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Article.class)
                .timeout(Duration.ofMillis(5000))
                .doOnError(error -> textMessage = "ERROR: Unable to find requested article id#: " + anIdNum + " : " + error.getMessage())
                .onErrorReturn(errorArticle).block();
        if(textMessage.contains("ERROR:")) throw new Exception(textMessage);
        return anArticle;
    } // RETRIEVESINGLEARTICLEDATA(STRING,STRING)

} // CLASS
