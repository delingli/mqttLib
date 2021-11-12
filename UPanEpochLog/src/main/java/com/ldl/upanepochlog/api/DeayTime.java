package com.ldl.upanepochlog.api;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({5, 30, 60, 90, 120})
@Retention(RetentionPolicy.SOURCE)
public @interface DeayTime {
}
