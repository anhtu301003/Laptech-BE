package com.project.LaptechBE.untils;

public class Endpoints {
    public static final String API_PREFIX = "/api";

    public static final class User{
        public static final String BASE = "/user";
        public static final String REGISTER = "/register";
        public static final String LOGIN = "/login";
        public static final String UPDATE = "/";
        public static final String REFRESHTOKEN = "/refresh-token";
    }

    public static final class Order{
        public static final String BASE = "/order";
    }

    public static final class Product{
        public static final String BASE = "/product";
        public static final String CREATEPRODUCT = "";
        public static final String CREATEBULKPRODUCTS = "/bulk";
        public static final String GETPRODUCTS = "/";
        public static final String GETALLCATEGORY = "/category/";
        public static final String GETPRODUCTBYID = "/";
        public static final String UPDATEPRODUCT = "/";
        public static final String DELETEPRODUCT = "/";
    }

    public static final class Cart{
        public static final String BASE = "/cart";
    }

    public static final class Review{
        public static final String BASE = "/review";
    }
}
