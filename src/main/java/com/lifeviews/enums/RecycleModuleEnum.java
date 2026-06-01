package com.lifeviews.enums;

import java.util.Arrays;

public enum RecycleModuleEnum {

    DIARY("diary"),
    PHOTO("photo"),
    FOOD("food"),
    TRAVEL("travel"),
    MOVIE("movie"),
    JOURNAL("journal");

    private final String code;

    RecycleModuleEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static RecycleModuleEnum fromCode(String code) {
        return Arrays.stream(values())
                .filter(module -> module.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的回收站模块"));
    }
}
