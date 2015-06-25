package twstudio.web.rest;

import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

import javax.ws.rs.*;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.spi.LoggerFactory;

import org.eclipse.jetty.util.log.Log;
import twstudio.domain.Article;
import twstudio.domain.ArticleRepo;

@Path("article")
public class ArticleService {
    @Inject ArticleRepo articleRepo;
    private Logger logger = Logger.getLogger("ha");
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getArticle(@PathParam("id") int id){
        Article article = articleRepo.getArticle(id);
        return Response.status(Response.Status.OK).entity(article).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public int add(Article article) throws SQLException{ // will add try catch later
        return articleRepo.add(article);
    }

    @PUT
    @Path("title")
    public String updateTitle(Article article){
        articleRepo.updateTitle(article);
        return "OK";
    }
    @PUT
    @Path("content")
    public String saveContent(Article article){
        articleRepo.saveContent(article);
        return "OK";
    }

    @PUT
    @Path("parent")
    public String changeParent(Article article){
        articleRepo.changeParent(article);
        return "OK";
    }

    @POST
    @Path("sync")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLatest(@FormParam("articleId") int articleId, @FormParam("modifiedOn") Date modifiedOn){
        logger.info("getLatest...");
        logger.info("articleId: " + articleId);
        logger.info("modifiedOn: "  + modifiedOn);
        Article article = articleRepo.getArticle(articleId);

        int compareResult = article.getModifiedOn().compareTo(modifiedOn);

        if (compareResult <= 0) // if it's the same or even older
            return Response.status(Response.Status.NO_CONTENT).build();

        return Response.status(Response.Status.OK).entity(article).build();
    }
}