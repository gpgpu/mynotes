package twstudio.web.filter;

import org.springframework.stereotype.Component;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import twstudio.web.common.RefbookHttpHeaderNames;


@Provider
@PreMatching
public class RefbookRequestFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {
		if (requestContext.getMethod().equals("OPTIONS")){
			requestContext.abortWith(Response.status(Response.Status.OK)
					.build());
			return;
		}
		
		Authenticator authenticator = Authenticator.getInstance();
		String path = requestContext.getUriInfo().getPath();
		
		// token must be verified unless excluded explicitly.
		// todo: decouple exclusion list
		if (path.endsWith("login")) return;
		if (path.contains("unsecured")) return;		
			String token = requestContext.getHeaderString(RefbookHttpHeaderNames.AUTH_TOKEN);
			if (!authenticator.isAuthTokenValid(token)){
				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
			}


	}
}
