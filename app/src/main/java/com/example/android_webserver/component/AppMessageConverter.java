package com.example.android_webserver.component;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android_webserver.model.ResultInfo;
import com.example.android_webserver.util.JsonUtils;
import com.yanzhenjie.andserver.framework.MessageConverter;
import com.yanzhenjie.andserver.framework.body.JsonBody;
import com.yanzhenjie.andserver.http.ResponseBody;
import com.yanzhenjie.andserver.util.IOUtils;
import com.yanzhenjie.andserver.util.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

public class AppMessageConverter implements MessageConverter {
    @Override
    public ResponseBody convert(@Nullable Object output, @Nullable MediaType mediaType) {

        return new JsonBody(JsonUtils.successJson(output));
    }

    @Nullable
    @Override
    public <T> T convert(@NonNull InputStream stream, @Nullable MediaType mediaType, Type type) throws IOException {
        Charset charset = mediaType == null ? null : mediaType.getCharset();

        if (charset == null) {
            return JsonUtils.parseJson(IOUtils.toString(stream), type);
        }

        return JsonUtils.parseJson(IOUtils.toString(stream, charset), type);
    }
}
