package com.example.android_webserver.util;

import android.os.Environment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.example.android_webserver.App;
import com.yanzhenjie.andserver.http.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

public class FileUtils {

    /**
     *  创建一个基于mimeType的随机文件
     * @param file
     * @return
     */
    public static File createRandomFile(MultipartFile file) {
        String extension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.getContentType().toString());
        if (!TextUtils.isEmpty(extension)) {
            extension = MimeTypeMap.getFileExtensionFromUrl(file.getFilename());
        }

        String uuid = UUID.randomUUID().toString();
        return new File(App.getInstance().getRootDir(), uuid  + "." + extension);
    }

    /**
     * SD 是否可用
     * @return
     */
    public static boolean storageAvailable() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sd = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            return sd.canWrite();
        } else {
            return false;
        }

    }
}
