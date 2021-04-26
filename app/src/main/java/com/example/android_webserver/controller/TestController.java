package com.example.android_webserver.controller;

import com.alibaba.fastjson.JSON;
import com.example.android_webserver.component.LoginInterceptor;
import com.example.android_webserver.model.UserInfo;
import com.example.android_webserver.util.FileUtils;
import com.example.android_webserver.util.Logger;
import com.yanzhenjie.andserver.annotation.Addition;
import com.yanzhenjie.andserver.annotation.CookieValue;
import com.yanzhenjie.andserver.annotation.CrossOrigin;
import com.yanzhenjie.andserver.annotation.FormPart;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.PathVariable;
import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.PutMapping;
import com.yanzhenjie.andserver.annotation.RequestBody;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.annotation.RequestMethod;
import com.yanzhenjie.andserver.annotation.RequestParam;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.http.cookie.Cookie;
import com.yanzhenjie.andserver.http.multipart.MultipartFile;
import com.yanzhenjie.andserver.http.session.Session;
import com.yanzhenjie.andserver.util.MediaType;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Zhenjie Yan on 2018/6/9.
 */
@RestController
@RequestMapping(path = "/user")
class TestController {

    @RequestMapping(
        path = "/connection",
        method = {RequestMethod.GET},
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Object getConnection(HttpRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("getLocalAddr", request.getLocalAddr());
        map.put("getLocalName", request.getLocalName());
        map.put("getLocalPort", request.getLocalPort());
        map.put("getRemoteAddr", request.getRemoteAddr());
        map.put("getRemoteHost", request.getRemoteHost());
        map.put("getRemotePort", request.getRemotePort());
        Logger.i(JSON.toJSONString(map));
        return map;
    }

    @CrossOrigin(
        methods = {RequestMethod.POST, RequestMethod.GET}
    )
    @RequestMapping(
        path = "/get/{userId}",
        method = {RequestMethod.PUT, RequestMethod.POST, RequestMethod.GET},
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String info(@PathVariable(name = "userId") String userId, HttpRequest request) {
        return userId;
    }

    @PutMapping(path = "/get/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String modify(@PathVariable("userId") String userId, @RequestParam(name = "sex") String sex,
                  @RequestParam(name = "age") int age) {
        String message = "The userId is %1$s, and the sex is %2$s, and the age is %3$d.";
        return String.format(Locale.US, message, userId, sex, age);
    }

    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String login(HttpRequest request, HttpResponse response, @RequestParam(name = "account") String account,
                 @RequestParam(name = "password") String password) {
        Session session = request.getValidSession();
        session.setAttribute(LoginInterceptor.LOGIN_ATTRIBUTE, true);

        Cookie cookie = new Cookie("account", account + "=" + password);
        response.addCookie(cookie);
        return "Login successful.";
    }

    @Addition(stringType = "login", booleanType = true)
    @GetMapping(path = "/userInfo", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    UserInfo userInfo(@CookieValue("account") String account) {
        Logger.i("Account: " + account);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId("123");
        userInfo.setUserName("AndServer");
        return userInfo;
    }

    @PostMapping(path = "/upload", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String upload(@RequestParam(name = "avatar") MultipartFile file) throws IOException {
        File localFile = FileUtils.createRandomFile(file);
        file.transferTo(localFile);
        return localFile.getAbsolutePath();
    }

    @GetMapping(path = "/consume", consumes = {"application/json", "!application/xml"})
    String consume() {
        return "Consume is successful";
    }

    @GetMapping(path = "/produce", produces = {"application/json; charset=utf-8"})
    String produce() {
        return "Produce is successful";
    }

    @GetMapping(path = "/include", params = {"name=123"})
    String include(@RequestParam(name = "name") String name) {
        return name;
    }

    @GetMapping(path = "/exclude", params = "name!=123")
    String exclude() {
        return "Exclude is successful.";
    }

    @GetMapping(path = {"/mustKey", "/getName"}, params = "name")
    String getMustKey(@RequestParam(name = "name") String name) {
        return name;
    }

    @PostMapping(path = {"/mustKey", "/postName"}, params = "name")
    String postMustKey(@RequestParam(name = "name") String name) {
        return name;
    }

    @GetMapping(path = "/noName", params = "!name")
    String noName() {
        return "NoName is successful.";
    }

    @PostMapping(path = "/formPart")
    UserInfo forPart(@FormPart(name = "user") UserInfo userInfo) {
        return userInfo;
    }

    @PostMapping(path = "/jsonBody")
    UserInfo jsonBody(@RequestBody UserInfo userInfo) {
        return userInfo;
    }

    @PostMapping(path = "/listBody")
    List<UserInfo> jsonBody(@RequestBody List<UserInfo> infoList) {
        return infoList;
    }
}