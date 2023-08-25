package vn.vnpay;

import lombok.extern.slf4j.Slf4j;
import vn.vnpay.kerberos.kdc.KerbyServer;
import vn.vnpay.kerberos.service.HazelcastService;
import vn.vnpay.server.NettyServer;

/**
 * @author thebq
 * Created: 11/08/2023
 */
@Slf4j
public class Main {
    public static void main(String[] args) {
        try {
            log.info("Start application");
            KerbyServer kerbyServer = new KerbyServer();
            kerbyServer.start();
            HazelcastService hazelcastService = new HazelcastService();
            hazelcastService.start();
            NettyServer nettyServer = new NettyServer();
            nettyServer.start();
            log.info("Finish application");
        } catch (Exception e) {
            log.error("Start application fail", e);
        } catch (Throwable throwable) {
            log.error("Start application fail", throwable);
        }
    }
}