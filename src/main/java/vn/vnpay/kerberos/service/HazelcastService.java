package vn.vnpay.kerberos.service;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.LoginModuleConfig;
import com.hazelcast.config.PermissionConfig;
import com.hazelcast.config.security.JaasAuthenticationConfig;
import com.hazelcast.config.security.RealmConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.common.constant.KerberosConstant;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import java.security.PrivilegedAction;

/**
 * @author thebq
 * Created: 21/08/2023
 */
@Slf4j
public class HazelcastService {
    public void start() {
        System.setProperty("java.security.auth.login.config", "jaas.conf");
        System.setProperty("java.security.krb5.conf", "krb5.conf");
        try {
            log.info("Start ");
            // Create config and set the evaluation license key
            Config config = new Config().setLicenseKey(
                    "ENTERPRISE_2023#jEszEuQ5exn6ojQPpkFfeeNm58Z6y8TT7K9zi5kfBhKcFcmRnvoNO7QNKhIt");

            // use TCP/IP cluster members discovery
            JoinConfig joinConfig = config.getNetworkConfig().getJoin();
            joinConfig.getMulticastConfig().setEnabled(false);
            joinConfig.getTcpIpConfig().setEnabled(true).addMember(KerberosConstant.LOCAL_HOST);

            // Configure Kerberos authentication for clients
            JaasAuthenticationConfig jaasAuthenticationConfig = new JaasAuthenticationConfig().addLoginModuleConfig(
                    new LoginModuleConfig(GssApiLoginModule.class.getName(), LoginModuleConfig.LoginModuleUsage.REQUIRED));
            config.getSecurityConfig()
                    .setEnabled(true)
                    .addClientPermissionConfig(new PermissionConfig(PermissionConfig.PermissionType.ALL, "*", null))
                    .setClientRealmConfig(KerberosConstant.KERBEROS,
                            new RealmConfig().setJaasAuthenticationConfig(jaasAuthenticationConfig));

            LoginContext lc = new LoginContext("HazelcastMember");
            lc.login();
            Subject.doAs(lc.getSubject(), (PrivilegedAction<HazelcastInstance>) () -> Hazelcast.newHazelcastInstance(config));
        } catch (Exception e) {
            log.error("Hazelcast receive response fail");
        }
    }
}