package twstudio.web.filter;

/**
 * Created by taowang on 5/22/15.
 */
import jersey.repackaged.com.google.common.cache.Cache;
import jersey.repackaged.com.google.common.cache.CacheBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.security.GeneralSecurityException;
import java.util.concurrent.TimeUnit;
import javax.security.auth.login.LoginException;

public class Authenticator {
    private static Authenticator authenticator = null;
    private final Map<String, String> userLookups = new HashMap<String, String>();
    private final Cache<String, String> tokenLookups = CacheBuilder.newBuilder()
            .expireAfterAccess(8, TimeUnit.HOURS)
            .build();



    private Authenticator(){
        userLookups.put("stdio", "tw0118bz");
    }
    public static Authenticator getInstance(){
        if (authenticator == null)
            authenticator = new Authenticator();
        return authenticator;
    }
    public String login(String userName, String password)throws LoginException{
        if (userLookups.containsKey(userName)){
            String passwordMatch = userLookups.get(userName);
            if (passwordMatch.equals(password)){
                String authToken = UUID.randomUUID().toString();
                tokenLookups.put(authToken, userName);
                return authToken;
            }
        }
        throw new LoginException("Login failed");
    }
    public void logout(String authToken) throws GeneralSecurityException{
        if (tokenLookups.getIfPresent(authToken) != null){
            tokenLookups.invalidate(authToken);

            return;
        }
        throw new GeneralSecurityException("Invalid token");
    }
    public boolean isAuthTokenValid(String token){
        if (tokenLookups.getIfPresent(token) != null)
            return true;
        return false;
    }

}