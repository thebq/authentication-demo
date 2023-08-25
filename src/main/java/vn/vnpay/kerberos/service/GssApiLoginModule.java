package vn.vnpay.kerberos.service;

import com.hazelcast.security.ClusterIdentityPrincipal;
import com.hazelcast.security.ClusterRolePrincipal;
import com.hazelcast.security.CredentialsCallback;
import com.hazelcast.security.HazelcastPrincipal;
import com.hazelcast.security.TokenCredentials;
import lombok.extern.slf4j.Slf4j;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSManager;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.security.Principal;
import java.util.Iterator;
import java.util.Map;

/**
 * @author thebq
 * Created: 21/08/2023
 */
@Slf4j
public class GssApiLoginModule implements LoginModule {

    private Subject subject;
    private CallbackHandler callbackHandler;
    private String name;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
                           Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.name = null;
    }

    @Override
    public boolean login() {
        try {
            CredentialsCallback cc = new CredentialsCallback();
            callbackHandler.handle(new Callback[]{cc});
            TokenCredentials creds = (TokenCredentials) cc.getCredentials();

            byte[] token = creds.getToken();

            GSSContext gssContext = GSSManager.getInstance().createContext((GSSCredential) null);
            token = gssContext.acceptSecContext(token, 0, token.length);

            if (!gssContext.isEstablished()) {
                log.error("Multi-step negotiation is not supported by this login module");
                return false;
            }
            name = gssContext.getSrcName().toString();
        } catch (Exception e) {
            log.error("Something went wrong during login", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean commit() throws LoginException {
        if (name == null) {
            throw new LoginException("No name available.");
        }
        subject.getPrincipals().add(new ClusterIdentityPrincipal(name));
        subject.getPrincipals().add(new ClusterRolePrincipal("kerberos"));
        return true;
    }

    @Override
    public boolean abort() {
        return logout();
    }

    @Override
    public boolean logout() {
        for (Iterator<Principal> it = subject.getPrincipals().iterator(); it.hasNext(); ) {
            Principal p = it.next();
            if (p instanceof HazelcastPrincipal) {
                it.remove();
            }
        }
        return true;
    }

}