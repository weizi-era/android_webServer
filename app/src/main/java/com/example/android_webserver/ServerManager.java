package com.example.android_webserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class ServerManager extends BroadcastReceiver {

    private static final String ACTION = "com.yanzhenjie.andserver.receiver";

    private static final String CMD_KEY = "cmd_key";
    private static final String MESSAGE_KEY = "message_key";

    private static final int CMD_VALUE_START = 1;
    private static final int CMD_VALUE_STOP = 2;
    private static final int CMD_VALUE_ERROR = 4;

    private MainActivity mActivity;
    private Intent mService;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (ACTION.equals(action)) {
            int cmd = intent.getIntExtra(CMD_KEY, 0);
            switch (cmd) {
                case CMD_VALUE_START: {
                    String ip = intent.getStringExtra(MESSAGE_KEY);
                    mActivity.onServerStart(ip);
                    break;
                }
                case CMD_VALUE_STOP: {
                    mActivity.onServerStop();
                    break;
                }
                case CMD_VALUE_ERROR: {
                    String error = intent.getStringExtra(MESSAGE_KEY);
                    mActivity.onServerError(error);
                    break;
                }

            }
        }
    }

    public ServerManager(MainActivity activity) {
        this.mActivity = activity;
        mService = new Intent(activity, CoreService.class);
    }

    private static void sendBroadcast(Context context, int cmd, String message) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(CMD_KEY, cmd);
        intent.putExtra(MESSAGE_KEY, message);

        context.sendBroadcast(intent);
    }

    private static void sendBroadcast(Context context) {
        sendBroadcast(context, CMD_VALUE_STOP, null);
    }

    /**
     * 通知server启动
     * @param context
     * @param hostAddress
     */
    public static void onServerStart(Context context, String hostAddress) {
        sendBroadcast(context, CMD_VALUE_START, hostAddress);
    }

    /**
     * 通知server停止
     * @param context
     */
    public static void onServerStop(Context context) {
        sendBroadcast(context);
    }

    /**
     * 通知server发生错误
     * @param context
     * @param error
     */
    public static void onServerError(Context context, String error) {
        sendBroadcast(context, CMD_VALUE_ERROR, error);
    }

    /**
     * 注册广播
     */
    public void register() {
        IntentFilter filter = new IntentFilter(ACTION);
        mActivity.registerReceiver(this, filter);
    }

    /**
     * 移除广播
     */
    public void unregister() {
        mActivity.unregisterReceiver(this);
    }


    public void startServer() {
        mActivity.startService(mService);
    }

    public void stopServer() {
        mActivity.stopService(mService);
    }



}
