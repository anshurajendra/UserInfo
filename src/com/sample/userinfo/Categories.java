package com.sample.userinfo;

import java.util.Set;
import java.util.TreeSet;

public class Categories {
    public static final Set<Category> categories = new TreeSet<Category>();

    public static final String FINANCE = "Finance";
    public static final String ENTERTAINMENT = "Entertainment";
    public static final String ERETAIL = "ERetail";
    public static final String SPORTS = "Sports";
    public static final String EDUCATIONAL = "Educational";
    public static final String HEALTH = "Health";
    public static final String SOCIAL = "Social";
    public static final String TRAVEL = "Travel";
    public static final String USER_PROFILE = "User Profile";
    public static final String TOP_FRIENDS = "Top friends";
    public static final String TOP_APPS = "Top apps";

    static {
        categories.add(new Category(FINANCE));
        categories.add(new Category(ENTERTAINMENT));
        categories.add(new Category(ERETAIL));
        categories.add(new Category(SPORTS));
        categories.add(new Category(EDUCATIONAL));
        categories.add(new Category(HEALTH));
        categories.add(new Category(SOCIAL));
        categories.add(new Category(TRAVEL));
        categories.add(new Category(USER_PROFILE));
        categories.add(new Category(TOP_FRIENDS));
        categories.add(new Category(TOP_APPS));
    }

    public static class Category {
        private String category = null;

        public Category(String category) {
            this.category = category;
        }
        public String getCategory() {
            return this.category;
        }
        @Override
        public String toString() {
            return this.category;
        }

    }
}