package io.howeveryir.auroramall.common;

import java.io.File;

public class Constants {
    private static final String DEFAULT_UPLOAD_DIR = System.getProperty("user.home")
            + File.separator + ".aurora-mall"
            + File.separator + "upload"
            + File.separator;

    public static final String FILE_UPLOAD_DIC = resolveUploadDir();

    public static final int INDEX_CAROUSEL_NUMBER = 5;
    public static final int INDEX_CATEGORY_NUMBER = 10;
    public static final int SEARCH_CATEGORY_NUMBER = 8;
    public static final int INDEX_GOODS_HOT_NUMBER = 4;
    public static final int INDEX_GOODS_NEW_NUMBER = 5;
    public static final int INDEX_GOODS_RECOMMOND_NUMBER = 10;
    public static final int SHOPPING_CART_ITEM_TOTAL_NUMBER = 13;
    public static final int SHOPPING_CART_ITEM_LIMIT_NUMBER = 5;
    public static final String MALL_VERIFY_CODE_KEY = "mallVerifyCode";
    public static final String MALL_USER_SESSION_KEY = "auroraMallUser";
    public static final int GOODS_SEARCH_PAGE_LIMIT = 10;
    public static final int ORDER_SEARCH_PAGE_LIMIT = 3;
    public static final int SELL_STATUS_UP = 0;
    public static final int SELL_STATUS_DOWN = 1;

    private Constants() {
    }

    private static String resolveUploadDir() {
        String envValue = System.getenv("APP_UPLOAD_DIR");
        if (envValue == null || envValue.trim().isEmpty()) {
            return DEFAULT_UPLOAD_DIR;
        }
        return envValue.endsWith(File.separator) ? envValue : envValue + File.separator;
    }
}
