package com.swinfotech.swpay;

import java.util.Date;

public class Constant {
    // here define all constants use in application

    // here  we define the commission images base url
    public static final String COMMISSION_IMAGES_BASE_URL = "https://portal.swpay.in";

    // this is volley timeout in milli seconds change it according to your need
    public static final int VOLLEY_TIMEOUT = 1000 * 30; // this means 30 seconds

    public static final String GET_PIN_CODE_OUR = "https://mobile.swpay.in/api/PostalPin/GetStateDist?";
    public static final String LOGIN = "https://mobile.swpay.in/api/AndroidAPI/AndroidLogin";
    public static final String DASHBOARD_USER_INFO = "https://mobile.swpay.in/api/AndroidAPI/DashboardUserInfo";
    public static final String RECHARGE_API = "https://mobile.swpay.in/api/AndroidAPI/RechargeAPI";
    public static final String SIGNUP_API = "https://mobile.swpay.in/api/AndroidAPI/SignUp";
    public static final String ADD_USER = "https://mobile.swpay.in/api/AndroidAPI/AddEditUsers";
    public static final String GET_PIN_CODE = "https://api.postalpincode.in/pincode/";
    public static final String GET_SCHEME = "https://mobile.swpay.in/api/AndroidAPI/GetSchemeAlloted";
    public static final String PAYMENT_REQUEST = "https://mobile.swpay.in/api/AndroidAPI/PaymentRequest?";
    public static final String CHANGE_PASSWORD = "https://mobile.swpay.in/api/AndroidAPI/PasswordUpInner?";
    public static final String CHANGE_TPIN = "https://mobile.swpay.in/api/AndroidAPI/TpinUpInner?";
    public static final String BBPS_FETCH_BILL = "https://mobile.swpay.in/api/AndroidAPI/BbpsFetchBill?";
    public static final String BBPS_Bill_Payment = "https://mobile.swpay.in/api/AndroidAPI/BillPaymentTr?";
    public static final String DMT_GET_BEN = "https://mobile.swpay.in/api/AndroidAPI/DMTGetBen?";
    public static final String DMT_VERIFY_BENE = "https://mobile.swpay.in/api/AndroidAPI/DMTVerifyBen?";
    public static final String DMT_ADD_BENE = "https://mobile.swpay.in/api/AndroidAPI/DMTAddBen";
    public static final String BENE_SENT_OTP = "https://mobile.swpay.in/api/AndroidAPI/DMTDelOtp?";
    public static final String DTM_DELETE_BENE = "https://mobile.swpay.in/api/AndroidAPI/DMTDelBen?";
    public static final String DTM_MONEY_TRANSFER = "https://mobile.swpay.in/api/AndroidAPI/DMTDoTransaction";
    public static final String DTM_ADD_REMITTER = "https://mobile.swpay.in/api/AndroidAPI/DMTAddRemitterOTP?";
    public static final String DTM_OTP_REMITTER = "https://mobile.swpay.in/api/AndroidAPI/DMTAddRemitter";
    public static final String DTM_BANK_LIST = "https://mobile.swpay.in/api/AndroidAPI/DMTBankList?";
    //    public static final String FORGOT_PASS= "https://mobile.swpay.in/api/Users/PassSendOTP?";
    public static final String FORGOT_PASS = "https://mobile.swpay.in/api/Users/PassSendOTP";
    public static final String FORGOT_PASS_OTP = "https://mobile.swpay.in/api/Users/OTPBasedResetPass?";
    public static final String GET_KYC_DOC = "https://mobile.swpay.in/api/AndroidAPI/UserKycFetch?";
    public static final String GET_AEPS_BENE_LIST = "https://mobile.swpay.in/api/AndroidAPI/AEPSGetBenef?";
    public static final String GET_DMTCF_BENF_LIST = "https://swpay.in/api/AndroidAPI/DMTCFGetBenef?";
    public static final String DEL_DMTCF_BENF = "https://swpay.in/api/AndroidAPI/DMTCFDelBenef";

    // plans API
    public static final String PLANS_API = "https://mobile.swpay.in/api/AndroidAPI/RPlansMobile?";
    public static final String OFFER_API = "https://mobile.swpay.in/api/AndroidAPI/ROfferMobile?";
    public static final String DTH_OFFER_API = "https://mobile.swpay.in/api/AndroidAPI/ROffDTHCustInfo?";
    public static final String POSTPAID_BSNL_INFO_API = "https://mobile.swpay.in/api/AndroidAPI/ROfferBSNL?";
    public static  boolean ISCF = false;
    public static boolean IS_AEPS_LIMIT_EXCEED = false;
    public static boolean ISBETA = false;
    public static boolean ISALPHA = false;

    public static String RECHARGE_AMOUNT = "";

    public static final String SLIDER_IMAGE_API = "https://mobile.swpay.in/api/AndroidAPI/AndroidImage?";
    public static final String HELP_DESK_API = "https://mobile.swpay.in/api/AndroidAPI/AndroidHelpDesk?";

    // DMT Beta APIs
    public static final String DMT_GET_BEN_BETA = "https://mobile.swpay.in/api/AndroidAPI/DMTGetBenBeta?";
    public static final String DMT_GET_OTP_BETA = "https://mobile.swpay.in/api/AndroidAPI/DMTAddRemitterBeta?";
    public static final String DMT_SENT_REMITTER_OTP_BETA = "https://mobile.swpay.in/api/AndroidAPI/DMTRemAddOTP?";
    public static final String DMT_VERIFY_BETA = "https://mobile.swpay.in/api/AndroidAPI/DMTFetchDBBen?";


    public static String getDate() {
        try {
//            Calendar cal = Calendar.getInstance();
//            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
//            String strDate = sdf.format(cal.getTime());
//            System.out.println("Current date in String Format: " + strDate);
//
//            SimpleDateFormat sdf1 = new SimpleDateFormat();
//            sdf1.applyPattern("dd/MM/yyyy HH:mm:a");
//            Date date = sdf1.parse(strDate);
//            String string = sdf1.format(date);
//            System.out.println("Current date in Date Format: " + string);

            // get the current date and time from
            String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());

//            String pattern = "dd/MM/YYYY hh:mm:a";
//            String dateInString = new SimpleDateFormat(pattern).format(new Date());
            return currentDateTimeString;
        } catch (Exception e) {
            return "";
        }
    }
}
