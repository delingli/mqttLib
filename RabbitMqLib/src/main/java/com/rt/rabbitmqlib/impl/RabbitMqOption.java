package com.rt.rabbitmqlib.impl;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

//这是配置参数 用户名密码一类的
public class RabbitMqOption implements Parcelable {
    private String host;
    private String username;
    private String password;
    private int port;

    private RabbitMqOption(RabitMqOptionBuilder builder) {
        this.host = builder.host;
        this.username = builder.username;
        this.password = builder.password;
        this.port = builder.port;
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

    public int getPort() {
        return port;
    }





    public static class RabitMqOptionBuilder   {
        private String host;
        private String username;
        private String password;
        private int port;

        public RabitMqOptionBuilder(String host) {
            this.host = host;
        }


        public RabitMqOptionBuilder username(String username) {
            this.username = username;
            return this;
        }

        public RabitMqOptionBuilder password(String password) {
            this.password = password;
            return this;
        }

        public RabitMqOptionBuilder port(int port) {
            this.port = port;
            return this;
        }

        public RabbitMqOption build() {
            return new RabbitMqOption(this);
        }

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.host);
        dest.writeString(this.username);
        dest.writeString(this.password);
        dest.writeInt(this.port);
    }

    public void readFromParcel(Parcel source) {
        this.host = source.readString();
        this.username = source.readString();
        this.password = source.readString();
        this.port = source.readInt();
    }

    protected RabbitMqOption(Parcel in) {
        this.host = in.readString();
        this.username = in.readString();
        this.password = in.readString();
        this.port = in.readInt();
    }

    public static final Parcelable.Creator<RabbitMqOption> CREATOR = new Parcelable.Creator<RabbitMqOption>() {
        @Override
        public RabbitMqOption createFromParcel(Parcel source) {
            return new RabbitMqOption(source);
        }

        @Override
        public RabbitMqOption[] newArray(int size) {
            return new RabbitMqOption[size];
        }
    };
}

