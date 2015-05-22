package twstudio.web.rest;

import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
/**
 * Created by Tao on 5/21/2015.
 */

@Path("portal")
public class PortalService {

    @GET
    public String getString(){
        return "haha";
    }
}
