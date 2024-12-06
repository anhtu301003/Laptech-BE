package com.project.LaptechBE.untils;

public class Endpoints {
    public static final String API_PREFIX = "/api";

    public static final class User{
        public static final String BASE = "/user";
        public static final String REGISTER = "/register";
        public static final String LOGIN = "/login";
        public static final String UPDATE = "/";
        public static final String LOG_OUT = "/logout";
    }

    public static final class Order{
        public static final String BASE = "/order";
    }

    public static final class Product{
        public static final String BASE = "/product";
    }

    public static final class Cart{
        public static final String BASE = "/cart";
    }
}
