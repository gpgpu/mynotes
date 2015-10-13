package twstudio.application;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import twstudio.domain.ArticleRepo;
import twstudio.domain.Portal;
import twstudio.domain.PortalRepo;
import twstudio.domain.TopicRepo;

import java.util.ArrayList;
import java.util.List;

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
    public void testStub(){

        ArrayList<Portal> portals = new ArrayList<Portal>();
        Portal portal = new Portal();
 //       portal.setId(1);

        portals.add(portal);

        when(portalRepo.getPortals()).thenReturn(portals);

        List<Portal> result = portalService.getTopics();

        assertThat(result.size(), equalTo(0));

    }
}
