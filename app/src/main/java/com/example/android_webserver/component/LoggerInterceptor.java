package com.example.android_webserver.component;

import androidx.annotation.NonNull;

import com.example.android_webserver.util.JsonUtils;
import com.example.android_webserver.util.Logger;
import com.yanzhenjie.andserver.framework.HandlerInterceptor;
import com.yanzhenjie.andserver.framework.handler.RequestHandler;
import com.yanzhenjie.andserver.http.HttpMethod;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.util.MultiValueMap;

public class LoggerInterceptor implements HandlerInterceptor {
    @Override
    public boolean onIntercept(@NonNull HttpRequest request, @NonNull HttpResponse response, @NonNull RequestHandler handler) throws Exception {
        String path = request.getPath();
        HttpMethod method = request.getMethod();
        MultiValueMap<String, String> valueMap = request.getParameter();
        Logger.i("Path: " + path);
        Logger.i("Method: " + method.value());
        Logger.i("Param: " + JsonUtils.toJsonString(valueMap));
        return false;
    }
}
