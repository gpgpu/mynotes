package twstudio.infrastructure.filter;

import org.twstudio.model.Authenticator;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.twstudio.infrastructure.RefbookHttpHeaderNames;

@Provider
@PreMatching
public class RefbookRequestFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {
		if (requestContext.getRequest().getMethod().equals("OPTIONS")){
			requestContext.abortWith(Response.status(Response.Status.OK)
					.build());
			return;
		}
		
		Authenticator authenticator = Authenticator.getInstance();
		String path = requestContext.getUriInfo().getPath();
		
		// for any request beside login, token must be verified
		if (path.endsWith("login")) return;
		if (path.contains("unsecured")) return;		
			String token = requestContext.getHeaderString(RefbookHttpHeaderNames.AUTH_TOKEN);
			if (!authenticator.isAuthTokenValid(token)){
				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
			}
	}
}
