package twstudio.web;

/**
 * Created by Tao on 5/21/2015.
 */

import org.glassfish.jersey.server.ResourceConfig;
import javax.ws.rs.ApplicationPath;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import twstudio.web.filter.*;

@Component
@ApplicationPath("/rest")
public class JerseyConfig extends ResourceConfig  {
    public JerseyConfig(){
        packages("twstudio.web.rest");
        register(RefbookRequestFilter.class);
        register(RefbookResponseFilter.class);
        register(org.glassfish.jersey.media.multipart.MultiPartFeature.class);
    }
}
