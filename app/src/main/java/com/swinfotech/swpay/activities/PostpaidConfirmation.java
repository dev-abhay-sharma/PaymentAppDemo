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
import com.swinfotech.swpay.databinding.ActivityPostpaidConfirmationBinding;
import com.swinfotech.swpay.databinding.ActivityPostpaidSuccessBinding;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class PostpaidConfirmation extends AppCompatActivity {

    ActivityPostpaidConfirmationBinding binding;
    PostpaidConfirmation activity;

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
        binding = ActivityPostpaidConfirmationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        session = new Session(this);

        activity = this;


        binding.postpaidBack.setOnClickListener(view -> {
            onBackPressed();
        });

        getMob = (TextView) findViewById(R.id.postpaid_mobile_get);
        getOp = (TextView) findViewById(R.id.postpaid_operator_get);
        getAmt = (TextView) findViewById(R.id.postpaid_amount_get);
        dAndT = (TextView) findViewById(R.id.postpaid_date_and_time);

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

        binding.postpaidWalletBal.setText(getAmountFormat(session.getString(Session.BALANCE_AMOUNT)));


        binding.postpaidSubmit.setOnClickListener(view -> {
            mob = String.valueOf(binding.postpaidMobileGet.getText());
            op = String.valueOf(binding.postpaidOperatorGet.getText());
            amt = String.valueOf(binding.postpaidAmountGet.getText());

            if (mob.isEmpty()) {
                binding.postpaidMobileGet.setError("Enter Mobile");
                Toast.makeText(PostpaidConfirmation.this, binding.postpaidMobileGet.getError(), Toast.LENGTH_SHORT).show();
                return;
            }
            if (op.isEmpty()) {
                binding.postpaidOperatorGet.setError("Enter Operator");
                Toast.makeText(PostpaidConfirmation.this, binding.postpaidOperatorGet.getError(), Toast.LENGTH_SHORT).show();
                return;
            }
            if (amt.isEmpty()) {
                binding.postpaidAmountGet.setError("Enter Amount");
                Toast.makeText(PostpaidConfirmation.this, binding.postpaidAmountGet.getError(), Toast.LENGTH_SHORT).show();
                return;
            }


            postpaidRecharge();

        });

    }

    private void postpaidRecharge() {
//        showProgressDialog();
        showLoadingDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.RECHARGE_API + "?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD) + "&mobile=" + mob + "&amount=" + amt + "&tpin=" + keyTPin + "&op=" + keyOperator_id + "&tok=" + session.getString(Session.TOKEN),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        hideProgressDialog();
                        hideLoadingDialog();
                        Log.e("strrrrr", ">>" + response);

                        binding.postpaidSubmit.setEnabled(true);
                        binding.errorLytPPaid.setVisibility(View.GONE);
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
                                    binding.errorLytPPaid.setVisibility(View.VISIBLE);
                                    binding.errorLytPPaid.setText(obj.getString("Resp"));
                                } else {
                                    Toast.makeText(PostpaidConfirmation.this, "Unknown Error...", Toast.LENGTH_SHORT).show();
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
                        try {
                            Toast.makeText(PostpaidConfirmation.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(PostpaidConfirmation.this, "Unable to fetch error from api", Toast.LENGTH_SHORT).show();
                        }
                        //Toast.makeText(PostpaidConfirmation.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(PostpaidConfirmation.this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    void showErrorDialog(String status, String remark) {
        final Dialog dia = new Dialog(PostpaidConfirmation.this);
        //We have added a title in the custom layout. So let's disable the default title.
        dia.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dia.setCancelable(true);
        //Mention the name of the layout of your custom dialog.
        ActivityPostpaidSuccessBinding binding = ActivityPostpaidSuccessBinding.inflate(getLayoutInflater());
        dia.setContentView(binding.getRoot());


        binding.postpaidMobileNumber.setText(mob);
        binding.postpaidOpSuccess.setText(op);
        binding.postpaidRupee.setText(amt);

        if (status.equalsIgnoreCase("FAILED")) {
            binding.postpaidGifFailed.setVisibility(View.VISIBLE);
            binding.postGif.setVisibility(View.GONE);
            binding.postpaidRechargeStatus.setText("Failed");
            binding.postpaidStatusImage.setImageDrawable(ContextCompat.getDrawable(PostpaidConfirmation.this, R.drawable.failed));
            binding.postpaidRechargeStatus.setTextColor(ContextCompat.getColor(PostpaidConfirmation.this, R.color.red));
            binding.postpaidTransactionId.setText(remark);
            binding.postGif.setImageDrawable(ContextCompat.getDrawable(PostpaidConfirmation.this, R.drawable.failure));
        } else if (status.equalsIgnoreCase("PENDING")) {
            binding.postpaidStatusImage.setImageDrawable(ContextCompat.getDrawable(PostpaidConfirmation.this, R.drawable.ic_autopay_grey));
//            binding.postpaidRechargeStatus.setText("Recharge Pending");
            binding.postpaidRechargeStatus.setText(status);
            binding.postpaidRechargeStatus.setTextColor(ContextCompat.getColor(PostpaidConfirmation.this, R.color.blue));
            binding.postpaidGifFailed.setVisibility(View.GONE);
            binding.postGif.setVisibility(View.VISIBLE);
            binding.postpaidTransactionId.setVisibility(View.GONE);
            binding.postpaidTransactionId.setText(remark);
        } else {
            binding.postpaidStatusImage.setImageDrawable(ContextCompat.getDrawable(PostpaidConfirmation.this, R.drawable.success));
            binding.postpaidRechargeStatus.setText(status);
            binding.postpaidRechargeStatus.setTextColor(ContextCompat.getColor(PostpaidConfirmation.this, R.color.green));
            binding.postpaidGifFailed.setVisibility(View.GONE);
            binding.postGif.setVisibility(View.VISIBLE);
            binding.postpaidTransactionId.setText(remark);
        }
        binding.postpaidDate.setText(Constant.getDate());

        binding.postpaidGotId.setOnClickListener(v -> {
            dia.dismiss();
            setResult();
        });


        binding.sharePostpaid.setOnClickListener(v -> {
            if (CheckPermission()) {
                shareImage(getMainFrameBitmap(binding.rootViewPostPaid));
            }
        });

        dia.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            SaveImage();
            Button share = (Button) findViewById(R.id.sharePostpaid);
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


    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(PostpaidConfirmation.this);
            progressDialog.setTitle("Please Wait");
            progressDialog.setMessage("Your Recharge Confirming");
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
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