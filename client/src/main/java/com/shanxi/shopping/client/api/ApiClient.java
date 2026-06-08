package com.shanxi.shopping.client.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shanxi.shopping.client.common.AppContext;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/**
 * HTTP API 客户端 — 封装与后端 REST 接口的通信
 */
public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080";

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());

    // ==================== 通用方法 ====================

    public static <T> T get(String path, TypeReference<Map<String, Object>> responseType) throws IOException, InterruptedException {
        HttpRequest request = buildGet(path);
        return send(request, responseType);
    }

    public static <T> T get(String path, Class<T> clazz) throws IOException, InterruptedException {
        HttpRequest request = buildGet(path);
        return sendForData(request, clazz);
    }

    public static <T> T post(String path, Object body, Class<T> clazz) throws IOException, InterruptedException {
        HttpRequest request = buildPost(path, body);
        return sendForData(request, clazz);
    }

    public static void post(String path, Object body) throws IOException, InterruptedException {
        HttpRequest request = buildPost(path, body);
        sendVoid(request);
    }

    public static <T> T put(String path, Object body, Class<T> clazz) throws IOException, InterruptedException {
        HttpRequest request = buildPut(path, body);
        return sendForData(request, clazz);
    }

    public static void put(String path, Object body) throws IOException, InterruptedException {
        HttpRequest request = buildPut(path, body);
        sendVoid(request);
    }

    /** PUT 请求（无 body） */
    public static void put(String path) throws IOException, InterruptedException {
        HttpRequest request = buildPutNoBody(path);
        sendVoid(request);
    }

    public static void delete(String path) throws IOException, InterruptedException {
        HttpRequest request = buildDelete(path);
        sendVoid(request);
    }

    public static void delete(String path, Object body) throws IOException, InterruptedException {
        HttpRequest request = buildDeleteWithBody(path, body);
        sendVoid(request);
    }

    // ==================== 请求构建 ====================

    private static HttpRequest buildGet(String path) {
        return withAuth(HttpRequest.newBuilder().GET(), path).build();
    }

    private static HttpRequest buildPost(String path, Object body) {
        try {
            String json = MAPPER.writeValueAsString(body);
            return withAuth(HttpRequest.newBuilder()
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json)), path).build();
        } catch (Exception e) {
            throw new RuntimeException("JSON 序列化失败", e);
        }
    }

    private static HttpRequest buildPut(String path, Object body) {
        try {
            String json = MAPPER.writeValueAsString(body);
            return withAuth(HttpRequest.newBuilder()
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json)), path).build();
        } catch (Exception e) {
            throw new RuntimeException("JSON 序列化失败", e);
        }
    }

    private static HttpRequest buildDelete(String path) {
        return withAuth(HttpRequest.newBuilder().DELETE(), path).build();
    }

    private static HttpRequest buildDeleteWithBody(String path, Object body) {
        try {
            String json = MAPPER.writeValueAsString(body);
            return withAuth(HttpRequest.newBuilder()
                    .header("Content-Type", "application/json")
                    .method("DELETE", HttpRequest.BodyPublishers.ofString(json)), path).build();
        } catch (Exception e) {
            throw new RuntimeException("JSON 序列化失败", e);
        }
    }

    private static HttpRequest.Builder withAuth(HttpRequest.Builder builder, String path) {
        builder.uri(URI.create(BASE_URL + path));
        String token = AppContext.getInstance().getToken();
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
        return builder;
    }

    /** 所有 PUT 请求统一不加 body */
    private static HttpRequest buildPutNoBody(String path) {
        return withAuth(HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.noBody()), path).build();
    }

    // ==================== 响应处理 ====================

    @SuppressWarnings("unchecked")
    private static <T> T send(HttpRequest request, TypeReference<Map<String, Object>> responseType)
            throws IOException, InterruptedException {
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        if (body == null || body.isEmpty()) return null;
        Map<String, Object> result = MAPPER.readValue(body, responseType);
        checkResult(result);
        return (T) result;
    }

    @SuppressWarnings("unchecked")
    private static <T> T sendForData(HttpRequest request, Class<T> clazz)
            throws IOException, InterruptedException {
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        if (body == null || body.isEmpty()) return null;
        Map<String, Object> result = MAPPER.readValue(body,
                new TypeReference<Map<String, Object>>() {});
        checkResult(result);
        Object data = result.get("data");
        if (data == null) return null;
        return MAPPER.convertValue(data, clazz);
    }

    private static void sendVoid(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();
        if (body == null || body.isEmpty()) return;
        Map<String, Object> result = MAPPER.readValue(body,
                new TypeReference<Map<String, Object>>() {});
        checkResult(result);
    }

    private static void checkResult(Map<String, Object> result) {
        if (result == null) {
            throw new RuntimeException("服务器无响应");
        }
        int code = (int) result.getOrDefault("code", 500);
        if (code != 200) {
            throw new RuntimeException((String) result.getOrDefault("message", "请求失败"));
        }
    }
}
