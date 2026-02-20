package indi.mofan.order.dubbo;

import indi.mofan.product.bean.Product;
import indi.mofan.product.dubbo.service.IProductDubboService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
@Component
public class DubboScenarioHelper {

    public Map<String, Object> testLoadBalanceStrategy(String strategy, String description, Integer requestCount,
            Supplier<Product> invoker) {
        Map<String, Object> result = new HashMap<>();
        result.put("strategy", strategy);
        result.put("description", description);
        result.put("requestCount", requestCount);

        List<Map<String, Object>> requestResults = new ArrayList<>();
        Map<String, Integer> serverCount = new HashMap<>();

        for (int i = 0; i < requestCount; i++) {
            long startTime = System.currentTimeMillis();
            Product product = invoker.get();
            long duration = System.currentTimeMillis() - startTime;

            Map<String, Object> requestResult = new HashMap<>();
            requestResult.put("requestId", i + 1);
            requestResult.put("productId", product.getId());
            requestResult.put("productName", product.getProductName());
            requestResult.put("responseTime", duration + "ms");
            requestResults.add(requestResult);

            String serverKey = product.getProductName();
            serverCount.put(serverKey, serverCount.getOrDefault(serverKey, 0) + 1);
        }

        result.put("requestResults", requestResults);
        result.put("serverDistribution", serverCount);
        return result;
    }

    public Map<String, Object> compareProtocols(IProductDubboService dubboService, IProductDubboService tripleService,
            IProductDubboService restService, Long productId, Integer requestCount) {
        Map<String, Object> result = new HashMap<>();
        result.put("productId", productId);
        result.put("requestCount", requestCount);

        Map<String, Object> dubboResult = new HashMap<>();
        Map<String, Object> tripleResult = new HashMap<>();
        Map<String, Object> restResult = new HashMap<>();

        List<Long> dubboResponseTimes = new ArrayList<>();
        List<Long> tripleResponseTimes = new ArrayList<>();
        List<Long> restResponseTimes = new ArrayList<>();

        int dubboSuccess = 0;
        int tripleSuccess = 0;
        int restSuccess = 0;

        for (int i = 0; i < requestCount; i++) {
            long startTime = System.currentTimeMillis();
            try {
                dubboService.getProductById(productId);
                dubboSuccess++;
                dubboResponseTimes.add(System.currentTimeMillis() - startTime);
            } catch (Exception e) {
                log.warn("Dubbo协议调用失败: {}", e.getMessage());
            }

            startTime = System.currentTimeMillis();
            try {
                tripleService.getProductById(productId);
                tripleSuccess++;
                tripleResponseTimes.add(System.currentTimeMillis() - startTime);
            } catch (Exception e) {
                log.warn("Triple协议调用失败: {}", e.getMessage());
            }

            startTime = System.currentTimeMillis();
            try {
                restService.getProductById(productId);
                restSuccess++;
                restResponseTimes.add(System.currentTimeMillis() - startTime);
            } catch (Exception e) {
                log.warn("REST协议调用失败: {}", e.getMessage());
            }
        }

        buildProtocolResult(dubboResult, dubboSuccess, requestCount, dubboResponseTimes);
        buildProtocolResult(tripleResult, tripleSuccess, requestCount, tripleResponseTimes);
        buildProtocolResult(restResult, restSuccess, requestCount, restResponseTimes);

        result.put("dubbo", dubboResult);
        result.put("triple", tripleResult);
        result.put("rest", restResult);
        result.put("summary", generateComparisonSummary(dubboResult, tripleResult, restResult));
        return result;
    }

    private void buildProtocolResult(Map<String, Object> result, int successCount, int requestCount,
            List<Long> responseTimes) {
        result.put("successCount", successCount);
        result.put("failCount", requestCount - successCount);
        result.put("successRate", String.format("%.2f%%", (double) successCount / requestCount * 100));
        if (!responseTimes.isEmpty()) {
            result.put("avgResponseTime", responseTimes.stream().mapToLong(Long::longValue).average().orElse(0));
            result.put("minResponseTime", responseTimes.stream().mapToLong(Long::longValue).min().orElse(0));
            result.put("maxResponseTime", responseTimes.stream().mapToLong(Long::longValue).max().orElse(0));
        }
    }

    private Map<String, Object> generateComparisonSummary(Map<String, Object> dubbo, Map<String, Object> triple,
            Map<String, Object> rest) {
        Map<String, Object> summary = new HashMap<>();

        Double dubboAvg = (Double) dubbo.get("avgResponseTime");
        Double tripleAvg = (Double) triple.get("avgResponseTime");
        Double restAvg = (Double) rest.get("avgResponseTime");

        if (dubboAvg != null && tripleAvg != null && restAvg != null) {
            Double fastest = Math.min(dubboAvg, Math.min(tripleAvg, restAvg));
            String fastestProtocol;
            if (fastest.equals(dubboAvg)) {
                fastestProtocol = "Dubbo";
            } else if (fastest.equals(tripleAvg)) {
                fastestProtocol = "Triple";
            } else {
                fastestProtocol = "REST";
            }
            summary.put("fastestProtocol", fastestProtocol);
            summary.put("fastestAvgTime", fastest);
        }

        Integer dubboSuccess = (Integer) dubbo.get("successCount");
        Integer tripleSuccess = (Integer) triple.get("successCount");
        Integer restSuccess = (Integer) rest.get("successCount");

        Integer highestSuccess = Math.max(dubboSuccess, Math.max(tripleSuccess, restSuccess));
        String mostReliableProtocol;
        if (highestSuccess.equals(dubboSuccess)) {
            mostReliableProtocol = "Dubbo";
        } else if (highestSuccess.equals(tripleSuccess)) {
            mostReliableProtocol = "Triple";
        } else {
            mostReliableProtocol = "REST";
        }
        summary.put("mostReliableProtocol", mostReliableProtocol);
        summary.put("highestSuccessCount", highestSuccess);
        return summary;
    }
}
