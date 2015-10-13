package twstudio.application;


import org.springframework.beans.factory.annotation.Autowired;
import twstudio.domain.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by taowang on 10/6/15.
 */
public class PortalService {

    private PortalRepo portalRepo;
    private TopicRepo topicRepo;
    private ArticleRepo articleRepo;

    @Autowired
    public PortalService(PortalRepo portalRepo, TopicRepo topicRepo, ArticleRepo articleRepo){
        this.portalRepo = portalRepo;
        this.topicRepo = topicRepo;
        this.articleRepo = articleRepo;
    }

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


}
