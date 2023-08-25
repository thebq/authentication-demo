package vn.vnpay.kerberos.kdc.authorization;

import lombok.extern.slf4j.Slf4j;
import org.apache.kerby.asn1.Asn1;

import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.login.LoginContext;
import java.util.Set;

/**
 * @author thebq
 * Created: 21/08/2023
 */
@Slf4j
public class Authorization {
    public void InitiatorAuthentication() {
        try {
            System.setProperty("sun.security.krb5.debug", "true");
            System.setProperty("java.security.auth.login.config", "jaas.conf");
            System.setProperty("java.security.krb5.conf", "krb5.conf");

            LoginContext lc = new LoginContext("KerberosWithPrompt",
                    new NamePasswordCbHandler("jduke@TEST.REALM", "theduke".toCharArray()));
            lc.login();
            Subject subj = lc.getSubject();

            Set<Object> privateCredentials = subj.getPrivateCredentials();
            KerberosTicket kt = (KerberosTicket) privateCredentials.iterator().next();

            Asn1.decodeAndDump(kt.getEncoded());
        } catch (Exception e) {
            log.error("Init authorization fail");
        }
    }

    public boolean AcceptorAuthentication() {
        try {
            System.setProperty("sun.security.krb5.debug", "true");
            System.setProperty("java.security.auth.login.config", "jaas.conf");
            System.setProperty("java.security.krb5.conf", "krb5.conf");

            LoginContext lc = new LoginContext("KerberosAcceptorWithKeytab");
            lc.login();
            return lc.getSubject() != null;
        } catch (Exception e) {
            log.error("Acceptor authentication fail");
        }
        return false;
    }
}