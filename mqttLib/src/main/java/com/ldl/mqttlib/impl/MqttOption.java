package com.ldl.mqttlib.impl;

import android.os.Parcel;
import android.os.Parcelable;

//这是配置参数
public class MqttOption implements Parcelable {
    private String host;
    private String username;
    private String password;
    private String publish_topid;
    private String response_topid;
    private String clientid;
    private String device_sn;
    private boolean retained;

    private MqttOption(MqttOptionBuilder builder) {
        this.host = builder.host;
        this.username = builder.username;
        this.password = builder.password;
        this.publish_topid = builder.publish_topid;
        this.response_topid = builder.response_topid;
        this.clientid = builder.clientId;
        this.retained = builder.retained;
        this.device_sn = builder.device_sn;
    }

    protected MqttOption(Parcel in) {
        host = in.readString();
        username = in.readString();
        password = in.readString();
        publish_topid = in.readString();
        response_topid = in.readString();
        clientid = in.readString();
        device_sn = in.readString();
        retained = in.readByte() != 0;
    }

    public static final Creator<MqttOption> CREATOR = new Creator<MqttOption>() {
        @Override
        public MqttOption createFromParcel(Parcel in) {
            return new MqttOption(in);
        }

        @Override
        public MqttOption[] newArray(int size) {
            return new MqttOption[size];
        }
    };

    public String getDevice_sn() {
        return device_sn;
    }

    public boolean isRetained() {
        return retained;
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPublish_topid() {
        return publish_topid;
    }

    public String getResponse_topid() {
        return response_topid;
    }

    public String getClientid() {
        return clientid;
    }

    public boolean getRetained() {
        return retained;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(host);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(publish_topid);
        dest.writeString(response_topid);
        dest.writeString(clientid);
        dest.writeString(device_sn);
        dest.writeByte((byte) (retained ? 1 : 0));
    }

    public static class MqttOptionBuilder {
        private String host;
        private String username;
        private String password;
        private String publish_topid;
        private String response_topid;
        private String clientId;
        private String device_sn;
        private boolean retained;


        public MqttOptionBuilder(String host) {
            this.host = host;
        }


        public MqttOptionBuilder retained(boolean retained) {
            this.retained = retained;
            return this;
        }

        public MqttOptionBuilder device_sn(String device_sn) {
            this.device_sn = device_sn;
            return this;
        }

        public MqttOptionBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public MqttOptionBuilder username(String username) {
            this.username = username;
            return this;
        }

        public MqttOptionBuilder password(String password) {
            this.password = password;
            return this;
        }

        public MqttOptionBuilder response_topid(String response_topid) {
            this.response_topid = response_topid;
            return this;
        }

        public MqttOptionBuilder publish_topid(String publish_topid) {
            this.publish_topid = publish_topid;
            return this;
        }

        public MqttOption build() {
            return new MqttOption(this);
        }
    }
}
