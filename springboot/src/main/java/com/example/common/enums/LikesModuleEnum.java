package com.example.common.enums;

import org.springframework.stereotype.Component;

public enum LikesModuleEnum {
    Blog("新闻"),
    Activity("活动");

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    LikesModuleEnum(String value) {
        this.value = value;
    }
}
