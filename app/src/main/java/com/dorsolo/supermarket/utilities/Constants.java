package com.dorsolo.supermarket.utilities;

import com.dorsolo.supermarket.R;

/**
 * Constants class contains subClasses with constants that are used all across the app, It helps managing
 * all of the constants from one place. The constants are originated based on there relevance which provide
 * easy and understandable access to them from different sections of the app
 */
public final class Constants {

    /**
     * Credentials constants
     */
    public static final class CredentialsConstants {
        public static final int MIN_EMAIL_LENGTH = 6;
        public static final int MIN_PASSWORD_LENGTH = 6;
        public static final String MODE = "MODE";
    }

    /**
     * User constants
     */
    public static final class UserConstants {
        public static final String USERNAME = "USERNAME";
        public static final String PASSWORD = "PASSWORD";
        public static final String EMAIL = "EMAIL";
        public static final String PHONE_NUMBER = "PHONE NUMBER";
        public static final String PROFILE_IMAGE = "PROFILE IMAGE";
        public static final String NUM_OF_PRODUCTS = "NUM OF PRODUCTS";
        public static final String IMAGES = "IMAGES";
        public static final String CLIENT_IMAGES_AMOUNT = "CLIENT IMAGES AMOUNT";
        public static final String TOTAL_IMAGES_AMOUNT = "TOTAL IMAGES AMOUNT";
    }

    /**
     * User update constants
     */
    public static final class UserUpdateConstants {
        public static final String DATA = "DATA";
        public static final String CODE = "CODE";
        public static final String UPDATE_PROFILE_IMAGE = "1";
        public static final String UPDATE_USERNAME = "2";
        public static final String UPDATE_PHONE_NUMBER = "3";
        public static final String UPDATE_NUM_OF_PRODUCTS = "4";
    }

    /**
     * Image constants
     */
    public static final class ImageConstants {
        public static final String TITLE = "TITLE";
        public static final String DESCRIPTION = "DESCRIPTION";
        public static final String IMAGE = "IMAGE";
        public static final int HIGH_QUALITY = 100;
        public static final int MID_QUALITY = 90;
    }

    /**
     * Networking constants
     */
    public static final class NetworkingConstants {
        public static final String POST = "POST";
        public static final String GET = "GET";
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String APPLICATION_JSON = "application/json";
        public static final int MIN_BUFFER_SIZE = 512;
        public static final int MAX_BUFFER_SIZE = 3072;
        public static final int THREAD_POOL_SIZE = 3;
        private static final String BASE_URL = "http://35.204.176.177/forSale3/";
        public static final String CREDENTIALS_URL = BASE_URL + "Credentials";
        public static final String HOME_URL = BASE_URL + "Home";
        public static final String POST_URL = BASE_URL + "Post";
    }

    /**
     * Responses Constants
     */
    public static final class ResponsesConstants {
        public static final String SUCCESS = "SUCCESS";
    }

    /**
     * ViewPager constants
     */
    public static final class ViewPagerConstants {
        public static final String FIRST_TIMER = "FIRST TIMER";
        public static final int PAGER_SLIDER_COUNT = 3;
    }

    /**
     * Home constants
     */
    public static final class HomeConstants {
        public static final int NUM_OF_HOME_VIEW_PAGER_SLIDERS = 2;
        public static final int[] icons = {
                R.drawable.ic_grid,
                R.drawable.ic_view_headline
        };
        public static final int HOME_VIEW_PAGER_GRID_SPAN_COUNT = 3;
        public static final int HOME_VIEW_PAGER_GRID_ITEM_SPAN_COUNT = 1;
    }

    /**
     * Retrieving data And external actions Constants
     */
    public static final class CodesConstants {
        public static final int PHONE_CALL_CODE = 1;
        public static final int THUMBNAIL_CAMERA_CODE = 2;
        public static final int CAMERA_CODE = 3;
        public static final int GALLERY_CODE = 4;
        public static final String CAPTURED_IMAGE = "data";
        public static final String TEXT = ".txt";
        public static final String TRANSITION = "TRANSITION";
        static final String JPEG = ".jpg";
    }

    /**
     * Post constants
     */
    public static final class PostConstants {
        public static final int REQ_WIDTH = 680;
        public static final int REQ_HEIGHT = 680;
    }

    /**
     * Database constants
     */
    public static final class DatabaseConstants {
        public static final String DATABASE_NAME = "Users";
    }
}