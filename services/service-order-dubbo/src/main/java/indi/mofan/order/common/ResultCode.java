package indi.mofan.order.common;

/**
 * 企业级通用结果码约定
 */
public enum ResultCode {
    // 成功
    SUCCESS(200, "OK"),

    // 客户端错误（参数校验、未授权等）
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),

    // 后端处理失败
    INTERNAL_ERROR(500, "Internal Server Error"),

    // 业务相关：限流/熔断等（通常使用 429 表示 Too Many Requests）
    TOO_MANY_REQUESTS(429, "Too Many Requests");

    private final int code;
    private final String defaultMsg;

    ResultCode(int code, String defaultMsg) {
        this.code = code;
        this.defaultMsg = defaultMsg;
    }

    public int getCode() {
        return code;
    }

    public String getDefaultMsg() {
        return defaultMsg;
    }
}
