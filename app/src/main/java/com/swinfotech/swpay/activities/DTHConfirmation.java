package com.swinfotech.swpay.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.swinfotech.swpay.databinding.ActivityDthconfirmationBinding;
import com.swinfotech.swpay.databinding.ActivityDthsuccessBinding;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class DTHConfirmation extends AppCompatActivity {

    ActivityDthconfirmationBinding binding;
    DTHConfirmation activity;

    ImageView backBtn;

    TextView getMob, getOp, getAmt, dAndT;
    String mob;
    String op;
    String amt, keyOperator_id, keyTPin;
    Session session;
    String status;
    private ProgressDialog progressDialog;
    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDthconfirmationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        session = new Session(this);

        activity = this;

//        backBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(DTHConfirmation.this, Telecom.class));
//            }
//        });

        binding.dthBack.setOnClickListener(view -> {
            onBackPressed();
        });

        getMob = (TextView) findViewById(R.id.dth_mobile_get);
        getOp = (TextView) findViewById(R.id.dth_operator_get);
        getAmt = (TextView) findViewById(R.id.dth_amount_get);
        dAndT = (TextView) findViewById(R.id.dth_date_and_time);

        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        dAndT.setText(currentDateTimeString);


        mob = getIntent().getStringExtra("keyMobile");
//        op = getIntent().getStringExtra("keyOperator");

        op = getIntent().getStringExtra("keyOperator");
        amt = getIntent().getStringExtra("keyAmount");
        keyOperator_id = getIntent().getStringExtra("keyOperator_id");
        keyTPin = getIntent().getStringExtra("keyTPin");

        getMob.setText(mob);
        getOp.setText(op);
        getAmt.setText(amt);

        binding.dthWalletBal.setText(getAmountFormat(session.getString(Session.BALANCE_AMOUNT)));


        binding.dthSubmit.setOnClickListener(view -> {
            mob = String.valueOf(binding.dthMobileGet.getText());
            op = String.valueOf(binding.dthOperatorGet.getText());
            amt = String.valueOf(binding.dthAmountGet.getText());

            if (mob.isEmpty()) {
                binding.dthMobileGet.setError("Enter Mobile");
                Toast.makeText(DTHConfirmation.this, binding.dthMobileGet.getError(), Toast.LENGTH_SHORT).show();
                return;
            }
            if (op.isEmpty()) {
                binding.dthOperatorGet.setError("Enter Operator");
                Toast.makeText(DTHConfirmation.this, binding.dthOperatorGet.getError(), Toast.LENGTH_SHORT).show();
                return;
            }
            if (amt.isEmpty()) {
                binding.dthAmountGet.setError("Enter Amount");
                Toast.makeText(DTHConfirmation.this, binding.dthAmountGet.getError(), Toast.LENGTH_SHORT).show();
                return;
            }


            DTHRecharge();

        });

    }


    private void DTHRecharge() {
//        showProgressDialog();
        showLoadingDialog();
        String url = Constant.RECHARGE_API + "?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD) + "&mobile=" + mob + "&amount=" + amt + "&tpin=" + keyTPin + "&op=" + keyOperator_id + "&tok=" + session.getString(Session.TOKEN);
        System.out.println(url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        hideProgressDialog();
                        hideLoadingDialog();
                        Log.e("strrrrr", ">>" + response);

                        binding.dthSubmit.setEnabled(true);
                        binding.errorLytDTH.setVisibility(View.GONE);
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

                            } else {
                                if (obj.has("Resp")) {
                                    binding.errorLytDTH.setVisibility(View.VISIBLE);
                                    binding.errorLytDTH.setText(obj.getString("Resp"));
                                } else {
                                    Toast.makeText(DTHConfirmation.this, "Unknown Error...", Toast.LENGTH_SHORT).show();
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
//                        hideProgressDialog();
                        hideLoadingDialog();
                        //displaying the error in toast if occurrs
                        Toast.makeText(DTHConfirmation.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(DTHConfirmation.this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }

    void showErrorDialog(String status, String remark) {
        final Dialog dia = new Dialog(DTHConfirmation.this);
        //We have added a title in the custom layout. So let's disable the default title.
        dia.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dia.setCancelable(true);
        //Mention the name of the layout of your custom dialog.
        ActivityDthsuccessBinding binding = ActivityDthsuccessBinding.inflate(getLayoutInflater());
        dia.setContentView(binding.getRoot());


        binding.dthMobileNumber.setText(mob);
        binding.dthOpSuccess.setText(op);
        binding.dthRupee.setText(amt);

        if (status.equalsIgnoreCase("FAILED")) {
            binding.dthGifFailed.setVisibility(View.VISIBLE);
            binding.dthGif.setVisibility(View.GONE);
            binding.dthRechargeStatus.setText("DTH Recharge Failed");
            binding.dthStatusImage.setImageDrawable(ContextCompat.getDrawable(DTHConfirmation.this, R.drawable.failed));
            binding.dthRechargeStatus.setTextColor(ContextCompat.getColor(DTHConfirmation.this, R.color.red));
            binding.dthTranscationId.setText(remark);
            binding.dthGif.setImageDrawable(ContextCompat.getDrawable(DTHConfirmation.this, R.drawable.failure));
        } else if (status.equalsIgnoreCase("PENDING")) {
            binding.dthStatusImage.setImageDrawable(ContextCompat.getDrawable(DTHConfirmation.this, R.drawable.ic_autopay_grey));
//            binding.dthRechargeStatus.setText("DTH Recharge Pending");
            binding.dthRechargeStatus.setText(status);
            binding.dthRechargeStatus.setTextColor(ContextCompat.getColor(DTHConfirmation.this, R.color.blue));
            binding.dthGifFailed.setVisibility(View.GONE);
            binding.dthGif.setVisibility(View.VISIBLE);
            binding.dthTranscationId.setVisibility(View.GONE);
            binding.dthTranscationId.setText(remark);
        } else {
            binding.dthStatusImage.setImageDrawable(ContextCompat.getDrawable(DTHConfirmation.this, R.drawable.success));
            binding.dthRechargeStatus.setText(status);
            binding.dthRechargeStatus.setTextColor(ContextCompat.getColor(DTHConfirmation.this, R.color.green));
            binding.dthGifFailed.setVisibility(View.GONE);
            binding.dthGif.setVisibility(View.VISIBLE);
            binding.dthTranscationId.setText(remark);
        }
        binding.dthDate.setText(Constant.getDate());

        binding.dthGotId.setOnClickListener(v -> {
            dia.dismiss();
            setResult();
        });

        binding.shareDTH.setOnClickListener(v -> {
            if (CheckPermission()) {
                shareImage(getMainFrameBitmap(binding.rootViewDTH));
            }
        });


        dia.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            SaveImage();
            Button share = (Button) findViewById(R.id.shareDTH);
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