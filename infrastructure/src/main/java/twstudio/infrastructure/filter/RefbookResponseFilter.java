package twstudio.infrastructure.filter;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import org.twstudio.infrastructure.RefbookHttpHeaderNames;

@Provider
@PreMatching
public class RefbookResponseFilter implements ContainerResponseFilter {

	@Override
	public void filter(ContainerRequestContext requestCtx,
			ContainerResponseContext responseCtx) throws IOException {

		// You may further limit certain client IPs with Access-Control-Allow-Origin instead of '*'
//		responseCtx.getHeaders().add( "Access-Control-Allow-Origin", "*" );    
//        responseCtx.getHeaders().add( "Access-Control-Allow-Credentials", "true" );
        responseCtx.getHeaders().add( "Access-Control-Allow-Methods", "GET, POST, DELETE, PUT" );
        responseCtx.getHeaders().add( "Access-Control-Allow-Headers", RefbookHttpHeaderNames.AUTH_TOKEN);
	}
}
