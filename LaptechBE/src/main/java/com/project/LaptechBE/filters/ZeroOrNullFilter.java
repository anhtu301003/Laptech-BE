package com.project.LaptechBE.filters;

public class ZeroOrNullFilter {
    @Override
    public boolean equals(Object value) {
        if (value == null) {
            return true; // Không hiển thị nếu là null
        }
        if (value instanceof Integer) {
            return (Integer) value == 0; // Không hiển thị nếu là 0
        }
        return false;
    }
}
