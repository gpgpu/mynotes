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
    @Produces("application/json")
    public Response saveContent(Article article){
        // check if there are updates after the record was retrieved.
        Article serverVersion = articleRepo.getArticle(article.getId());

        long clientTimeStamp = article.getModifiedOn().getTime();
        long serverTimeStamp = serverVersion.getModifiedOn().getTime();
        System.out.println("client: server timestamp: "  + clientTimeStamp + " : " + serverTimeStamp);
        if (serverTimeStamp > clientTimeStamp)
            return Response.status(Response.Status.NO_CONTENT).build();

        // if no conflict, then set new timestamp and update
        Date newModifiedOn = new Date();
        article.setModifiedOn(newModifiedOn);
        articleRepo.saveContent(article);
        return Response.status(Response.Status.OK).entity(newModifiedOn).build();
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
    public Response getLatest(@FormParam("articleId") int articleId, @FormParam("modifiedOn") long modifiedOn){

    //    Date clientModifiedOn = new Date(modifiedOn);
        Article article = articleRepo.getArticle(articleId);

        long serverTotalSeconds = article.getModifiedOn().getTime();

        System.out.println("clientModifiedOn:" + modifiedOn);
        System.out.println("serverModifiedOn:" + serverTotalSeconds);

        boolean hasNewVersion = serverTotalSeconds > modifiedOn;



       if (hasNewVersion)
           return Response.status(Response.Status.OK).entity(article).build();

        return Response.status(Response.Status.NO_CONTENT).build();
    }
}