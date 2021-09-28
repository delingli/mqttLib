package com.rt.rabbitmqlib.impl;

import android.os.Parcel;
import android.os.Parcelable;

//这是配置参数 用户名密码一类的
public class RabbitMqOption implements Parcelable{
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


    protected RabbitMqOption(Parcel in) {
        host = in.readString();
        username = in.readString();
        password = in.readString();
        port = in.readInt();
    }

    public static final Creator<RabbitMqOption> CREATOR = new Creator<RabbitMqOption>() {
        @Override
        public RabbitMqOption createFromParcel(Parcel in) {
            return new RabbitMqOption(in);
        }

        @Override
        public RabbitMqOption[] newArray(int size) {
            return new RabbitMqOption[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(host);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeInt(port);
    }


    public static class RabitMqOptionBuilder {
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


}

