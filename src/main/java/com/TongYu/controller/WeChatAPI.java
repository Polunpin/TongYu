package com.TongYu.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeChatAPI {
    public static void main(String[] args) throws Exception {
        String accessToken = "YOUR_ACCESS_TOKEN"; // 获取Access Token
        String unionId = "YOUR_UNION_ID"; // 外部联系人的unionid

        // 获取外部联系人列表
        String url = "https://qyapi.weixin.qq.com/cgi-bin/external/contact/list?access_token=" + accessToken;

        //... 发送请求并获取返回值，解析返回值
        // 从返回的外部联系人列表中查找unionid对应的userId
        
        // 打印对应的userId
    }

    private static String sendGet(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        }
        return null;
    }
}
