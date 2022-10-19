package com.swinfotech.swpay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.swinfotech.swpay.signin.Login;

public class Session {
    public static final String IS_LOCK_SCREEN_ACTIVE = "is_lock_screen_active";
    public static final String APP_LOCK_PIN = "app_lock_pin";


    // Declare object of sharedPreferences
    // these static variables for use every where you want to check login or get username or password
    public static final String USER_NAME = "user_name";
    public static final String PASSWORD = "password";

    // Aeps SDK Get Details
    public static final String AADHAAR_NO_AEPS = "aadhaar";
    public static final String MOBILE_NO_AEPS = "mobile_no";
    public static final String BANK_ACCOUNT_AEPS = "bank_name";
    public static final String AMOUNT_AEPS = "amt";
    public static final String MESSAGE_AEPS = "message";
    public static final String AEPS_MERCHANT_ID = "id";
    public static final String USER_ID_TWO = "id_1";
    // End AEPS SDK Details

    // this is for edit profile fill details
    public static final String AADHAAR_NO = "aadhaar_no";
    public static final String PAN_NO = "pan_no";
    public static final String PIN_CODE = "pin_code";
    public static final String STATES = "states";
    public static final String DISTRICT = "districts";
    public static final String ADDRESS = "address";
    public static final String KYC_STATUS = "kyc_status";
//    public static final String FIRM_NAME = "firm_name";

//    service id for all
    public static final String SERVICE_ID_RECHARGE = "service_id_re";
    public static final String SERVICE_STATUS_RECHARGE = "status_re";

    public static final String SERVICE_ID_BBPS = "service_id_bbps";
    public static final String SERVICE_STATUS_BBPS = "status_bbps";

    public static final String SERVICE_ID_AEPS = "service_id_aeps";
    public static final String SERVICE_STATUS_AEPS = "status_aeps";

    public static final String SERVICE_ID_DMT = "service_id_dmt";
    public static final String SERVICE_STATUS_DMT = "status_dmt";

    public static final String SERVICE_ID_WALLET = "service_id_wallet";
    public static final String SERVICE_STATUS_WALLET = "status_wallet";

    public static final String SERVICE_ID_UPI = "service_id_upi";
    public static final String SERVICE_STATUS_UPI = "status_upi";

    public static final String SERVICE_ID_CARD = "service_id_card";
    public static final String SERVICE_STATUS_CARD = "status_card";

    public static final String SERVICE_ID_PAYOUT = "service_id_payout";
    public static final String SERVICE_STATUS_PAYOUT = "status_payout";

    public static final String SERVICE_ID_MPOS = "service_id_mpos";
    public static final String SERVICE_STATUS_MPOS = "status_mpos";

    public static final String TPIN = "tpin";
    public static final String IS_LOGIN = "is_login";
    public static final String IS_REMEMBER_ME = "remember_me";
    public static final String LAST_VISIT_TIME = "last_time_visit";
    public static final String USER_ID = "user_id";
    public static final String USER_TYPE = "user_type";
    public static final String TOKEN = "tok";
    public static final String PROFILE_IMG = "img";
    public static final String BALANCE_AMOUNT = "balance_amount";
    public static final String BALANCE_OUTSTANDING = "balance_outstanding";
    public static final String BALANCE_AEPS = "balance_aeps";
    public static final String AEPS_LIMIT = "aeps_limit";
    public static final String AEPS_BALANCE = "aeps_bal";
    public static final String MOBILE = "mobile";
    public static final String USER_EMAIL = "user_email";
    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;
    // Declare preference name
    private final String PREFERENCE_NAME = "SW_PAY";

    public Session(Context context) {
        // get the SharedPreference
        preferences = context.getSharedPreferences(PREFERENCE_NAME, 0);
        // edit the editor to put values in shared preference
        editor = preferences.edit();
    }

    // set the boolean value in SharedPreferences
    public void setBoolean(String PREF_NAME, boolean value) {
        // PREF_NAME is the key to store
        // value is value for key

        // set the value in SharedPreferences
        editor.putBoolean(PREF_NAME, value);
        // commit the editor to make changes in the SharedPreferences
        editor.commit();
    }


    // set the string value in SharedPreferences
    public void setString(String PREF_NAME, String value) {
        editor.putString(PREF_NAME, value);
        editor.commit();
    }

    public String getString(String PREF_NAME) {
        // check if SharedPreferences contains the value or not
        if (preferences.contains(PREF_NAME)) {
            // return the stored value
            return preferences.getString(PREF_NAME, "");
        }
        // if SharedPreferences not contains the value then return empty string
        return "";
    }

    // get the boolean value from SharedPreferences
    public boolean getBoolean(String PREF_NAME) {
        return preferences.getBoolean(PREF_NAME, false);
    }


    public void logout(Context context) {

        boolean isAppLock = getBoolean(IS_LOCK_SCREEN_ACTIVE);
        String appLock = null;
        if (isAppLock) {
            appLock = getString(Session.APP_LOCK_PIN);
        }

        context.getSharedPreferences(PREFERENCE_NAME, 0).edit().clear().commit();

        if (isAppLock) {
            setString(APP_LOCK_PIN, appLock);
            setBoolean(IS_LOCK_SCREEN_ACTIVE, isAppLock);
        }

        Intent intent = new Intent(context, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }



    // mobile clear text interface
    public interface OnClearTextClickListener {
        void onClearClick();
    }

}
