package com.ldl.mqttlib.impl;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.ArrayMap;

public class OnlineInforOption implements Parcelable {
    private int type;
    private ArrayMap<String, String> params;

    private OnlineInforOption(OnlineInforOption.MqttPublishOptionBuilder builder) {
        this.type = builder.type;
        this.params = builder.params;
    }


    protected OnlineInforOption(Parcel in) {
        type = in.readInt();
    }

    public static final Creator<OnlineInforOption> CREATOR = new Creator<OnlineInforOption>() {
        @Override
        public OnlineInforOption createFromParcel(Parcel in) {
            return new OnlineInforOption(in);
        }

        @Override
        public OnlineInforOption[] newArray(int size) {
            return new OnlineInforOption[size];
        }
    };

    public int getType() {
        return type;
    }

    public ArrayMap<String, String> getParams() {
        return params;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(type);
    }

    public static class MqttPublishOptionBuilder {
        private int type;
        private ArrayMap<String, String> params;


        public MqttPublishOptionBuilder(int type) {
            this.type = type;
        }


        public OnlineInforOption.MqttPublishOptionBuilder params(ArrayMap<String, String> params) {
            this.params = params;
            return this;
        }
        public OnlineInforOption build() {
            return new OnlineInforOption(this);
        }

    }
}
