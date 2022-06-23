package com.itc.switchdevicecomponent.impl;

import android.os.Parcel;
import android.os.Parcelable;

import com.itc.switchdevicecomponent.annation.DeviceType;

//这是配置参数
public class SwitchDeviceOption implements Parcelable {
    private String device_sn;
    private String url;
    @DeviceType.DeviceType
    private String deviceType;

    private SwitchDeviceOption(SwitchDeviceOptionBuilder builder) {
        this.device_sn = builder.device_sn;
        this.deviceType = builder.deviceType;
        this.url = builder.url;
    }

    protected SwitchDeviceOption(Parcel in) {
        device_sn = in.readString();
        url = in.readString();
        deviceType = in.readString();
    }

    public static final Creator<SwitchDeviceOption> CREATOR = new Creator<SwitchDeviceOption>() {
        @Override
        public SwitchDeviceOption createFromParcel(Parcel in) {
            return new SwitchDeviceOption(in);
        }

        @Override
        public SwitchDeviceOption[] newArray(int size) {
            return new SwitchDeviceOption[size];
        }
    };

    public String getDevice_sn() {
        return device_sn;
    }

    public String getConfigUrl() {
        return url;
    }

    public String deviceType() {
        return deviceType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(device_sn);
        parcel.writeString(url);
        parcel.writeString(deviceType);
    }


    public static class SwitchDeviceOptionBuilder {
        private String device_sn;
        private String url;
        @DeviceType.DeviceType
        private String deviceType;

        public SwitchDeviceOptionBuilder(@DeviceType.DeviceType String deviceType) {
            this.deviceType = deviceType;
        }

        public SwitchDeviceOptionBuilder url(String url) {
            this.url = url;
            return this;
        }

        public SwitchDeviceOptionBuilder device_sn(String device_sn) {
            this.device_sn = device_sn;
            return this;
        }

        public SwitchDeviceOption build() {
            return new SwitchDeviceOption(this);
        }

    }
}
