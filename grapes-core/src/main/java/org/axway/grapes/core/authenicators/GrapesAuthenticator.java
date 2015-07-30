package org.axway.grapes.core.authenicators;
//todo if you dont put basic auth you dont get the popup box in the browser.
//todo maybe should have a session time out? otherwise it reset on browers close?
//todo password encryption?
//todo remove souts and add logging info?
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.axway.grapes.core.service.CredentialService;
import org.axway.grapes.model.datamodel.Credential;
import org.wisdom.api.crypto.Crypto;
import org.wisdom.api.http.Context;
import org.wisdom.api.http.Result;
import org.wisdom.api.http.Results;
import org.wisdom.api.security.Authenticator;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by jennifer on 5/22/15.
 */
@Component
@Provides
@Instantiate
public class GrapesAuthenticator implements Authenticator {
    @Requires
    Crypto crypto;
    @Requires
    CredentialService credentialService;

    /**
     * @return the name of the authenticator used to select the authenticator service in the
     * {@link org.wisdom.api.security.Authenticated} annotation. Must not be {@literal null}.
     */
    @Override
    public String getName() {
        System.out.println("inside auth");
        return "grapes-authenticator";
    }

    /**
     * Retrieves the username from the HTTP context; the default is to read from the session cookie.
     *
     * @param context the context
     * @return {@literal null} if the user is not authenticated, the user name otherwise.
     */
    @Override
    public String getUserName(Context context) {
        String str="";
       String authHeader =context.header("Authorization");
        System.out.println("auth header recived: "+authHeader);
        System.out.println(context.session().getData());



        if (authHeader != null && authHeader.startsWith("Basic")) {
            String userNameAndPassword = authHeader.substring("Basic ".length()).trim();
//            System.out.println(userNameAndPassword);
            byte[] bytes = crypto.decodeBase64(userNameAndPassword);
            try {
                str = new String(bytes, "UTF-8");
                System.out.println(str);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            System.out.println("str is still " + str);
            String[] strings = str.split(":");
            if (strings.length==2){
                System.out.println("string 0 is "+strings[0]);
                System.out.println("string 1 is "+strings[1]);
                List<Credential.AvailableRoles> roles = getsomething(strings[0], strings[1]);
                System.out.println("rolese is "+roles);

                if (roles != null) {
                    if(!roles.isEmpty()){
//                        System.out.println("should be here");
                        context.session().put("roles", String.valueOf(roles));
                    }

                }
            }
            System.out.println("grrr");
        }
        if(context.session().get("roles") != null){
            return context.session().get("roles");
        }
        return null;
    }

    /**
     * Generates an alternative result if the user is not authenticated. It should be a '401 Not Authorized' response.
     *
     * @param context the context
     * @return the result.
     */
    @Override
    public Result onUnauthorized(Context context) {
        return Results.unauthorized("Your are not authenticated !");
    }

    private List<Credential.AvailableRoles> getsomething(String username, String password){

        if(username!= null && !username.isEmpty() && (password !=null && !password.isEmpty())){
                    System.out.println("in my new spiffy method user: "+username + " password: "+password);
          try {
              Credential credential = credentialService.getCredential(username);
              if (credential != null && password.equals(credential.getPassword())) {
                  return credential.getRoles();
              }
          }catch(NoSuchElementException e){

              System.out.println("invalid credentials");
             // return null;
          }
        }
     return null;
    }

}
