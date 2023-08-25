package vn.vnpay.kerberos.client;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.security.SimpleTokenCredentials;
import lombok.extern.slf4j.Slf4j;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import vn.vnpay.common.constant.KerberosConstant;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static vn.vnpay.common.constant.KerberosConstant.KRB5_OID;

/**
 * @author thebq
 * Created: 21/08/2023
 */
@Slf4j
public class Hazelcast {
    public boolean HazelcastRequest() {
        HazelcastInstance hz = null;
        try {
            System.setProperty("java.security.auth.login.config", "jaas.conf");
            System.setProperty("java.security.krb5.conf", "krb5.conf");
            System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");

            log.info("Start send request from Hazelcast");
            GSSManager manager = GSSManager.getInstance();
            GSSName servicePrincipalName = manager.createName("hazelcast/localhost@TEST.REALM", null);
            GSSContext gssContext = manager.createContext(servicePrincipalName, KRB5_OID, null, GSSContext.DEFAULT_LIFETIME);
            gssContext.requestMutualAuth(false);
            byte[] token = gssContext.initSecContext(new byte[0], 0, 0);
            if (!gssContext.isEstablished()) {
                log.info("Multi-step GSS-API context initialization is not supported");
                return false;
            }

            ClientConfig clientConfig = new ClientConfig();
            clientConfig.getSecurityConfig().setCredentials(new SimpleTokenCredentials(token));
            clientConfig.getNetworkConfig().addAddress(KerberosConstant.LOCAL_HOST);

            hz = HazelcastClient.newHazelcastClient(clientConfig);

            Map<String, String> testMap = new HashMap<>();
            if (Objects.nonNull(hz))
                testMap = hz.getMap(KerberosConstant.TEST);
            String oldVal = testMap.put(KerberosConstant.LAST_RUN, LocalTime.now().toString());
            if (oldVal == null) {
                log.info("This is the first run of the client application");
            } else {
                log.info("Last client application run was at " + oldVal);
            }
            log.info("Finish send request from Hazelcast");
            return true;
        } catch (Exception e) {
            log.error("Hazelcast send request fail");
            return false;
        } finally {
            if (Objects.nonNull(hz))
                hz.shutdown();
        }
    }
}