package twstudio.web.filter;

/**
 * Created by taowang on 5/22/15.
 */
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.security.GeneralSecurityException;
import javax.security.auth.login.LoginException;

public class Authenticator {
    private static Authenticator authenticator = null;
    private final Map<String, String> userLookups = new HashMap<String, String>();
    private final Map<String, String> tokenLookups = new HashMap<String, String>();



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
        if (tokenLookups.containsKey(authToken)){
            tokenLookups.remove(authToken);
            return;
        }
        throw new GeneralSecurityException("Invalid token");
    }
    public boolean isAuthTokenValid(String token){
        if (tokenLookups.containsKey(token))
            return true;
        return false;
    }

}