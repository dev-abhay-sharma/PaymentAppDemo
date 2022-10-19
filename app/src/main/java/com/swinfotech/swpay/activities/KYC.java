package com.swinfotech.swpay.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.databinding.ActivityKycBinding;
import com.swinfotech.swpay.model.VolleyMultipartRequest;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class KYC extends AppCompatActivity {

    ActivityKycBinding binding;
    KYC activity;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    Session session;

    Bitmap image;
    String encodedImageString;
    public static final int VOLLEY_TIMEOUT_KYC = 1000 * 60;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKycBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        session = new Session(activity);

        onclick();

        getDocWithAPI();

        binding.kycSubmit.setOnClickListener(v -> {
//            if (binding.kycUserImage.getDrawable() == null) {
//                Toast.makeText(activity, "Please Upload Profile Picture", Toast.LENGTH_SHORT).show();
//            } else if (binding.kycAadhaarImg.getDrawable() == null) {
//                Toast.makeText(activity, "Please Upload Aadhar First Page", Toast.LENGTH_SHORT).show();
//            } else if (binding.kycAadhaarSecondImg.getDrawable() == null) {
//                Toast.makeText(activity, "Please Upload Aadhar Second Page", Toast.LENGTH_SHORT).show();
//            } else if (binding.kycPanImg.getDrawable() == null) {
//                Toast.makeText(activity, "Please Upload Pan Card", Toast.LENGTH_SHORT).show();
//            } else {
//
//                kycAPI1();
//            }

//            kycAPI1();
            finish();
            Toast.makeText(activity, "Upload Done", Toast.LENGTH_SHORT).show();

        });

    }

    private void getDocWithAPI() {
        showLoadingDialog();
        String url = "username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&tok=" + session.getString(Session.TOKEN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.GET_KYC_DOC + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);

                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.has("GetKycDoc")) {
                                JSONObject object = obj.getJSONObject("GetKycDoc");

//                            JSONObject object = obj.getJSONObject("GetKycDoc");

                                String profileImg = "";
                                if (object.has("profile_pic")) {
                                    profileImg = object.getString("profile_pic");
                                }

                                String panCard = "";
                                if (object.has("pan_card")) {
                                    panCard = object.getString("pan_card");
                                }

                                String aadharFront = "";
                                if (object.has("aadhar_front")) {
                                    aadharFront = object.getString("aadhar_front");
                                }

                                String aadharBack = "";
                                if (object.has("aadhar_back")) {
                                    aadharBack = object.getString("aadhar_back");
                                }

                                String profileImage = "https://swpay.in/" + profileImg;
                                String panCardImg = "https://swpay.in/" + panCard;
                                String aadhaarImg = "https://swpay.in/" + aadharFront;
                                String aadhaarBackImg = "https://swpay.in/" + aadharBack;

                                Glide.with(activity).load(profileImage).into(binding.kycUserImage);
                                Glide.with(activity).load(panCardImg).into(binding.kycPanImg);
                                Glide.with(activity).load(aadhaarImg).into(binding.kycAadhaarImg);
                                Glide.with(activity).load(aadhaarBackImg).into(binding.kycAadhaarSecondImg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoadingDialog();
                Toast.makeText(activity, "Something went wrong !", Toast.LENGTH_SHORT).show();
                System.out.println("Something went wrong !");
            }
        });

//        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(activity));
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

    }


//    private void saveProfileAccount() {
//        // loading or check internet connection or something...
//        // ... then
//        showLoadingDialog();
//        String url = "https://mobile.swpay.in/api/AndroidAPI/UserKycUpload";
//        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
//            @Override
//            public void onResponse(NetworkResponse response) {
//                hideLoadingDialog();
//                String resultResponse = new String(response.data);
//                try {
//                    JSONObject result = new JSONObject(resultResponse);
//
//                    String status = "";
//                    if (result.has("Status")){
//                        status = result.getString("Status");
//                    }
//
//                    if (status.equalsIgnoreCase("200")) {
//                        Toast.makeText(activity, "KYC Upload Successfully !", Toast.LENGTH_SHORT).show();
//                        System.out.println("KYC Upload Successfully !");
//                        finish();
//                    } else {
//                        System.out.println("Something went wrong !!!!!!!!!!!!");
//                        Toast.makeText(activity, "Something went wrong !", Toast.LENGTH_SHORT).show();
//                    }
//
////                    String status = result.getString("status");
////                    String message = result.getString("message");
//
////                    if (status.equals(Constant.REQUEST_SUCCESS)) {
////                        // tell everybody you have success upload image and post strings
////                        Log.i("Message", message);
////                    } else {
////                        Log.i("Unexpected", message);
////                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                hideLoadingDialog();
//                NetworkResponse networkResponse = error.networkResponse;
//                Toast.makeText(activity, "Error " + error, Toast.LENGTH_SHORT).show();
////                String errorMessage = "Unknown error";
////                if (networkResponse == null) {
////                    if (error.getClass().equals(TimeoutError.class)) {
////                        errorMessage = "Request timeout";
////                    } else if (error.getClass().equals(NoConnectionError.class)) {
////                        errorMessage = "Failed to connect server";
////                    }
////                } else {
////                    String result = new String(networkResponse.data);
////                    try {
////                        JSONObject response = new JSONObject(result);
////                        String status = response.getString("status");
////                        String message = response.getString("message");
//////                        String message = "do again";
////
////                        Log.e("Error Status", status);
//////                        Log.e("Error Message", message);
////
////                        if (networkResponse.statusCode == 404) {
////                            errorMessage = "Resource not found";
////                        } else if (networkResponse.statusCode == 401) {
////                            errorMessage = message+" Please login again";
////                        } else if (networkResponse.statusCode == 400) {
////                            errorMessage = message+ " Check your inputs";
////                        } else if (networkResponse.statusCode == 500) {
////                            errorMessage = message+" Something is getting wrong";
////                        }
////                    } catch (JSONException e) {
////                        e.printStackTrace();
////                    }
////                }
////                Log.i("Error", errorMessage);
//                error.printStackTrace();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
////                params.put("api_token", "gh659gjhvdyudo973823tt9gvjf7i6ric75r76");
//                params.put("username", session.getString(Session.MOBILE));
//                params.put("password", session.getString(Session.PASSWORD));
////                params.put("about", mAboutInput.getText().toString());
////                params.put("contact", mContactInput.getText().toString());
//                return params;
//            }
//
//            @Override
//            protected Map<String, DataPart> getByteData() {
//                Map<String, DataPart> params = new HashMap<>();
//                // file name could found file base or direct access from real path
//                // for now just get bitmap data from ImageView
////                params.put("avatar", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), mAvatarImage.getDrawable()), "image/jpeg"));
//                params.put("profile", new DataPart("profile.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), binding.kycUserImage.getDrawable()), "image/jpeg"));
//                params.put("pan_card", new DataPart("pan.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), binding.kycPanImg.getDrawable()), "image/jpeg"));
//                params.put("front_aadhar", new DataPart("front.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), binding.kycAadhaarImg.getDrawable()), "image/jpeg"));
//                params.put("back_aadhar", new DataPart("back.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), binding.kycAadhaarSecondImg.getDrawable()), "image/jpeg"));
//
//                return params;
//            }
//        };
//
////        RequestQueue requestQueue = Volley.newRequestQueue(activity);
////        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
////                VOLLEY_TIMEOUT_KYC,
////                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
////                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
////        requestQueue.add(multipartRequest);
//        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
//    }


    // old
    private void kycAPI1() {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/UserKycUpload";
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideLoadingDialog();
                        System.out.println(response);

                        try {
                            JSONObject object = new JSONObject(new String(response.data));

                            String status = "";
                            if (object.has("Status")){
                                status = object.getString("Status");
                            }

                            if (status.equalsIgnoreCase("200")) {
                                Toast.makeText(activity, "KYC Upload Successfully !", Toast.LENGTH_SHORT).show();
                                System.out.println("KYC Upload Successfully !");
                                finish();
                            } else {
                                System.out.println("Something went wrong !!!!!!!!!!!!");
                                Toast.makeText(activity, "Something went wrong !", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoadingDialog();
                System.out.println(error);
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                    Toast.makeText(context, context.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show();
                    System.out.println("Time Out Error");
                } else if (error instanceof AuthFailureError) {
                    System.out.println("AuthFail Error");
                    //TODO
                } else if (error instanceof ServerError) {
                    System.out.println("Server Error");
                    //TODO
                } else if (error instanceof NetworkError) {
                    System.out.println("Network Error");
                    //TODO
                } else if (error instanceof ParseError) {
                    System.out.println("Parse Error");
                    //TODO
                } else {
                    System.out.println("unknown error");
                }
                Toast.makeText(activity, "Error " + error, Toast.LENGTH_SHORT).show();
//                Toast.makeText(activity, "Something went wrong try again !", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", session.getString(Session.MOBILE));
                params.put("password", session.getString(Session.PASSWORD));
                params.put("tok", session.getString(Session.TOKEN));
                return params;
            }


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();///IMAGE NAME SETTING DURING
//                params.put("img1", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                params.put("profile", new DataPart(imagename + ".jpeg", getFileDataFromDrawable(activity, binding.kycUserImage.getDrawable()), "image/jpeg"));
                params.put("pan_card", new DataPart(imagename + ".jpeg", getFileDataFromDrawable(activity, binding.kycPanImg.getDrawable()), "image/jpeg"));
                params.put("front_aadhar", new DataPart(imagename + ".jpeg", getFileDataFromDrawable(activity, binding.kycAadhaarImg.getDrawable()), "image/jpeg"));
                params.put("back_aadhar", new DataPart(imagename + ".jpeg", getFileDataFromDrawable(activity, binding.kycAadhaarSecondImg.getDrawable()), "image/jpeg"));

//                params.put("back_aadhar", new DataPart(imagename + ".jpeg", getFileDataFromDrawable(getBaseContext(), binding.kycAadhaarSecondImg.getDrawable())));
                return params;

            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
//                Constant.VOLLEY_TIMEOUT,
                VOLLEY_TIMEOUT_KYC,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(volleyMultipartRequest);
    }


    public static byte[] getFileDataFromDrawable(Context context, int id) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    public static byte[] getFileDataFromDrawable(Context context, Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    private void onclick() {

        binding.kycUploadPic.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK,  MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1001);

//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_PICK);
//            startActivityForResult(Intent.createChooser(intent, "Select Image"), 1001);

        });

        // new
        binding.kycUploadAadhaar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK,  MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1002);
        });


        binding.kycUploadAadhaarSecond.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK,  MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1003);
        });

        binding.kycUploadPan.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK,  MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1004);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001) {
            if (resultCode == RESULT_OK && data != null) {
                try {
                    Uri selectedImage = data.getData();
//                    InputStream imageStream = getContentResolver().openInputStream(selectedImage);
//                    image = BitmapFactory.decodeStream(imageStream);

                    Cursor returnCursor = getContentResolver().query(selectedImage, null, null, null, null);
                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                    returnCursor.moveToFirst();
                    Log.e("TAG", "Name:" + returnCursor.getString(nameIndex));
                    Log.e("TAG","Size: "+Long.toString(returnCursor.getLong(sizeIndex)));
                    System.out.println("Name" + returnCursor.getString(nameIndex));
                    System.out.println("Size" + Long.toString(returnCursor.getLong(sizeIndex)));
                    InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                    image = BitmapFactory.decodeStream(imageStream);

//                    if (returnCursor.getLong(sizeIndex) >= 500000)
                    if (returnCursor.getLong(sizeIndex) >= 250000) {
                        Toast.makeText(activity, "Select image below 250kb", Toast.LENGTH_LONG).show();
                        System.out.println("select 250kb");
                    } else {
//                        Toast.makeText(activity, "Done", Toast.LENGTH_SHORT).show();
                        Glide.with(binding.kycUserImage).clear(binding.kycUserImage);
                        uploadUserProfile();
                        binding.kycUserImage.setImageBitmap(image);
                        System.out.println(image);
//                        binding.kycUserImgSuccess.setText("profile_picture.png");
                        binding.kycUserImgSuccess.setText("Profile Picture Upload Successfully");
                        binding.kycUserImgSuccess.setTextColor(ContextCompat.getColor(activity, R.color.green));
                        System.out.println("Done");
                    }

                    // clear image
//                    Glide.with(binding.kycUserImage).clear(binding.kycUserImage);



//                    binding.kycUserImage.setImageBitmap(image);
//                    System.out.println(image);
//                    binding.kycUserImgSuccess.setText("profile_picture.png");
//                    binding.kycUserImgSuccess.setTextColor(ContextCompat.getColor(activity, R.color.green));

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }

        if (requestCode == 1002) {
            if (resultCode == RESULT_OK && data != null) {

                try {
                    Uri selectedImage = data.getData();
//                    InputStream imageStream = getContentResolver().openInputStream(selectedImage);
//                    image = BitmapFactory.decodeStream(imageStream);

                    Cursor returnCursor = getContentResolver().query(selectedImage, null, null, null, null);
                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                    returnCursor.moveToFirst();
                    Log.e("TAG", "Name:" + returnCursor.getString(nameIndex));
                    Log.e("TAG","Size: "+Long.toString(returnCursor.getLong(sizeIndex)));
                    System.out.println("Name" + returnCursor.getString(nameIndex));
                    System.out.println("Size" + Long.toString(returnCursor.getLong(sizeIndex)));
                    InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                    image = BitmapFactory.decodeStream(imageStream);

                    if (returnCursor.getLong(sizeIndex) >= 250000) {
                        Toast.makeText(activity, "Select image below 250kb", Toast.LENGTH_LONG).show();
                        System.out.println("select 250kb");
                    } else {
                        Glide.with(binding.kycAadhaarImg).clear(binding.kycAadhaarImg);
                        uploadAadharFront();
                        binding.kycAadhaarImg.setImageBitmap(image);
                        System.out.println(image);
//                        binding.kycAadhaarSuccess.setText("first_Aadhar.png");
                        binding.kycAadhaarSuccess.setText("Aadhar Front Upload Successfully");
                        binding.kycAadhaarSuccess.setTextColor(ContextCompat.getColor(activity, R.color.green));
                    }

                    // clear image
//                    Glide.with(binding.kycAadhaarImg).clear(binding.kycAadhaarImg);
//
//                    binding.kycAadhaarImg.setImageBitmap(image);
//                    System.out.println(image);
//                    binding.kycAadhaarSuccess.setText("first_Aadhar.png");
//                    binding.kycAadhaarSuccess.setTextColor(ContextCompat.getColor(activity, R.color.green));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }


        if (requestCode == 1003) {
            if (resultCode == RESULT_OK && data != null) {

                try {
                    Uri selectedImage = data.getData();
//                    InputStream imageStream = getContentResolver().openInputStream(selectedImage);
//                    image = BitmapFactory.decodeStream(imageStream);

                    Cursor returnCursor = getContentResolver().query(selectedImage, null, null, null, null);
                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                    returnCursor.moveToFirst();
                    Log.e("TAG", "Name:" + returnCursor.getString(nameIndex));
                    Log.e("TAG","Size: "+Long.toString(returnCursor.getLong(sizeIndex)));
                    System.out.println("Name" + returnCursor.getString(nameIndex));
                    System.out.println("Size" + Long.toString(returnCursor.getLong(sizeIndex)));
                    InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                    image = BitmapFactory.decodeStream(imageStream);

                    if (returnCursor.getLong(sizeIndex) >= 250000) {
                        Toast.makeText(activity, "Select image below 250kb", Toast.LENGTH_LONG).show();
                        System.out.println("select 250kb");
                    } else {
                        Glide.with(binding.kycAadhaarSecondImg).clear(binding.kycAadhaarSecondImg);
                        uploadAadharBack();
                        binding.kycAadhaarSecondImg.setImageBitmap(image);
                        System.out.println(image);
//                        binding.kycAadhaarSecondSuccess.setText("second_Aadhar.png");
                        binding.kycAadhaarSecondSuccess.setText("Aadhar Back Upload Successfully");
                        binding.kycAadhaarSecondSuccess.setTextColor(ContextCompat.getColor(activity, R.color.green));
                    }

//                    Glide.with(binding.kycAadhaarSecondImg).clear(binding.kycAadhaarSecondImg);
//                    binding.kycAadhaarSecondImg.setImageBitmap(image);
//                    System.out.println(image);
//                    binding.kycAadhaarSecondSuccess.setText("second_Aadhar.png");
//                    binding.kycAadhaarSecondSuccess.setTextColor(ContextCompat.getColor(activity, R.color.green));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }

        if (requestCode == 1004) {
            if (resultCode == RESULT_OK && data != null) {

                try {
                    Uri selectedImage = data.getData();
//                    InputStream imageStream = getContentResolver().openInputStream(selectedImage);
//                    image = BitmapFactory.decodeStream(imageStream);

                    Cursor returnCursor = getContentResolver().query(selectedImage, null, null, null, null);
                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                    returnCursor.moveToFirst();
                    Log.e("TAG", "Name:" + returnCursor.getString(nameIndex));
                    Log.e("TAG","Size: "+Long.toString(returnCursor.getLong(sizeIndex)));
                    System.out.println("Name" + returnCursor.getString(nameIndex));
                    System.out.println("Size" + Long.toString(returnCursor.getLong(sizeIndex)));
                    InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                    image = BitmapFactory.decodeStream(imageStream);

                    if (returnCursor.getLong(sizeIndex) >= 250000) {
                        Toast.makeText(activity, "Select image below 250kb", Toast.LENGTH_LONG).show();
                        System.out.println("select 250kb");
                    } else {
                        Glide.with(binding.kycPanImg).clear(binding.kycPanImg);
                        uploadPanCard();
                        binding.kycPanImg.setImageBitmap(image);
                        System.out.println(image);
                        binding.kycPanSuccess.setText("Pan Card Upload Successfully");
                        binding.kycPanSuccess.setTextColor(ContextCompat.getColor(activity, R.color.green));
                    }

//                    Glide.with(binding.kycPanImg).clear(binding.kycPanImg);
//                    binding.kycPanImg.setImageBitmap(image);
//                    System.out.println(image);
//                    binding.kycPanSuccess.setText("pan_card.png");
//                    binding.kycPanSuccess.setTextColor(ContextCompat.getColor(activity, R.color.green));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }

    }

    private void uploadPanCard() {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/UserKycUploadPan";
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideLoadingDialog();
                        System.out.println(response);

                        try {
                            JSONObject object = new JSONObject(new String(response.data));

                            String status = "";
                            if (object.has("Status")){
                                status = object.getString("Status");
                            }

                            if (status.equalsIgnoreCase("200")) {
                                Toast.makeText(activity, "Pan Card Upload Successfully !", Toast.LENGTH_SHORT).show();
                                System.out.println("Pan Card Upload Successfully !");
                            } else {
                                System.out.println("Something went wrong !!!!!!!!!!!!");
                                Toast.makeText(activity, "Something went wrong !", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoadingDialog();
                System.out.println(error);
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                    Toast.makeText(context, context.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show();
                    System.out.println("Time Out Error");
                } else if (error instanceof AuthFailureError) {
                    System.out.println("AuthFail Error");
                    //TODO
                } else if (error instanceof ServerError) {
                    System.out.println("Server Error");
                    //TODO
                } else if (error instanceof NetworkError) {
                    System.out.println("Network Error");
                    //TODO
                } else if (error instanceof ParseError) {
                    System.out.println("Parse Error");
                    //TODO
                } else {
                    System.out.println("unknown error");
                }
                Toast.makeText(activity, "Error " + error, Toast.LENGTH_SHORT).show();
//                Toast.makeText(activity, "Something went wrong try again !", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", session.getString(Session.MOBILE));
                params.put("password", session.getString(Session.PASSWORD));
                params.put("tok", session.getString(Session.TOKEN));
                return params;
            }


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();///IMAGE NAME SETTING DURING
                params.put("pan_card", new DataPart(imagename + ".jpeg", getFileDataFromDrawable(activity, binding.kycPanImg.getDrawable()), "image/jpeg"));
                return params;

            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
//                Constant.VOLLEY_TIMEOUT,
                VOLLEY_TIMEOUT_KYC,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(volleyMultipartRequest);
    }

    private void uploadAadharBack() {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/UserKycUploadAadrBk";
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideLoadingDialog();
                        System.out.println(response);

                        try {
                            JSONObject object = new JSONObject(new String(response.data));

                            String status = "";
                            if (object.has("Status")){
                                status = object.getString("Status");
                            }

                            if (status.equalsIgnoreCase("200")) {
                                Toast.makeText(activity, "Aadhar Back Upload Successfully !", Toast.LENGTH_SHORT).show();
                                System.out.println("Aadhar Back Upload Successfully !");
                            } else {
                                System.out.println("Something went wrong !!!!!!!!!!!!");
                                Toast.makeText(activity, "Something went wrong !", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoadingDialog();
                System.out.println(error);
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                    Toast.makeText(context, context.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show();
                    System.out.println("Time Out Error");
                } else if (error instanceof AuthFailureError) {
                    System.out.println("AuthFail Error");
                    //TODO
                } else if (error instanceof ServerError) {
                    System.out.println("Server Error");
                    //TODO
                } else if (error instanceof NetworkError) {
                    System.out.println("Network Error");
                    //TODO
                } else if (error instanceof ParseError) {
                    System.out.println("Parse Error");
                    //TODO
                } else {
                    System.out.println("unknown error");
                }
                Toast.makeText(activity, "Error " + error, Toast.LENGTH_SHORT).show();
//                Toast.makeText(activity, "Something went wrong try again !", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", session.getString(Session.MOBILE));
                params.put("password", session.getString(Session.PASSWORD));
                params.put("tok", session.getString(Session.TOKEN));
                return params;
            }


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();///IMAGE NAME SETTING DURING
                params.put("back_aadhar", new DataPart(imagename + ".jpeg", getFileDataFromDrawable(activity, binding.kycAadhaarSecondImg.getDrawable()), "image/jpeg"));
                return params;

            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
//                Constant.VOLLEY_TIMEOUT,
                VOLLEY_TIMEOUT_KYC,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(volleyMultipartRequest);
    }

    private void uploadAadharFront() {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/UserKycUploadAadrFnt";
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideLoadingDialog();
                        System.out.println(response);

                        try {
                            JSONObject object = new JSONObject(new String(response.data));

                            String status = "";
                            if (object.has("Status")){
                                status = object.getString("Status");
                            }

                            if (status.equalsIgnoreCase("200")) {
                                Toast.makeText(activity, "Front Aadhar Upload Successfully !", Toast.LENGTH_SHORT).show();
                                System.out.println("Front Aadhar Upload Successfully !");
                            } else {
                                System.out.println("Something went wrong !!!!!!!!!!!!");
                                Toast.makeText(activity, "Something went wrong !", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoadingDialog();
                System.out.println(error);
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                    Toast.makeText(context, context.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show();
                    System.out.println("Time Out Error");
                } else if (error instanceof AuthFailureError) {
                    System.out.println("AuthFail Error");
                    //TODO
                } else if (error instanceof ServerError) {
                    System.out.println("Server Error");
                    //TODO
                } else if (error instanceof NetworkError) {
                    System.out.println("Network Error");
                    //TODO
                } else if (error instanceof ParseError) {
                    System.out.println("Parse Error");
                    //TODO
                } else {
                    System.out.println("unknown error");
                }
                Toast.makeText(activity, "Error " + error, Toast.LENGTH_SHORT).show();
//                Toast.makeText(activity, "Something went wrong try again !", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", session.getString(Session.MOBILE));
                params.put("password", session.getString(Session.PASSWORD));
                params.put("tok", session.getString(Session.TOKEN));
                return params;
            }


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();///IMAGE NAME SETTING DURING
                params.put("front_aadhar", new DataPart(imagename + ".jpeg", getFileDataFromDrawable(activity, binding.kycAadhaarImg.getDrawable()), "image/jpeg"));
                return params;

            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
//                Constant.VOLLEY_TIMEOUT,
                VOLLEY_TIMEOUT_KYC,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(volleyMultipartRequest);
    }

    private void uploadUserProfile() {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/UserKycUploadProfile";
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideLoadingDialog();
                        System.out.println(response);

                        try {
                            JSONObject object = new JSONObject(new String(response.data));

                            String status = "";
                            if (object.has("Status")){
                                status = object.getString("Status");
                            }

                            if (status.equalsIgnoreCase("200")) {
                                Toast.makeText(activity, "Profile Upload Successfully !", Toast.LENGTH_SHORT).show();
                                System.out.println("Profile Upload Successfully !");
                            } else {
                                System.out.println("Something went wrong !!!!!!!!!!!!");
                                Toast.makeText(activity, "Something went wrong !", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoadingDialog();
                System.out.println(error);
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                    Toast.makeText(context, context.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show();
                    System.out.println("Time Out Error");
                } else if (error instanceof AuthFailureError) {
                    System.out.println("AuthFail Error");
                    //TODO
                } else if (error instanceof ServerError) {
                    System.out.println("Server Error");
                    //TODO
                } else if (error instanceof NetworkError) {
                    System.out.println("Network Error");
                    //TODO
                } else if (error instanceof ParseError) {
                    System.out.println("Parse Error");
                    //TODO
                } else {
                    System.out.println("unknown error");
                }
                Toast.makeText(activity, "Error " + error, Toast.LENGTH_SHORT).show();
//                Toast.makeText(activity, "Something went wrong try again !", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", session.getString(Session.MOBILE));
                params.put("password", session.getString(Session.PASSWORD));
                params.put("tok", session.getString(Session.TOKEN));
                return params;
            }


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();///IMAGE NAME SETTING DURING
                params.put("profile", new DataPart(imagename + ".jpeg", getFileDataFromDrawable(activity, binding.kycUserImage.getDrawable()), "image/jpeg"));

                return params;

            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
//                Constant.VOLLEY_TIMEOUT,
                VOLLEY_TIMEOUT_KYC,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(volleyMultipartRequest);
    }

    private void encodeBitmapImage(Bitmap image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] bytesOfImages = byteArrayOutputStream.toByteArray();
        encodedImageString = Base64.encodeToString(bytesOfImages, Base64.DEFAULT);

    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

//    public Bitmap get_Resized_Bitmap(Bitmap bmp, int newHeight, int newWidth) {
//        int width = bmp.getWidth();
//        int height = bmp.getHeight();
//        float scaleWidth = ((float) newWidth) / width;
//        float scaleHeight = ((float) newHeight) / height;
//        // CREATE A MATRIX FOR THE MANIPULATION
//        Matrix matrix = new Matrix();
//        // RESIZE THE BIT MAP
//        matrix.postScale(scaleWidth, scaleHeight);
//
//        // "RECREATE" THE NEW BITMAP
//        Bitmap newBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, false);
//        return newBitmap ;
//    }




    // new Upload with okhttp

//    private void SelectImage() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), 101);
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 101
//                && resultCode == RESULT_OK
//                && data != null
//                && data.getData() != null) {
//            try {
//                selectedImage = data.getData();
//                path = getImageFilePath(selectedImage);
//                file = new File(path);
//                content_type = getMimeType(file.getAbsolutePath());
//                //  Log.d("content_type", content_type);
//                Picasso.with(activity)
//                        .load(selectedImage)
//                        .error(R.drawable.placeholder)
//                        .placeholder(R.drawable.placeholder)
//                        .into(iv_profile_image_profile_personal);
//                uploadImageToServer();
//            } catch (Exception e) {
//                e.printStackTrace();
//                Toast.makeText(context, "Something Went Wrong..", Toast.LENGTH_SHORT).show();
//            }
//
//        }
//    }
//
//    @SuppressLint("Range")
//    public String getImageFilePath(Uri uri) {
//        String imagePath = "";
//        File file = new File(uri.getPath());
//        String[] filePath = file.getPath().split(":");
//        String image_id = filePath[filePath.length - 1];
//
//        Cursor cursor = activity.getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{image_id}, null);
//        if (cursor != null) {
//            if (cursor.moveToFirst()) {
//                cursor.moveToFirst();
//                imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//            }
//            cursor.close();
//        }
//        return imagePath;
//    }
//
//    private String getMimeType(String path) {
//        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
//        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//    }
//
//    private void uploadImageToServer() {
//        showLoadingDialog();
//        String image = "";
//        if (selectedImage != null) {
//            InputStream imageStream = null;
//            try {
//                imageStream = context.getContentResolver().openInputStream(
//                        selectedImage);
//
//                bitmap = BitmapFactory.decodeStream(imageStream);
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
//                byte[] byteArray = stream.toByteArray();
//                try {
//                    stream.close();
//                    stream = null;
//                    image = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                } catch (IOException e) {
//
//                    e.printStackTrace();
//                }
//
//                if (image != null) {
//                    try {
//                        Thread thread = new Thread() {
//                            @Override
//                            public void run() {
//                                OkHttpClient client = new OkHttpClient();
//                                RequestBody request = RequestBody.create(MediaType.parse(content_type), file);
//                                RequestBody requestBody = new MultipartBody.Builder()
//                                        .setType(MultipartBody.FORM)
//                                        .addFormDataPart("profile_pic", path.substring(path.lastIndexOf("/") + 1), request)
//                                        .addFormDataPart("user_id", sp.getString("user_id", ""))
//                                        .build();
//
//                                Request finalRequest = new Request.Builder()
//                                        .url(update_profile_image)
//                                        .post(requestBody)
//                                        .build();
//                                final Response response;
//                                try {
//                                    response = client.newCall(finalRequest).execute();
//
//                                    String responseMsg = response.body().string();
//
//                                    //    Log.e("TAG", "run: " + responseMsg);
//                                    final JSONObject jsonObject = new JSONObject(responseMsg);
//                                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {
//                                        if (activity==null){
//                                            return;
//                                        }
//                                        activity.runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                try {
//                                                    hideLoadingDialog();
//                                                    edt.putString("image", jsonObject.getString("imagename"));
//                                                    edt.commit();
//                                                    Toast.makeText(context, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
//                                                } catch (JSONException e) {
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                        });
//                                    } else {
//                                        if (activity==null){
//                                            return;
//                                        }
//                                        activity.runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                hideLoadingDialog();
//                                                Toast.makeText(context, "Something Went Wrong...", Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                                    }
//                                } catch (IOException | JSONException e) {
//                                    e.printStackTrace();
//                                    if (activity==null){
//                                        return;
//                                    }
//                                    activity.runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            hideLoadingDialog();
//                                            Toast.makeText(context, "Something Went Wrong Try Again...", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//                            }
//                        };
//                        thread.start();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        //  Log.e("jsondata", "onClick: " + e.toString());
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }




    private void showLoadingDialog() {
        if (mDialog == null) {
            mDialog = new SimpleArcDialog(activity);
            configuration = new ArcConfiguration(activity);
            mDialog.setConfiguration(configuration);
            configuration.setText("Please Wait.....");
            configuration.setLoaderStyle(SimpleArcLoader.STYLE.COMPLETE_ARC);
            configuration.setColors(new int[]{Color.BLUE, Color.RED, Color.WHITE, Color.MAGENTA});
            configuration.setTextColor(Color.BLUE);
            mDialog.setCanceledOnTouchOutside(false);
        }
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    private void hideLoadingDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }




}