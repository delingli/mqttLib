package com.ldl.upanepochlog.api;

import androidx.annotation.IntDef;

public class LogParamsOption {
    private String ip;
    private String device_sn;
    private int port;
    private int dealyTime;

    private LogParamsOption(LogParamsOptionBuilder builder) {
        this.ip = builder.ip;
        this.port = builder.port;
        this.device_sn = builder.device_sn;
        this.dealyTime = builder.dealyTime;
    }

    public String getDeviceSn() {
        return device_sn;
    }

    public String getIp() {
        return ip;
    }


    public int getDealyTime() {
        return dealyTime;
    }


    public int getPort() {
        return port;
    }

    public static class LogParamsOptionBuilder {
        private String ip;
        private String device_sn;
        private int port;
        private int dealyTime;

        public LogParamsOptionBuilder(String ip) {
            this.ip = ip;
        }


        public LogParamsOptionBuilder port(int port) {
            this.port = port;
            return this;
        }

        public LogParamsOptionBuilder dealyTime(@DeayTime int dealyTime) {
            this.dealyTime = dealyTime;
            return this;
        }


        public LogParamsOptionBuilder device_sn(String device_sn) {
            this.device_sn = device_sn;
            return this;
        }


        public LogParamsOption build() {
            return new LogParamsOption(this);
        }

    }
}
