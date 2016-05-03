package twstudio.application;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import twstudio.domain.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Created by taowang on 10/8/15.
 */
public class TestPortalService {

    private PortalService portalService;
    private PortalRepo portalRepo;
    private TopicRepo topicRepo;
    private ArticleRepo articleRepo;

    @Before
    public void init(){
        portalRepo = Mockito.mock(PortalRepo.class);
        topicRepo = Mockito.mock(TopicRepo.class);
        articleRepo = Mockito.mock(ArticleRepo.class);

        portalService = new PortalService(portalRepo, topicRepo, articleRepo);
    }


    @Test
    public void putArticles(){
        Map<Integer, Topic> topicTree = new HashMap<Integer, Topic>();

        Topic topic1 = new Topic();
        topic1.setId(1);

        Topic topic2 = new Topic();
        topic2.setId(2);

        topicTree.put(topic1.getId(), topic1);
        topicTree.put(topic2.getId(), topic2);

        List<Article> articles = new ArrayList<>();
        Article article1 = new Article();
        article1.setTopicId(1);
        article1.setId(1);
        Article article2 = new Article();
        article2.setTopicId(1);
        article2.setId(2);
        Article article3 = new Article();
        article3.setTopicId(2);
        article3.setId(3);

        articles.add(article1);
        articles.add(article2);
        articles.add(article3);

        // act
        portalService.putArticlesIntoHierarchy(topicTree, articles);

        // assert
        assertEquals(2, topicTree.size());
        assertEquals(2, topicTree.get(1).getArticles().size());
        assertEquals(1, topic2.getArticles().size());
    }

    @Test
    public void putTopics(){
        Map<Integer, Topic> topicTree = new HashMap<Integer, Topic>();

        Topic topic1 = new Topic();
        topic1.setId(1);

        Topic topic2 = new Topic();
        topic2.setId(2);

        topicTree.put(topic1.getId(), topic1);
        topicTree.put(topic2.getId(), topic2);

        List<Article> articles = new ArrayList<Article>();
        Article article1 = new Article();
        article1.setTopicId(1);
        article1.setId(1);
        Article article2 = new Article();
        article2.setTopicId(1);
        article2.setId(2);
        Article article3 = new Article();
        article3.setTopicId(2);
        article3.setId(3);

        articles.add(article1);
        articles.add(article2);
        articles.add(article3);

        // act
        portalService.putArticlesIntoHierarchy(topicTree, articles);

        // assert
        assertEquals(2, topicTree.size());
        assertEquals(2, topicTree.get(1).getArticles().size());
        assertEquals(1, topic2.getArticles().size());
    }
}
