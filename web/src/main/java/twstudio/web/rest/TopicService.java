package twstudio.web.rest;

import java.sql.SQLException;

import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import twstudio.domain.TopicRepo;
import twstudio.domain.Topic;

@Path("topic")
public class TopicService {
    @Inject TopicRepo topicRepo;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public int addTopic(Topic topic) throws SQLException{
        int newId = topicRepo.add(topic);
        return newId;
    }

    @PUT
    @Path("title")
    public String updateTitle(Topic topic){
        topicRepo.updateTitle(topic);
        return "OK";
    }
    @PUT
    @Path("parent")
    public String changeParent(Topic topic){
        topicRepo.changeParent(topic);
        return "OK";
    }
}