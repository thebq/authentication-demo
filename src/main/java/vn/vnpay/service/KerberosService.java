package vn.vnpay.service;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.common.util.KerberosUtil;
import vn.vnpay.common.util.MetaData;
import vn.vnpay.kerberos.client.Hazelcast;
import vn.vnpay.model.Result;

import java.util.Objects;

/**
 * @author thebq
 * Created: 21/08/2023
 */
@Slf4j
public class KerberosService {
    private final Hazelcast hazelcast = new Hazelcast();
    private final KerberosUtil kerberosUtil = KerberosUtil.getInstance();

    private static KerberosService instance;

    public static KerberosService getInstance() {
        if (Objects.isNull(instance)) {
            instance = new KerberosService();
        }
        return instance;
    }

    public FullHttpResponse loginByKerberos() {
        log.info("Start login by kerberos");
        boolean response = hazelcast.HazelcastRequest();
        if (response) {
            Result result = new Result(MetaData.SUCCESS, null);
            return kerberosUtil.createResponse(HttpResponseStatus.OK, result.toString());
        }
        Result result = new Result(MetaData.FAIL_LOGIN, null);
        return kerberosUtil.createResponse(HttpResponseStatus.OK, result.toString());
    }
}