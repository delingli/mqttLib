package com.rt.rabbitmqlib.impl;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.ArrayMap;

public class RabbitMqParamsOption implements Parcelable {
    private ArrayMap<String, String> params;

    private RabbitMqParamsOption(RabbitMqParamsOptionBuilder builder) {
        this.params = builder.params;
    }

    protected RabbitMqParamsOption(Parcel in) {
    }

    public static final Creator<RabbitMqParamsOption> CREATOR = new Creator<RabbitMqParamsOption>() {
        @Override
        public RabbitMqParamsOption createFromParcel(Parcel in) {
            return new RabbitMqParamsOption(in);
        }

        @Override
        public RabbitMqParamsOption[] newArray(int size) {
            return new RabbitMqParamsOption[size];
        }
    };

    public ArrayMap<String, String> getParams() {
        return params;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }


    public static class RabbitMqParamsOptionBuilder {
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
}
