package com.rt.mylibrary.basebean;

import org.jetbrains.annotations.Nullable;

public class LayersBean {
    @Nullable
    private String key;
    @Nullable
    private String name;
    @Nullable
    private String type;
    @Nullable
    private String icon;

    @Nullable
    public final String getKey() {
        return this.key;
    }

    public final void setKey(@Nullable String var1) {
        this.key = var1;
    }

    @Nullable
    public final String getName() {
        return this.name;
    }

    public final void setName(@Nullable String var1) {
        this.name = var1;
    }

    @Nullable
    public final String getType() {
        return this.type;
    }

    public final void setType(@Nullable String var1) {
        this.type = var1;
    }

    @Nullable
    public final String getIcon() {
        return this.icon;
    }

    public final void setIcon(@Nullable String var1) {
        this.icon = var1;
    }
}