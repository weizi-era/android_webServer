package com.example.android_webserver.util;

import com.alibaba.fastjson.JSON;
import com.example.android_webserver.model.ResultInfo;

import java.lang.reflect.Type;

public class JsonUtils {

    /**
     * 执行成功
     * @param data
     * @return
     */
    public static String successJson(Object data) {
        ResultInfo resultInfo = new ResultInfo();

        resultInfo.setSuccess(true);
        resultInfo.setData(data);
        resultInfo.setResultCode(200);

        return JSON.toJSONString(resultInfo);

    }

    /**
     * 执行失败
     * @param code
     * @param msg
     * @return
     */
    public static String failureJson(int code, String msg) {
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setSuccess(false);
        resultInfo.setResultCode(code);
        resultInfo.setErrorMsg(msg);

        return JSON.toJSONString(resultInfo);
    }

    /**
     * 转换object为 json字符串。
     *
     * @param data the object.
     *
     * @return json string.
     */
    public static String toJsonString(Object data) {
        return JSON.toJSONString(data);
    }

    /**
     * 转换json字符串为object
     *
     * @param json json string.
     * @param type the type of object.
     * @param <T> type.
     *
     * @return object.
     */
    public static <T> T parseJson(String json, Type type) {
        return JSON.parseObject(json, type);
    }
}
