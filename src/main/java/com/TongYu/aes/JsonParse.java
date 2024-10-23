package com.TongYu.aes;

import org.json.JSONObject;

/**
 * JsonParse class
 * 提供提取消息格式中的密文及生成回复消息格式的接口.
 */
class JsonParse {

    /**
     * 提取出 JSON 包中的加密消息
     * @param jsontext 待提取的json字符串
     * @return 提取出的加密消息字符串
     */
    public static Object[] extract(String jsontext) throws AesException {
        Object[] result = new Object[3];
        try {
            System.out.println("------jsonText:" + jsontext);
            JSONObject json = new JSONObject(jsontext);
            String encryptMsg = json.getString("Encrypt");
            String toUserName = json.getString("ToUserName");
            String agentID = json.getString("AgentID");

            result[0] = toUserName;
            result[1] = encryptMsg;
            result[2] = agentID;
            return result;
        } catch (Exception e) {
            throw new AesException(AesException.ParseJsonError);
        }
    }

    /**
     * 生成json消息
     * @param encrypt 加密后的消息密文
     * @param signature 安全签名
     * @param timestamp 时间戳
     * @param nonce 随机字符串
     * @return 生成的json字符串
     */
    public static String generate(String encrypt, String signature, String timestamp, String nonce) {

        String format = "{\"encrypt\":\"%1$s\",\"msgsignature\":\"%2$s\",\"timestamp\":\"%3$s\",\"nonce\":\"%4$s\"}";
        return String.format(format, encrypt, signature, timestamp, nonce);

    }
}
