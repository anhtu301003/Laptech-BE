package com.project.LaptechBE.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SubCategoryEnum {
    GAMING("gaming"),
    OFFICE("office"),
    ULTRA_THIN("ultra-thin"),
    TWO_IN_ONE("2-in-1"),
    WORKSTATION("workstation"),
    BUDGET("budget"),
    STUDENT("student"),
    BUSINESS("business");

    private String value;

    SubCategoryEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    // Phương thức để lấy enum từ chuỗi
    @JsonCreator
    public static SubCategoryEnum fromString(String value) {
        for (SubCategoryEnum subCategory : SubCategoryEnum.values()) {
            if (subCategory.getValue().equalsIgnoreCase(value)) {
                return subCategory;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy giá trị enum cho: " + value);
    }
}
