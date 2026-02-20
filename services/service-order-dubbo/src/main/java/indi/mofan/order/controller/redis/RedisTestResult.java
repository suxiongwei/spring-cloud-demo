package indi.mofan.order.controller.redis;

import lombok.Data;

import java.util.List;

@Data
public class RedisTestResult {
    private String testName;
    private boolean success;
    private String message;
    private Object data;
    private long executionTime;
    private String explanation;
    private List<String> interviewPoints;

    public static RedisTestResult success(String testName, String message, Object data, String explanation,
            List<String> interviewPoints) {
        RedisTestResult result = new RedisTestResult();
        result.setTestName(testName);
        result.setSuccess(true);
        result.setMessage(message);
        result.setData(data);
        result.setExplanation(explanation);
        result.setInterviewPoints(interviewPoints);
        return result;
    }

    public static RedisTestResult error(String testName, String message, String explanation) {
        RedisTestResult result = new RedisTestResult();
        result.setTestName(testName);
        result.setSuccess(false);
        result.setMessage(message);
        result.setExplanation(explanation);
        return result;
    }
}
