package vn.vnpay.kerberos.kdc;

import lombok.extern.slf4j.Slf4j;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.server.SimpleKdcServer;
import vn.vnpay.common.constant.KerberosConstant;
import vn.vnpay.common.util.LocalProperties;

import java.io.File;

import static java.util.Arrays.asList;
import static org.apache.kerby.kerberos.kerb.client.KrbConfigKey.PREAUTH_REQUIRED;

/**
 * @author thebq
 * Created: 21/08/2023
 */
@Slf4j
public class KerbyServer {
    private static int PORT;
    private static String HOST;
    private static String REALM;
    private static boolean ALLOW_UDP;

    static {
        try {
            PORT = Integer.parseInt(String.valueOf(LocalProperties.get("kerberos-port")));
            HOST = String.valueOf(LocalProperties.get("kerberos-host"));
            REALM = String.valueOf(LocalProperties.get("kerberos-realm"));
            ALLOW_UDP = (boolean) LocalProperties.get("kerberos-allow-udp");
        } catch (Exception e) {
            log.error("Load config FAIL");
        }
    }

    public void start() throws KrbException {
        SimpleKdcServer kdc = null;
        log.info("Start kerberos server");
        try {
            kdc = new SimpleKdcServer();
            kdc.setKdcHost(HOST);
            kdc.setKdcRealm(REALM);
            kdc.setKdcPort(PORT);
            kdc.setAllowUdp(ALLOW_UDP);
            kdc.getKdcConfig().setBoolean(PREAUTH_REQUIRED, false);
            // Init kdc server, krb5.conf file and kadmin api
            kdc.init();

            kdc.createPrincipal("thebq", "vnpay@123");
            kdc.createPrincipal("gsstest/localhost", "servicePassword");
            kdc.createPrincipal("hazelcast/localhost", "s1mpl3+FAST");

            // export service principal's keytab
            File keytabFile = new File(KerberosConstant.KEYTAB_FILE);
            if (!keytabFile.exists()) {
                kdc.getKadmin().exportKeytab(keytabFile, asList("gsstest/localhost@TEST.REALM", "hazelcast/localhost@TEST.REALM"));
            }

            kdc.start();
            log.info("Kerberos server has started on port: {}", PORT);
        } catch (Exception exception) {
            log.error("Kerberos server start FAIL");
        } finally {
            if (kdc != null)
                kdc.stop();
        }
    }
}