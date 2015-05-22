package twstudio.web.rest;

/**
 * Created by taowang on 5/22/15.
 */
import javax.security.auth.login.LoginException;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.CacheControl;
import twstudio.web.filter.Authenticator;

@Path("login")
public class AuthService {
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(
            @Context HttpHeaders headers,
            @FormParam("username") String username,
            @FormParam("password") String password){
        Authenticator authenticator = Authenticator.getInstance();
        try{
            String token = authenticator.login(username, password);
            String result = "{\"token\": \"" + token + "\"}";
            return getNoCacheResponseBuilder(Response.Status.OK).entity(result).build();

        } catch (final LoginException ex){
            return getNoCacheResponseBuilder(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\": \"Wrong username or password\"}").build();
        }


    }
    private Response.ResponseBuilder getNoCacheResponseBuilder(Response.Status status){
        CacheControl cc = new CacheControl();
        cc.setNoCache(true);
        cc.setMaxAge(-1);
        cc.setMustRevalidate(true);

        return Response.status(status).cacheControl(cc);
    }

}