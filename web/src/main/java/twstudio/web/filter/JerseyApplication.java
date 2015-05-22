package twstudio.web.filter;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;
 
import org.glassfish.jersey.media.multipart.MultiPartFeature;
 

public class JerseyApplication extends Application {
	 
		@Override
		public Set<Class<?>> getClasses() {
			Set<Class<?>> resources = new HashSet<Class<?>>();
			resources.add(MultiPartFeature.class);
			return resources;
		}
	}
