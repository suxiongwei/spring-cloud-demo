package indi.mofan.order.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Activate(group = {"provider", "consumer"})
public class DemoFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) {
        long startTime = System.currentTimeMillis();

        RpcContext rpcContext = RpcContext.getServiceContext();
        String interfaceName = invoker.getInterface().getName();
        String methodName = invocation.getMethodName();
        String remoteAddress = rpcContext.getRemoteHost() + ":" + rpcContext.getRemotePort();

        log.info("=== Dubbo Filter 拦截开始 ===");
        log.info("接口: {}", interfaceName);
        log.info("方法: {}", methodName);
        log.info("远程地址: {}", remoteAddress);
        log.info("参数类型: {}", Arrays.toString(invocation.getParameterTypes()));
        log.info("参数值: {}", Arrays.toString(invocation.getArguments()));

        Map<String, Object> requestInfo = new HashMap<>();
        requestInfo.put("interfaceName", interfaceName);
        requestInfo.put("methodName", methodName);
        requestInfo.put("remoteAddress", remoteAddress);
        requestInfo.put("parameterTypes", Arrays.toString(invocation.getParameterTypes()));
        requestInfo.put("parameterValues", Arrays.toString(invocation.getArguments()));
        requestInfo.put("startTime", startTime);

        invocation.getAttachments().put("filter-request-info", requestInfo.toString());

        try {
            Result result = invoker.invoke(invocation);

            long duration = System.currentTimeMillis() - startTime;

            log.info("=== Dubbo Filter 拦截结束 ===");
            log.info("执行时间: {}ms", duration);
            log.info("返回值: {}", result.getValue());
            log.info("异常: {}", result.getException());

            Map<String, Object> responseInfo = new HashMap<>();
            responseInfo.put("duration", duration);
            responseInfo.put("returnValue", result.getValue());
            responseInfo.put("hasException", result.getException() != null);
            responseInfo.put("exception", result.getException() != null ? result.getException().getMessage() : null);

            result.getAttachments().put("filter-response-info", responseInfo.toString());

            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Dubbo Filter 拦截异常: {}", e.getMessage(), e);

            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("duration", duration);
            errorInfo.put("error", e.getMessage());

            invocation.getAttachments().put("filter-error-info", errorInfo.toString());

            throw e;
        }
    }
}
