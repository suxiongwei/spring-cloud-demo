package indi.mofan.common;

/**
 * Common response envelope for service APIs.
 */
public class ApiResponse<T> {
    private int code;
    private String msg;
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(String msg, T data) {
        return new ApiResponse<>(ResultCode.SUCCESS.getCode(), msg, data);
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(ResultCode.SUCCESS.getDefaultMsg(), data);
    }

    public static <T> ApiResponse<T> success(String msg) {
        return new ApiResponse<>(ResultCode.SUCCESS.getCode(), msg, null);
    }

    public static <T> ApiResponse<T> fail(ResultCode code, String msg) {
        return new ApiResponse<>(code.getCode(), msg, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
