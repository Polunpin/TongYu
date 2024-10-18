package com.TongYu.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * 示例：使用 RestTemplate 发送 HTTP 请求
 */
@Service
public class ApiService {

    @Resource
    private RestTemplate restTemplate;

    // 示例：发送 GET 请求
    public String getExample() {
        String url = "https://api.example.com/data"; // JSON 接口的 URL
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response.getBody(); // 返回响应体（JSON 字符串）
    }

    // 示例：发送 POST 请求
    public String postExample() {
        String url = "https://api.example.com/data"; // JSON 接口的 URL
        MyRequestBody requestBody = new MyRequestBody("value1", "value2"); // 请求体对象
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestBody, String.class);
        return response.getBody(); // 返回响应体（JSON 字符串）
    }

    // 自定义请求体类
    static class MyRequestBody {
        private String field1;
        private String field2;

        public MyRequestBody(String field1, String field2) {
            this.field1 = field1;
            this.field2 = field2;
        }

        // Getter 和 Setter
        public String getField1() {
            return field1;
        }

        public void setField1(String field1) {
            this.field1 = field1;
        }

        public String getField2() {
            return field2;
        }

        public void setField2(String field2) {
            this.field2 = field2;
        }
    }
}
