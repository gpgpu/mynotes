package twstudio.web.rest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import twstudio.domain.Article;
import twstudio.domain.ArticleRepo;

@Path("article")
public class ArticleService {
    @Inject ArticleRepo articleRepo;

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
}