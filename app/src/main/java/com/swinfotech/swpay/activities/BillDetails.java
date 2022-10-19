package com.swinfotech.swpay.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.chaos.view.PinView;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.databinding.ActivityBbpsBillConfirmBinding;
import com.swinfotech.swpay.databinding.ActivityBbpspaymentDoneBinding;
import com.swinfotech.swpay.databinding.ActivityBillDetailsBinding;
import com.swinfotech.swpay.model.OPListElectricityGetSet;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONException;
import org.json.JSONObject;

public class BillDetails extends AppCompatActivity {

    ActivityBillDetailsBinding binding;
    BillDetails activity;

    Session session;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    OPListElectricityGetSet data;

    // op_id From electricity get
    String op_id;
    String tpin = "";
    Boolean isLengthChanged = false;
    int oldLength = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        Objects.requireNonNull(getSupportActionBar()).hide();
        binding = ActivityBillDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;
        session = new Session(activity);

        // for op_id
        data = (OPListElectricityGetSet) getIntent().getSerializableExtra("data");
        op_id = data.getId();

        binding.goToBack1.setOnClickListener(v -> {
            onBackPressed();
        });

        getDataFromIntent();

        onClick();

    }

    private void onClick() {
        binding.billSubmit.setOnClickListener(v -> {
            if (getStringFromEditText(binding.billMobNum) == null) {
                binding.billMobNum.setError("Please Enter Mobile No");
                binding.billMobNum.requestFocus();
            } else if (getStringFromEditText(binding.billMobNum).length() != 10) {
                binding.billMobNum.setError("Please Enter 10 Digit Mobile Number");
                binding.billMobNum.requestFocus();
            } else if (getStringFromEditText(binding.billAccountId) == null) {
                binding.billAccountId.setError("Please Enter Account Id");
                binding.billAccountId.requestFocus();
            } else {

                BBPSFetchBillAPI();

            }

        });
    }

    private void BBPSFetchBillAPI() {
        showLoadingDialog();
        String url = "username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&op_id=" + op_id
                + "&mobile=" + getStringFromEditText(binding.billMobNum)
                + "&account=" + getStringFromEditText(binding.billAccountId)
                + "&recharge_mobile" + getStringFromEditText(binding.billMobNum);
        // Recharge mobile optional now we can access api without recharge mobile
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.BBPS_FETCH_BILL + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
                        binding.billSubmit.setEnabled(true);
                        Log.e("TAG", "onResponse: " + response);
                        try {
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject object = jsonObject.getJSONObject("Data");

                                Log.e("TAG", "onResponse: josn Obj " + jsonObject);
                                Log.e("TAG", "onResponse:  Obj " + object);

                                if (jsonObject.has("Status")) {
                                    String status = "";
                                    //status = obj.getString("Status");
                                    //JSONObject object = jsonObject.getJSONObject("Data");

                                    String dataStatus = "";
                                    if (object.has("Status")) {
                                        dataStatus = object.getString("Status");
                                    }

                                    String name = "";
                                    if (object.has("CUSTNAME")) {
                                        name = object.getString("CUSTNAME");
                                    }

                                    String accountNo = "";
                                    if (jsonObject.has("Account")) {
                                        accountNo = jsonObject.getString("Account");
                                    }

                                    String billAmount = "";
                                    if (object.has("BILLAMT")) {
                                        billAmount = object.getString("BILLAMT");
                                    }

                                    String billDate = "";
                                    if (jsonObject.has("BillDate")) {
                                        billDate = jsonObject.getString("BillDate");
                                    }

                                    String billNum = "";
                                    if (object.has("BILLNO")) {
                                        billNum = object.getString("BILLNO");
                                    }

                                    String dueDate = "";
                                    if (object.has("BILLDUEDATE")) {
                                        dueDate = object.getString("BILLDUEDATE");
                                    }

                                    String agentId = "";
                                    if (jsonObject.has("Account")) {
                                        agentId = jsonObject.getString("Account");
                                    }

                                    String fetchBillId = "";
                                    if (object.has("fetchBillID")) {
                                        fetchBillId = object.getString("fetchBillID");
                                    }

                                    String message = "";
                                    if (object.has("msg")) {
                                        message = object.getString("msg");
                                    }

                                    // here we go
                                    if (status.equalsIgnoreCase("SUCCESS")) {
                                        showBillFetchAPIResult(dataStatus, name, accountNo, billAmount, billDate, billNum, dueDate, message, agentId, fetchBillId);
                                    } else {
                                        showBillFetchAPIResult(dataStatus, name, accountNo, billAmount, billDate, billNum, dueDate, message, agentId, fetchBillId);
                                    }

                                } else {
                                    System.out.println("Data Not Going in Another Activity");
                                }

                            } else {
                                System.out.println("Response is Null");
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
            }
        });
        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void showBillFetchAPIResult(String dataStatus, String name, String accountNo, String billAmount, String billDate, String billNum, String dueDate, String message, String agentId, String fetchBillId) {
        ActivityBbpsBillConfirmBinding bind = ActivityBbpsBillConfirmBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        bind.baToMain.setOnClickListener(v -> {
            onBackPressed();
        });


        if (dataStatus.equalsIgnoreCase("3")) {
            bind.correctBill.setVisibility(View.GONE);
            bind.wrongBill.setVisibility(View.VISIBLE);
            bind.errorAccountID.setText(accountNo);
            bind.billErrorMessage.setText(message);
        } else {
            if (Integer.parseInt(billAmount) == 0) {
                bind.llTpin.setVisibility(View.GONE);
                bind.tvNoBillsPending.setVisibility(View.VISIBLE);
            } else {
                bind.llTpin.setVisibility(View.VISIBLE);
                bind.tvNoBillsPending.setVisibility(View.GONE);
            }
            bind.billCustomerName.setText(name);
            bind.billID.setText(accountNo);
            bind.billAmount.setText(billAmount);
            bind.billDate.setText(billDate);
            bind.billNum.setText(billNum);
            bind.billDueDate.setText(dueDate);

            bind.billDone.setOnClickListener(v -> {
                if (bind.billTPin.getOTP().equalsIgnoreCase("")) {
                    bind.errorTPin.setError("Please Enter TPin");
                    bind.errorTPin.setVisibility(View.VISIBLE);
                    bind.billTPin.requestFocus();
                } else if (bind.billTPin.getOTP().length() < 4) {
                    bind.errorTPin.setError("Please Enter 4 Digit TPin");
                    bind.billTPin.requestFocus();
                    bind.errorTPin.setVisibility(View.VISIBLE);
                } else {
                    bind.errorTPin.setVisibility(View.GONE);
                    billPaymentAPI(bind.billTPin.getOTP(), name, accountNo, billAmount, billDate, dueDate, billNum, agentId, fetchBillId, bind.errorTPin);
                }
            });

        }

    }


    private void billPaymentAPI(String TPIN, String name, String accountNo, String billAmount, String billDate, String dueDate, String billNum, String agentId, String fetchBillId, TextView errorTPin) {
        showLoadingDialog();
        String TPin = String.valueOf(TPIN);
        String url = "username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&mobile_tpin=" + TPin
                + "&recharge_amount=" + billAmount
                + "&op_code=" + op_id
                + "&account=" + getStringFromEditText(binding.billAccountId)
                + "&mobile_number=" + getStringFromEditText(binding.billMobNum)
                + "&agentid=" + agentId
                + "&billnumber=" + billNum
                + "&billdate=" + billDate
                + "&duedate=" + dueDate
                + "&customername=" + name
                + "&apibillid=" + fetchBillId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.BBPS_Bill_Payment + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
//                Toast.makeText(activity, "Data Coming From API", Toast.LENGTH_SHORT).show();

                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.has("apistatus")) {
                                String apistatus = "FAILED";
                                apistatus = obj.getString("apistatus");
                                String remark = "";
                                if (obj.has("remarks")) {
                                    remark = obj.getString("remarks");
                                }
                                String resp = "";
                                if (obj.has("Resp")) {
                                    resp = obj.getString("Resp");
                                }

                                if (apistatus.equalsIgnoreCase("SUCCESS")) {
                                    showErrorDialog(apistatus, remark, resp, name, billAmount);
                                } else {
                                    showErrorDialog(apistatus, remark, resp, name, billAmount);
                                }

                            } else {
                                if (obj.has("Resp")) {
                                    errorTPin.setVisibility(View.VISIBLE);
                                    errorTPin.setText(obj.getString("Resp"));
                                } else {
                                    Toast.makeText(activity, "Unknown Error...", Toast.LENGTH_SHORT).show();
                                    System.out.println("Unknown Error...");
                                }
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
//                Toast.makeText(activity, "Data is Not Coming From API", Toast.LENGTH_SHORT).show();
            }
        });
        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void showErrorDialog(String status, String remark, String resp, String name, String billAmount) {
        final Dialog dia = new Dialog(activity);
        //We have added a title in the custom layout. So let's disable the default title.
        dia.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dia.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dia.setCancelable(false);
        //Mention the name of the layout of your custom dialog.

        ActivityBbpspaymentDoneBinding binding1 = ActivityBbpspaymentDoneBinding.inflate(getLayoutInflater());
        dia.setContentView(binding1.getRoot());

        if (status.equalsIgnoreCase("FAILED")) {
            binding1.gifFailedBBPS.setVisibility(View.VISIBLE);
            binding1.gifSuccessBBPS.setVisibility(View.GONE);
            binding1.doneStatus.setText("Failed");
            binding1.doneResp.setText(resp);
            binding1.doneResp.setTextColor(ContextCompat.getColor(activity, R.color.red));
            binding1.doneStatus.setTextColor(ContextCompat.getColor(activity, R.color.red));
            binding1.doneRemarks.setText(remark);
            binding1.doneName.setText(name);
            binding1.doneId.setText(getStringFromEditText(binding.billAccountId));
            binding1.doneMobNo.setText(getStringFromEditText(binding.billMobNum));
            binding1.doneAmount.setText(billAmount);
        } else if (status.equalsIgnoreCase("PENDING")) {
            binding1.gifFailedBBPS.setVisibility(View.GONE);
            binding1.gifSuccessBBPS.setVisibility(View.VISIBLE);
            binding1.doneStatus.setText("Recharge Pending");
            binding1.doneStatus.setTextColor(ContextCompat.getColor(activity, R.color.blue));
            binding1.doneResp.setText(resp);
            binding1.doneResp.setTextColor(ContextCompat.getColor(activity, R.color.blue));
            binding1.doneRemarks.setText(remark);
            binding1.doneName.setText(name);
            binding1.doneId.setText(getStringFromEditText(binding.billAccountId));
            binding1.doneMobNo.setText(getStringFromEditText(binding.billMobNum));
            binding1.doneAmount.setText(billAmount);
        } else {
            binding1.gifFailedBBPS.setVisibility(View.GONE);
            binding1.gifSuccessBBPS.setVisibility(View.VISIBLE);
            binding1.doneStatus.setText(status);
            binding1.doneStatus.setTextColor(ContextCompat.getColor(activity, R.color.green));
            binding1.doneResp.setTextColor(ContextCompat.getColor(activity, R.color.green));
            binding1.doneResp.setText(resp);
            binding1.doneRemarks.setText(remark);
            binding1.doneName.setText(name);
            binding1.doneId.setText(getStringFromEditText(binding.billAccountId));
            binding1.doneMobNo.setText(getStringFromEditText(binding.billMobNum));
            binding1.doneAmount.setText(billAmount);
        }
        // Date Fix for Every Status
        binding1.doneDate.setText(Constant.getDate());

        binding1.doneBillPayment.setOnClickListener(v -> {
            dia.dismiss();
            onBackPressed();
        });

        binding1.shareBBPS.setOnClickListener(v -> {
            if (CheckPermission()) {
                shareImage(getMainFrameBitmap(binding1.rootViewBBPS));
            }
        });

        dia.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            SaveImage();
            Button share = (Button) findViewById(R.id.shareBBPS);
//            binding.shareMobile.performClick();
            share.performClick();
        } else {
            Toast.makeText(activity, "Permission Denied", Toast.LENGTH_SHORT).show();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }


    private Bitmap getMainFrameBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        return CropBitmapTransparency(bitmap);
    }

    private Bitmap CropBitmapTransparency(Bitmap sourceBitmap) {
        int minX = sourceBitmap.getWidth();
        int minY = sourceBitmap.getHeight();
        int maxX = -1;
        int maxY = -1;
        for (int y = 0; y < sourceBitmap.getHeight(); y++) {
            for (int x = 0; x < sourceBitmap.getWidth(); x++) {
                if (((sourceBitmap.getPixel(x, y) >> 24) & 255) > 0) {
                    if (x < minX) {
                        minX = x;
                    }
                    if (x > maxX) {
                        maxX = x;
                    }
                    if (y < minY) {
                        minY = y;
                    }
                    if (y > maxY) {
                        maxY = y;
                    }
                }
            }
        }
        if (maxX < minX || maxY < minY) {
            return null;
        }
        return Bitmap.createBitmap(sourceBitmap, minX, minY, (maxX - minX) + 1, (maxY - minY) + 1);
    }


    private boolean CheckPermission() {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return false;
        }
        return true;
    }

    private void shareImage(Bitmap bitmap) {
        try {
            String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "title", null);
            Uri bitmapUri = Uri.parse(bitmapPath);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/png");
            intent.putExtra(Intent.EXTRA_TEXT, ""); // add your custom msg here
            intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
            startActivity(Intent.createChooser(intent, "Share"));
        } catch (Exception e) {
            e.printStackTrace();
            // showMessage("Something Went Wrong...");
        }
    }


    private String getStringFromEditText(EditText editText) {
        // check if edittext is null or not
        if (editText == null) {
            // then return null
            return null;
        }
        // check if edittext is empty
        if (editText.getText().toString().trim().equalsIgnoreCase("")) {
            // then also return null for better optimization
            return null;
        }
        // else return the value
        return editText.getText().toString().trim();

    }

    private void getDataFromIntent() {
        if (getIntent() != null) {
            data = (OPListElectricityGetSet) getIntent().getSerializableExtra("data");
            // check if data not null
            if (data != null) {
                String image = data.getImage();

                // check if image is svg or not
                if (image.substring(image.lastIndexOf(".") + 1).equalsIgnoreCase("svg")) {
                    // make uri from url
                    Uri uri = Uri.parse(Constant.COMMISSION_IMAGES_BASE_URL + data.getImage());
                    // load image from uri using GlideToVector
                    GlideToVectorYou
                            .init()
                            .with(activity)
                            .withListener(new GlideToVectorYouListener() {
                                @Override
                                public void onLoadFailed() {
                                }

                                @Override
                                public void onResourceReady() {
                                }
                            })
                            .load(uri, binding.billImage);

                } else {
                    Glide.with(activity)
                            .load(Constant.COMMISSION_IMAGES_BASE_URL + data.getImage())
                            .into(binding.billImage);
                }

                binding.billName.setText(data.getOpName());
            } else {
                Toast.makeText(activity, "Data is null please check your code", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity, "Intent is null please check your code", Toast.LENGTH_SHORT).show();
        }
    }

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