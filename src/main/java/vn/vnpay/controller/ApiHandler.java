package vn.vnpay.controller;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.common.constant.KerberosConstant;
import vn.vnpay.common.util.KerberosUtil;
import vn.vnpay.common.util.MetaData;
import vn.vnpay.model.Result;
import vn.vnpay.service.KerberosService;

import java.util.Objects;

/**
 * @author thebq
 * Created: 13/08/2023
 */
@Slf4j
@ChannelHandler.Sharable
public class ApiHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final KerberosUtil kerberosUtil = KerberosUtil.getInstance();
    private final KerberosService kerberosService = KerberosService.getInstance();
    private static ApiHandler instance;

    public static ApiHandler getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ApiHandler();
        }
        return instance;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        HttpMethod method = request.method();
        String uri = request.uri();
        try {
            if (HttpMethod.POST.equals(method)) {
                if (KerberosConstant.LOGIN_URL.equals(uri)) {
                    updateFeeTransaction(ctx, request);
                }
                handleRequest(ctx);
            }
        } catch (Exception e) {
            log.error("Fail to request: ", e);
        }
    }

    private void updateFeeTransaction(ChannelHandlerContext ctx, FullHttpRequest request) {
        log.info("START login by kerberos");
        FullHttpResponse response = kerberosService.loginByKerberos();
        log.info("FINISH login by kerberos");
        ctx.writeAndFlush(response);
    }

    private void handleRequest(ChannelHandlerContext ctx) {
        Result result = new Result(MetaData.NOT_FOUND, null);
        FullHttpResponse response = kerberosUtil.createResponse(HttpResponseStatus.OK, result.toString());
        ctx.writeAndFlush(response);
    }
}
