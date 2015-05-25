package twstudio.web.rest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.transaction.annotation.Transactional;
import twstudio.domain.*;

import twstudio.viewmodel.DisplayOrderViewModel;

@Path("portal")
@Transactional
public class PortalService {
    @Inject PortalRepo portalRepo;
    @Inject TopicRepo topicRepo;
    @Inject ArticleRepo articleRepo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Portal> getTopics(){
        List<Portal> portals = portalRepo.getPortals();
        List<Topic> topics = topicRepo.getTopics();
        List<Article> articles = articleRepo.getArticles();

        Map<Integer, Portal> portalMap = new HashMap<Integer, Portal>();
        Map<Integer, Topic> topicMap = new HashMap<Integer, Topic>();

        for (Portal portal : portals){
            portalMap.put(portal.getId(), portal);
        }
        for (Topic topic : topics){
            topicMap.put(topic.getId(), topic);
        }

        for (Topic topic : topics){
            if (topic.getParentTopicId() == 0){
                portalMap.get(topic.getPortalId()).getTopics().add(topic);
            }
            else{
                topicMap.get(topic.getParentTopicId()).getTopics().add(topic);
            }
        }

        for (Article article : articles){
            topicMap.get(article.getTopicId()).getArticles().add(article);
        }
        return portals;
    }

    @PUT
    @Path("displayOrder")
    public String saveDisplayOrders(List<DisplayOrderViewModel> items)
            throws SQLException{

        portalRepo.saveDisplayOrders(items);
        return String.valueOf(items.size());
    }
}