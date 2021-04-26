package com.example.android_webserver;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yanzhenjie.loading.dialog.LoadingDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ServerManager mServerManager;

    private Button mStart;
    private Button mStop;
    private Button mBrowser;
    private TextView mTvMessage;
    private Toolbar toolbar;
    private Button mPhoto;

    private LoadingDialog mDialog;
    private String mRootUrl;
    private Uri photoUri;
    private String photoPath;
    private ImageView photoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mStart = findViewById(R.id.btn_start);
        mStop = findViewById(R.id.btn_stop);
        mBrowser = findViewById(R.id.btn_browse);
        mTvMessage = findViewById(R.id.tv_message);
        mPhoto = findViewById(R.id.btn_photo);
        photoImageView = findViewById(R.id.img_photo);


        mServerManager = new ServerManager(this);
        mServerManager.register();

        mStart.setOnClickListener(this);
        mStop.setOnClickListener(this);
        mBrowser.setOnClickListener(this);
        mPhoto.setOnClickListener(this);

        //start server
        mStart.performClick();

    }

    private void checkPermission(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            context.startForegroundService(new Intent(context, CoreService.class));
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            Log.d("进入：", "requestPermissions");
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            Log.d("进入：", "requestPermissions");
        }

    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_start: {
                showDialog();
                mServerManager.startServer();
                break;
            }
            case R.id.btn_stop: {
                showDialog();
                mServerManager.stopServer();
                break;
            }
            case R.id.btn_browse: {
                if (!TextUtils.isEmpty(mRootUrl)) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.setData(Uri.parse(mRootUrl));
                    startActivity(intent);
                }
                break;
            }

            case R.id.btn_photo: {
                 Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                 File photoFile = createImgFile();
                 photoUri = Uri.fromFile(photoFile);
                 intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                 startActivityForResult(intent, 1);
            }
        }
    }

    private File createImgFile() {

        @SuppressLint("SimpleDateFormat")
        String fileName = "img_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
        File sdDir;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            sdDir = this.getExternalCacheDir();
        } else {
            sdDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }

        File photoDir = new File(sdDir + "/MyPhoto/");

        if (!photoDir.exists()) {
            photoDir.mkdir();
        }

        File tempFile = new File(photoDir, fileName);
        try {
            if (tempFile.exists()) {
                tempFile.delete();
            }
            if (tempFile.createNewFile()) {
                Log.d(TAG, "createImgFile: 文件创建成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        photoPath = tempFile.getAbsolutePath();

        return tempFile;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    setImageBitmap();
                    galleryAddPic();
                    break;
                case 2:
                    if (data != null) {
                        photoUri = data.getData();
                    }
                    String[] filePathColumn={MediaStore.Audio.Media.DATA};
                    Cursor cursor=getContentResolver().query(photoUri,filePathColumn,null,null,null);
                    cursor.moveToFirst();
                    photoPath=cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                    cursor.close();

                    setImageBitmap();
                    break;
            }
        }
    }

    //将图片添加进手机相册
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(photoUri);
        this.sendBroadcast(mediaScanIntent);
    }

    /**
     * 压缩图片
     */
    private void setImageBitmap() {

        //获取imageView的宽和高
        int targetWidth = photoImageView.getWidth();
        int targetHeight = photoImageView.getHeight();

        //根据图片路径，获取bitmap的宽和高
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, options);
        int photoWidth = options.outWidth;
        int photoHeight = options.outHeight;

        //获取缩放比例
        int inSampleSize = 1;
        if (photoWidth > targetWidth || photoHeight > targetHeight) {
            int widthRatio = Math.round((float)photoWidth / targetWidth);
            int heightRatio = Math.round((float)photoHeight / targetHeight);
            inSampleSize = Math.min(widthRatio, heightRatio);
            Log.d("Record", "进入缩放比例");
        }

        //使用现在的options获取Bitmap
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
        photoImageView.setImageBitmap(bitmap);

      //  String bitmapToString = putImageToShare(getApplicationContext(), photoImageView);

      //  uploadImage(getApplicationContext(), bitmapToString);

    }

    private void uploadImage(Context context, String bitmapToString) {
        PhotoListService service = RetrofitUtils.getInstance().create(PhotoListService.class);

        RequestBody requestBody = RequestBody.create(MediaType.parse(""), bitmapToString);

        service.updatePhoto("这是一张图片", requestBody).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: 上传成功" );
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Log.d(TAG, "onFailure: 上传失败" + t.getMessage());
            }
        });
    }

    private String putImageToShare(Context context, ImageView photoImageView) {

        BitmapDrawable drawable = (BitmapDrawable) photoImageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream byStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byStream);
        byte[] byteArray = byStream.toByteArray();

        return new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
    }

    /**
     * server启动通知回调
     * @param ip
     */
    public void onServerStart(String ip) {
        closeDialog();
        mStart.setVisibility(View.GONE);
        mStop.setVisibility(View.VISIBLE);
        mBrowser.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(ip)) {
            List<String> addressList = new ArrayList<>();
            mRootUrl = "http://" + ip + ":8080/";
            addressList.add(mRootUrl);
            addressList.add("http://" + ip + ":8080/login.html");
            mTvMessage.setText(TextUtils.join("\n", addressList));
        } else {
            mRootUrl = null;
            mTvMessage.setText(R.string.server_ip_error);
        }

    }


    /**
     * server错误通知回调
     * @param error
     */
    public void onServerError(String error) {
        closeDialog();

        mRootUrl = null;
        mStart.setVisibility(View.VISIBLE);
        mStop.setVisibility(View.GONE);
        mBrowser.setVisibility(View.GONE);
        mTvMessage.setText(error);

    }

    /**
     * server停止通知回调
     */
    public void onServerStop() {
        closeDialog();

        mRootUrl = null;
        mStart.setVisibility(View.VISIBLE);
        mStop.setVisibility(View.GONE);
        mBrowser.setVisibility(View.GONE);
        mTvMessage.setText(R.string.server_stop_succeed);

    }

    private void showDialog() {
        if (mDialog == null) {
            mDialog = new LoadingDialog(this);
        }
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    private void closeDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mServerManager.unregister();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}