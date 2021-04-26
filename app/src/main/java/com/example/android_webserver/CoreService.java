package com.example.android_webserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.android_webserver.util.NetUtils;
import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

public class CoreService extends Service {

    private static final String TAG = CoreService.class.getSimpleName();

    private Server mServer;

    @Override
    public void onCreate() {
        super.onCreate();
        mServer = AndServer.webServer(this)
                .port(8080)
                .timeout(10, TimeUnit.SECONDS)
                .listener(new Server.ServerListener() {
                    @Override
                    public void onStarted() {
                        InetAddress address = NetUtils.getLocalIPAddress();
                        if (address == null) {
                            Log.e(TAG, "onStartedService: local ip address id null");
                            throw new NullPointerException();
                        }
                        ServerManager.onServerStart(CoreService.this, address.getHostAddress());
                    }

                    @Override
                    public void onStopped() {
                        ServerManager.onServerStop(CoreService.this);
                    }

                    @Override
                    public void onException(Exception e) {
                        e.printStackTrace();
                        ServerManager.onServerError(CoreService.this, e.getMessage());
                    }
                }).build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startServer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopServer();
        super.onDestroy();
    }

    /**
     * 启动服务
     */
    private void startServer() {
        mServer.startup();
    }

    /**
     * 停止服务
     */
    private void stopServer() {
        mServer.shutdown();
    }
}
