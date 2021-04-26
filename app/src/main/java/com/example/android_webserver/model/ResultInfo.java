package com.example.android_webserver.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

public class ResultInfo implements Parcelable {

    @JSONField(name = "isSuccess")
    private boolean isSuccess;

    @JSONField(name = "resultCode")
    private int resultCode;

    @JSONField(name = "errorMsg")
    private String errorMsg;

    @JSONField(name = "data")
    private Object data;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ResultInfo() {

    }

    protected ResultInfo(Parcel in) {
        isSuccess = in.readByte() != 0;
        resultCode = in.readInt();
        errorMsg = in.readString();
    }

    public static final Creator<ResultInfo> CREATOR = new Creator<ResultInfo>() {
        @Override
        public ResultInfo createFromParcel(Parcel in) {
            return new ResultInfo(in);
        }

        @Override
        public ResultInfo[] newArray(int size) {
            return new ResultInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isSuccess ? 1 : 0));
        dest.writeInt(resultCode);
        dest.writeString(errorMsg);
    }
}
