package com.swinfotech.swpay.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.databinding.ActivityDemoBinding;
import com.swinfotech.swpay.databinding.ActivityRechargeConfirmationBinding;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class RechargeConfirmation extends AppCompatActivity {

    ActivityRechargeConfirmationBinding binding;
    RechargeConfirmation activity;

    TextView getMob, getOp, getAmt, dAndT;
    String mob;
    String op;
    String amt, keyOperator_id, keyTPin;
    Session session;
    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    Session.OnClearTextClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRechargeConfirmationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        session = new Session(this);

        activity = this;

        binding.mobileBack.setOnClickListener(v -> {
            onBackPressed();
        });

        getMob = (TextView) findViewById(R.id.mobile_get);
        getOp = (TextView) findViewById(R.id.operator_get);
        getAmt = (TextView) findViewById(R.id.amount_get);
        dAndT = (TextView) findViewById(R.id.date_and_time);

        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        dAndT.setText(currentDateTimeString);


        mob = getIntent().getStringExtra("keyMobile");
//        op = getIntent().getStringExtra("keyOperator");
        op = getIntent().getStringExtra("operator");
        amt = getIntent().getStringExtra("keyAmount");
        keyOperator_id = getIntent().getStringExtra("keyOperator_id");
        keyTPin = getIntent().getStringExtra("keyTPin");

        System.out.println(keyOperator_id);

        getMob.setText(mob);
//        getOp.setText(op);
        getOp.setText(op);
        getAmt.setText(amt);


        binding.rechargeWalletBal.setText(getAmountFormat(session.getString(Session.BALANCE_AMOUNT)));


        // API Calling For recharge


        binding.submit.setOnClickListener(view -> {
            mob = String.valueOf(binding.mobileGet.getText());
            op = String.valueOf(binding.operatorGet.getText());
            amt = String.valueOf(binding.amountGet.getText());

            if (mob.isEmpty()) {
                binding.mobileGet.setError("Enter Mobile");
                Toast.makeText(RechargeConfirmation.this, binding.mobileGet.getError(), Toast.LENGTH_SHORT).show();
                return;
            }
            if (op.isEmpty()) {
                binding.operatorGet.setError("Enter Operator");
                Toast.makeText(RechargeConfirmation.this, binding.operatorGet.getError(), Toast.LENGTH_SHORT).show();
                return;
            }
            if (amt.isEmpty()) {
                binding.amountGet.setError("Enter Amount");
                Toast.makeText(RechargeConfirmation.this, binding.amountGet.getError(), Toast.LENGTH_SHORT).show();
                return;
            }

            binding.submit.setEnabled(false);
            Log.e("TAG", "onClick: " + keyOperator_id);
            Log.e("TAG", "onCreate: hitting api");
            appemptRecharge();
        });

    }

    private void appemptRecharge() {
//        showProgressDialog();
        showLoadingDialog();
        String url = Constant.RECHARGE_API + "?username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&mobile=" + mob
                + "&amount=" + amt
                + "&tpin=" + keyTPin
                + "&op=" + keyOperator_id
                + "&tok=" + session.getString(Session.TOKEN);
        System.out.println(url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.RECHARGE_API + "?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD) + "&mobile=" + mob + "&amount=" + amt + "&tpin=" + keyTPin + "&op=" + keyOperator_id + "&tok=" + session.getString(Session.TOKEN),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        hideProgressDialog();
                        hideLoadingDialog();


                        Log.e("strrrrr", ">>" + response);
                        binding.submit.setEnabled(true);
                        binding.errorLyt.setVisibility(View.GONE);
                        try {
                            JSONObject obj = new JSONObject(response);

                            if (obj.has("apistatus")) {
                                String apistatus = "FAILED";
                                apistatus = obj.getString("apistatus");
                                String remark = "";
                                if (obj.has("remarks")) {
                                    remark = obj.getString("remarks");
                                }
                                if (apistatus.equalsIgnoreCase("SUCCESS")) {
                                    showErrorDialog(apistatus, remark);
                                } else {
                                    showErrorDialog(apistatus, remark);
                                }

                            } else if (obj.has("Resp")) {
                                if (obj.has("Resp")) {
//                                resp = obj.getString("Resp");
                                    binding.errorLyt.setVisibility(View.VISIBLE);
                                    binding.errorLyt.setText(obj.getString("Resp"));
                                } else {
                                    System.out.println("Please Try Again");
                                }
                            }
                            else {
                                JSONArray jsonArray = obj.getJSONArray("repeat");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    if (object.has("apistatus")) {
                                        String apistatus = "FAILED";
                                        apistatus = object.getString("apistatus");
                                        String remark = "";
                                        if (object.has("remarks")) {
                                            remark = object.getString("remarks");
                                        }
                                        if (apistatus.equalsIgnoreCase("SUCCESS")) {
                                            showErrorDialog(apistatus, remark);
                                        } else {
                                            showErrorDialog(apistatus, remark);
                                        }
                                    } else {
                                        Toast.makeText(RechargeConfirmation.this, "Unknown Error...", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        binding.submit.setEnabled(true);
//                        hideProgressDialog();
                        hideLoadingDialog();
                        //displaying the error in toast if occurs
                        try {
                            Toast.makeText(RechargeConfirmation.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(RechargeConfirmation.this, "Unable to fetch error from api", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(RechargeConfirmation.this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }


    void showErrorDialog(String status, String remark) {
        final Dialog dia = new Dialog(RechargeConfirmation.this);
        //We have added a title in the custom layout. So let's disable the default title.
        dia.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dia.setCancelable(true);
        //Mention the name of the layout of your custom dialog.
        ActivityDemoBinding binding = ActivityDemoBinding.inflate(getLayoutInflater());
        dia.setContentView(binding.getRoot());


        binding.mobileNumber.setText(mob);
        binding.oprator.setText(op);
        binding.rupees.setText(amt);

        if (status.equalsIgnoreCase("FAILED")) {
            binding.gifFailed.setVisibility(View.VISIBLE);
            binding.gif.setVisibility(View.GONE);
            binding.rechargeStatus.setText("Failed");
            binding.statusImage.setImageDrawable(ContextCompat.getDrawable(RechargeConfirmation.this, R.drawable.failed));
            binding.rechargeStatus.setTextColor(ContextCompat.getColor(RechargeConfirmation.this, R.color.red));
            binding.transcationId.setText(remark);
            binding.gif.setImageDrawable(ContextCompat.getDrawable(RechargeConfirmation.this, R.drawable.failure));
        } else if (status.equalsIgnoreCase("PENDING")) {
            binding.statusImage.setImageDrawable(ContextCompat.getDrawable(RechargeConfirmation.this, R.drawable.ic_autopay_grey));
//            binding.rechargeStatus.setText("Recharge Pending");
            binding.rechargeStatus.setText(status);
            binding.rechargeStatus.setTextColor(ContextCompat.getColor(RechargeConfirmation.this, R.color.blue));
            binding.gifFailed.setVisibility(View.GONE);
            binding.gif.setVisibility(View.VISIBLE);
            binding.transcationId.setVisibility(View.GONE);
            binding.transcationId.setText(remark);
        } else {
            binding.statusImage.setImageDrawable(ContextCompat.getDrawable(RechargeConfirmation.this, R.drawable.success));
            binding.rechargeStatus.setText(status);
            binding.rechargeStatus.setTextColor(ContextCompat.getColor(RechargeConfirmation.this, R.color.green));
            binding.gifFailed.setVisibility(View.GONE);
            binding.gif.setVisibility(View.VISIBLE);
            binding.transcationId.setText(remark);
        }
        binding.date.setText(Constant.getDate());
        binding.gotId.setOnClickListener(v -> {
            dia.dismiss();
            setResult();
        });

        binding.shareMobile.setOnClickListener(v -> {
            if (CheckPermission()) {
                shareImage(getMainFrameBitmap(binding.rootViewMobile));
            }
        });


        dia.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            SaveImage();
            Button share = (Button) findViewById(R.id.shareMobile);
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


    private void setResult() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private String getAmountFormat(String data) {
        BigDecimal bd = new BigDecimal(data).setScale(2, RoundingMode.HALF_UP);
//        double newInput = bd.doubleValue();
        String newInput = String.valueOf(bd.doubleValue());
        return newInput;
    }

    // Here The End of API And Database

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