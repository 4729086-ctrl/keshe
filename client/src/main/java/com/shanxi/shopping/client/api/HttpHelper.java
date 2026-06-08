package com.shanxi.shopping.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shanxi.shopping.client.common.AppContext;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

/**
 * HTTP 请求辅助工具 — 统一处理 Token 注入和空 body 保护
 */
public class HttpHelper {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String BASE = "http://localhost:8080";

    /** GET 请求，返回 Map */
    public static Map<String, Object> getMap(String path) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + path))
                .header("Authorization", "Bearer " + AppContext.getInstance().getToken())
                .GET().build();
        HttpResponse<String> resp = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        String body = resp.body();
        if (body == null || body.isEmpty()) throw new RuntimeException("服务器无响应");
        return MAPPER.readValue(body, Map.class);
    }

    /** POST 请求，返回 Map */
    public static Map<String, Object> postMap(String path, Object body) throws Exception {
        String json = MAPPER.writeValueAsString(body);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + path))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + AppContext.getInstance().getToken())
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> resp = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        String respBody = resp.body();
        if (respBody == null || respBody.isEmpty()) throw new RuntimeException("服务器无响应");
        return MAPPER.readValue(respBody, Map.class);
    }

    /** 安全解析 JSON（空 body 抛明确异常） */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> parse(String body) {
        if (body == null || body.isEmpty()) throw new RuntimeException("服务器返回空响应");
        try {
            return MAPPER.readValue(body, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("响应解析失败: " + e.getMessage());
        }
    }
}
