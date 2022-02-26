package com.evcharginapp.network;

public class NetworkURL {

    public static String BASE_URL="https://datageniustechus.com:3008/";
    public static String LOGIN_URL=BASE_URL+"doLogin";
    public static String REGISTER_URL=BASE_URL+"doSignup";
    public static String GET_STATE_URL=BASE_URL+"getAllStates";
    public static String GET_ZIP_URL=BASE_URL+"getAllZipcodes";
    public static String GET_EVLIST_URL=BASE_URL+"getChargingStationsData";
    public static String ADD_EV_URL=BASE_URL+"saveChargingStationsData";
    public static String ADD_FAVEV_URL=BASE_URL+"saveFavouriteStation";
    public static String GET_FAVEV_URL=BASE_URL+"getFavouriteStations";
}
