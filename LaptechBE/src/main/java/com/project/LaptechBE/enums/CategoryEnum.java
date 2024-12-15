package com.project.LaptechBE.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CategoryEnum {
    LAPTOP("laptop"),
    PC("pc"),
    PHONE("phone"),
    ACCESSORY("accessory"),
    TABLET("tablet"),
    OTHER("other");  // Sửa "ORTHER" thành "OTHER"

    private String value;

    // Constructor để khởi tạo giá trị chuỗi cho mỗi enum
    CategoryEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    // Phương thức để lấy enum từ chuỗi
    @JsonCreator
    public static CategoryEnum fromString(String value) {
        for (CategoryEnum category : CategoryEnum.values()) {
            if (category.getValue().equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy giá trị enum cho: " + value);
    }
}

