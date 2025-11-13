package indi.mofan.order.service;

import org.springframework.stereotype.Service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import indi.mofan.order.common.BlockDetailFormatter;

import indi.mofan.order.common.ApiResponse;
import indi.mofan.order.common.ResultCode;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommonResourceService {

    @SentinelResource(value = "common-resource", blockHandler = "commonResourceBlockHandler")
    public ApiResponse<String> commonResource(String source) {
        log.info("访问公共资源，来源: {}", source);
        return ApiResponse.success("公共资源访问成功，来源: " + source);
    }

    public ApiResponse<String> commonResourceBlockHandler(String source, BlockException ex) {
        log.warn("公共资源限流触发，来源: {}, {}", source, BlockDetailFormatter.format(ex));
        return ApiResponse.fail(ResultCode.TOO_MANY_REQUESTS, "公共资源访问受限，请稍后再试。" + BlockDetailFormatter.format(ex));
    }
}