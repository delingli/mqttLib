package com.ldl.upanepochlog.log;

import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


public class ServiceOpt {
    public static final int START = 1;
    public static final int STOP = 2;
    public static final int SUBMIT = 3;
    public static final int CLEARALLLOG = 4;
    public static final int CLEARAPPLOG = 5;
    public static final int CLEARANRLOG = 6;
    public static final int CLEARCRASHLOG = 7;
    public static final int CLEARSYSTEMLOG = 8;
    public static final int ENABLELOG = 9;
    public static final int GET_LOGS_SPACE = 10;
    @IntDef({START,STOP,SUBMIT,CLEARALLLOG,CLEARAPPLOG,CLEARANRLOG,CLEARCRASHLOG,CLEARSYSTEMLOG,ENABLELOG,GET_LOGS_SPACE})
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    public @interface ServiceOpts {
    }

}
