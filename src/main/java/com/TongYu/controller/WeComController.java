package com.TongYu.controller;

import com.TongYu.aes.AesException;
import com.TongYu.config.ApiResponse;
import com.TongYu.service.WeComService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 企微相关接口
 *
 * @author TongYu
 */
@Slf4j
@RestController()
@RequestMapping("/weCom")
public class WeComController {

    @Resource
    public WeComService weComService;

    /**
     * 上传驾驶证图片至企业微信微盘
     *
     * @param file  驾驶证图片
     * @param stuId 学生id
     */
    @PostMapping("/uploadFile")
    public ApiResponse uploadFile(@RequestPart("file") MultipartFile file, @RequestPart("stuId") String stuId) {
        // 驾驶证图片保存至企业微信微盘
        return ApiResponse.ok(weComService.uploadFileToWeCom(file, stuId));
    }

    /**
     * 更新AccessToken
     * 每两个小时执行一次
     */
    @Scheduled(cron = "0 0 */2 * * ?")
    public void getCorpAccessToken() {
        weComService.getCorpAccessToken();
    }

    /**
     * jsCode2session|获取 session_key
     *
     * @param jsCode 前端获取的 code
     */
    @GetMapping("/jsCode2session")
    public ApiResponse jsCode2session(String jsCode) {
        return ApiResponse.ok(weComService.jsCode2session(jsCode));
    }

    /**
     * 获取微信客户详情
     *
     * @param externalUserId 微信外部联系人ID
     * @return 微信客户详情
     */
    @GetMapping("/getWxCustomerDetails")
    public ApiResponse getWxCustomerDetails(String externalUserId) {
        return ApiResponse.ok(weComService.getWxCustomerDetails(externalUserId));
    }

    /**
     * 注册用户测试
     *
     * @param externalUserId 微信外部联系人ID
     * @return 注册结果
     */
    @GetMapping("/registerStudent")
    public ApiResponse registerStudent(String externalUserId) {
        return ApiResponse.ok(weComService.registerStudent(externalUserId));
    }


    /**
     * 企业微信回调
     * 3.1 支持Http Get请求验证URL有效性
     * 3.2 支持Http Post请求接收业务数据
     *
     * @return "success"
     */
    @RequestMapping(value = "/getCallBack", method = {RequestMethod.GET, RequestMethod.POST})
    public Object getCallBack(HttpServletRequest request, @RequestBody(required = false) String body) throws AesException {
        log.info("收到企业微信回调请求，请求体：{}", body);
        return weComService.getCallBack(request, body);
    }


    /**
     * 通过config接口注入权限验证配置
     *
     * @return jsSdkResponse
     */
    @GetMapping(value = "/getJsConfig")
    public ApiResponse getJsConfig(String pageUrl) {
        return ApiResponse.ok(weComService.getJsConfig(pageUrl));
    }

    /**
     * 构造企业微信登录链接
     * @param redirectUri 登录成功后跳转的链接
     * @return 企业微信登录链接
     */
    @GetMapping(value = "/getLoginUrl")
    public void getLoginUrl(HttpServletResponse response, String redirectUri) {
        weComService.getLoginUrl(response,redirectUri);
    }

    /**
     * 企业微信扫码登录回调
     * @return 登录结果
     */
    @GetMapping(value = "/loginCallBack")
    public ApiResponse loginCallBack(String code, String state) {
        return ApiResponse.ok(weComService.loginCallBack(code, state));
    }

    /**
     * 企业微信获取用户信息
     * @param code 扫码登录返回的code
     */
}