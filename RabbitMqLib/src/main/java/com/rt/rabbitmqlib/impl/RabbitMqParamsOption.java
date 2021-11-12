package com.rt.rabbitmqlib.impl;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.ArrayMap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RabbitMqParamsOption implements Parcelable {
    private ArrayMap<String, String> params;
    private RabbitMqParamsOption(RabbitMqParamsOptionBuilder builder) {
        this.params = builder.params;
    }
    public ArrayMap<String, String> getParams() {
        return params;
    }

    public static class RabbitMqParamsOptionBuilder   {
        private ArrayMap<String, String> params;

        public RabbitMqParamsOptionBuilder() {
        }
        public RabbitMqParamsOptionBuilder params(ArrayMap<String, String> params) {
            this.params = params;
            return this;
        }
        public RabbitMqParamsOption build() {
            return new RabbitMqParamsOption(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.params.size());
        for (Map.Entry<String, String> entry : this.params.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
    }

    public void readFromParcel(Parcel source) {
        int paramsSize = source.readInt();
        this.params = new ArrayMap<String, String>(paramsSize);
        for (int i = 0; i < paramsSize; i++) {
            String key = source.readString();
            String value = source.readString();
            this.params.put(key, value);
        }
    }

    protected RabbitMqParamsOption(Parcel in) {
        int paramsSize = in.readInt();
        this.params = new ArrayMap<String, String>(paramsSize);
        for (int i = 0; i < paramsSize; i++) {
            String key = in.readString();
            String value = in.readString();
            this.params.put(key, value);
        }
    }

    public static final Parcelable.Creator<RabbitMqParamsOption> CREATOR = new Parcelable.Creator<RabbitMqParamsOption>() {
        @Override
        public RabbitMqParamsOption createFromParcel(Parcel source) {
            return new RabbitMqParamsOption(source);
        }

        @Override
        public RabbitMqParamsOption[] newArray(int size) {
            return new RabbitMqParamsOption[size];
        }
    };
}
