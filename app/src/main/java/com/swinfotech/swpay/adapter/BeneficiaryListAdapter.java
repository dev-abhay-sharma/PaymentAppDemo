package com.swinfotech.swpay.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chaos.view.PinView;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.model.BeneficiaryListGetSet;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class BeneficiaryListAdapter extends RecyclerView.Adapter<BeneficiaryListAdapter.ViewHolder> {

    LayoutInflater inflater;
    List<BeneficiaryListGetSet> beneList;
    List<BeneficiaryListGetSet> beneList1;
    Context context;
    String type = "Select";

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    Session session;

    public BeneficiaryListAdapter(Context context, List<BeneficiaryListGetSet> beneList) {
        this.inflater = LayoutInflater.from(context);
        this.beneList = beneList;
        this.beneList1 = beneList;
        this.context = context;
        this.session = new Session(context);
    }

    @NonNull
    @Override
    public BeneficiaryListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.beneficiary_list_api, parent, false);
        return new BeneficiaryListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BeneficiaryListAdapter.ViewHolder holder, int position) {
        holder.name.setText(getTextFromString(beneList.get(position).getName()));
        holder.mobNo.setText(getTextFromString(beneList.get(position).getMobileNo()));

//        holder.rpTid.setText(getTextFromString(beneList.get(position).getrPTid()));

        holder.accountNo.setText(getTextFromString(beneList.get(position).getAccountNum()));
        holder.ifsc.setText(getTextFromString(beneList.get(position).getIfsc()));
        holder.bankName.setText(getTextFromString(beneList.get(position).getBankName()));
        holder.status.setText(getTextFromString(beneList.get(position).getStatus()));

        /*if (isServiceId(beneList.get(position).getIsValidate())) {
            holder.isValidate.setVisibility(View.VISIBLE);
            holder.isValidate.setOnClickListener(v -> {
//                verifyBeneDialog(beneList.get(position));
                verifyBeneAPI(beneList.get(position));

            });
        } else {
            holder.isValidate.setVisibility(View.GONE);
        }*/

       /* holder.deleteBene.setOnClickListener(v -> {
            deleteBene(beneList.get(position), position);
//            position

        });*/

        holder.moneyTransfer.setOnClickListener(v -> {
            transferInfo(beneList.get(position));
        });

    }


    @Override
    public int getItemCount() {
        return beneList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, mobNo, rpTid, accountNo, ifsc, bankName, status;
        LinearLayout isValidate, deleteBene, moneyTransfer;

        //        ImageView deleteBene, moneyTransfer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.listName);
            mobNo = itemView.findViewById(R.id.listMobNo);

//            rpTid = itemView.findViewById(R.id.listRpTid);

            accountNo = itemView.findViewById(R.id.listAccountNo);
            ifsc = itemView.findViewById(R.id.listIfsc);
            bankName = itemView.findViewById(R.id.listBankName);
            status = itemView.findViewById(R.id.listStatus);
            isValidate = itemView.findViewById(R.id.listVerify);
            deleteBene = itemView.findViewById(R.id.listDelete);
            moneyTransfer = itemView.findViewById(R.id.beneMoneyTransfer);

        }
    }

    private void transferInfo(BeneficiaryListGetSet listMoney) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_transfer_info);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.98f);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        ImageView close;
        close = dialog.findViewById(R.id.trBack);

        close.setOnClickListener(v -> {
            dialog.dismiss();
        });

        String remitterId = listMoney.getRemitterID();
        String rPTid = listMoney.getrPTid();
        String mobileNo = listMoney.getMobileNo();

        EditText name, accountNo, bankName, amount, tPin;
        TextView errorTPin;
        Button pay;

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.moneyTransferType, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner select = dialog.findViewById(R.id.trSelectTypeBene);
        select.setAdapter(adapter);
        select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        name = dialog.findViewById(R.id.trNameBene);
        name.setEnabled(false);
        accountNo = dialog.findViewById(R.id.trAccountNoBene);
        accountNo.setEnabled(false);
        bankName = dialog.findViewById(R.id.trBankNameBene);
        bankName.setEnabled(false);
        amount = dialog.findViewById(R.id.trAmountBene);
        tPin = dialog.findViewById(R.id.trTPinBene);


        errorTPin = dialog.findViewById(R.id.trErrorTPin);
        pay = dialog.findViewById(R.id.payNowBene);

        name.setText(listMoney.getName());
        accountNo.setText(listMoney.getAccountNum());
        bankName.setText(listMoney.getBankName());

        pay.setOnClickListener(v -> {

            if (type.equalsIgnoreCase("Select")) {
                Toast.makeText(context, "Please Select Payment Method", Toast.LENGTH_SHORT).show();
            } else if (getStringFromEditText(amount) == null) {
                amount.setError("Please Enter Amount");
                amount.requestFocus();
            } else if (getStringFromEditText(tPin) == null) {
                tPin.setError("Please Enter tPin");
                tPin.requestFocus();
            } else if (tPin.length() != 4) {
                tPin.setError("Please Enter 4 Digit TPIN");
                tPin.requestFocus();
            } else {
                transferMoneyAPI(dialog, remitterId, rPTid, accountNo, mobileNo, amount, type, tPin, errorTPin, name, listMoney.getIfsc());
            }

        });


        dialog.show();

    }

    private void transferMoneyAPI(Dialog dialog, String remitterId, String rPTid, EditText accountNo, String mobileNo, EditText amount, String type, EditText tPin, TextView errorTPin, EditText name1, String ifsc) {
//        dialog.dismiss();
        showLoadingDialog();
        String accNo = getStringFromEditText(accountNo);
        String amt = getStringFromEditText(amount);
        String TPIN = getStringFromEditText(tPin);
        String name = getStringFromEditText(name1);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.DTM_MONEY_TRANSFER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);

                        try {
                            if (response != null) {
                                JSONObject object = new JSONObject(response);
                                if (object.has("apistatus")) {
                                    String apistatus = "FAILED";
                                    apistatus = object.getString("apistatus");

                                    String remark = "";
                                    if (object.has("remarks")) {
                                        remark = object.getString("remarks");
                                    }

                                    String closingBal = "";
                                    if (object.has("AdminClosingBalance")) {
                                        closingBal = object.getString("AdminClosingBalance");
                                    }

                                    String apiReference = "";
                                    if (object.has("api_ref")) {
                                        apiReference = object.getString("api_ref");
                                    }

                                    String bankTrId = "";
                                    if (object.has("bank_tr_id")) {
                                        bankTrId = object.getString("bank_tr_id");
                                    }

                                    String transferAmount = "";
                                    if (object.has("bank_tr_amount")) {
                                        transferAmount = object.getString("bank_tr_amount");
                                    }

                                    String accountNo = "";
                                    if (object.has("bank_ac_no")) {
                                        accountNo = object.getString("bank_ac_no");
                                    }

                                    String mobNum = "";
                                    if (object.has("ben_mobile")) {
                                        mobNum = object.getString("ben_mobile");
                                    }

                                    String nameBene = "";
                                    if (object.has("ben_name")) {
                                        nameBene = object.getString("ben_name");
                                    }

                                    if (apistatus.equalsIgnoreCase("SUCCESS")) {
                                        dialog.dismiss();
                                        showSuccessDialog(apistatus, remark, closingBal, apiReference, bankTrId, transferAmount, accountNo, mobNum, nameBene, name);
                                    } else {
                                        dialog.dismiss();
                                        showSuccessDialog(apistatus, remark, closingBal, apiReference, bankTrId, transferAmount, accountNo, mobNum, nameBene, name);
                                    }


                                } else {
                                    if (object.has("Resp")) {
                                        Log.e("TAG", "onResponse: ++++++++++++++++++++++++++");
                                        errorTPin.setVisibility(View.VISIBLE);
                                        if (object.getString("Resp").equals("API Inactive/ API Not Forwarded/ Operator API Code Not Updated"))
                                            errorTPin.setText("Service not available. Please try again later");
                                        else errorTPin.setText(object.getString("Resp"));
                                    } else {
                                        Toast.makeText(context, "Unknown Error...", Toast.LENGTH_SHORT).show();
                                    }
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
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();

                params.put("username", session.getString(Session.MOBILE));
                params.put("password", session.getString(Session.PASSWORD));
                params.put("BenefName", name);
                params.put("BenefIFSC", ifsc);
                params.put("BenAcNo", accNo);
                params.put("BenMobileNo", mobileNo);
                params.put("BenTrAmount", amt);
                params.put("BenTrTPIN", TPIN);
                Log.e("PARAMS", "getParams: " + params);
                return params;
            }
        };
        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void showSuccessDialog(String apistatus, String remark, String closingBal, String apiReference, String bankTrId, String transferAmount, String accountNo, String mobNum, String nameBene, String name) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_money_transfer_success);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.98f);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        GifImageView failed, success;
        failed = dialog.findViewById(R.id.gifMoneyTransferFailed);
        success = dialog.findViewById(R.id.gifMoneyTransferSuccess);

        TextView remarks = dialog.findViewById(R.id.moneyTrRemarks);
//        TextView adminBal = dialog.findViewById(R.id.moneyTrBal);
//        TextView referenceId = dialog.findViewById(R.id.moneyTrRef);
        TextView bankTransferId = dialog.findViewById(R.id.moneyTrBankTrId);
        TextView amount = dialog.findViewById(R.id.moneyTrAmount);
        TextView accNo = dialog.findViewById(R.id.moneyTrAccountNo);
//        TextView mobNo = dialog.findViewById(R.id.moneyTrMobNo);
        TextView beneName = dialog.findViewById(R.id.moneyTrName);
        TextView bankBeneName = dialog.findViewById(R.id.moneyTrBankName);
//        TextView message = dialog.findViewById(R.id.moneyTrMessage);


        Button done = dialog.findViewById(R.id.moneyTrDone);
        done.setOnClickListener(v -> {
            dialog.dismiss();
        });

        if (apistatus.equalsIgnoreCase("SUCCESS")) {
            failed.setVisibility(View.GONE);
            success.setVisibility(View.VISIBLE);
            remarks.setText(remark);
            remarks.setTextColor(ContextCompat.getColor(context, R.color.green));
//            adminBal.setText(closingBal);
//            referenceId.setText(apiReference);
            bankTransferId.setText(bankTrId);
            amount.setText(transferAmount);
            amount.setTextColor(ContextCompat.getColor(context, R.color.green));
            accNo.setText(accountNo);
//            mobNo.setText(mobileNo);
            beneName.setText(name);
            bankBeneName.setText(nameBene);
//            message.setVisibility(View.VISIBLE);
        } else if (apistatus.equalsIgnoreCase("PENDING")) {
            failed.setVisibility(View.GONE);
            success.setVisibility(View.VISIBLE);
            remarks.setText(remark);
            remarks.setTextColor(ContextCompat.getColor(context, R.color.blue));
//            adminBal.setText(closingBal);
//            referenceId.setText(apiReference);
            bankTransferId.setText(bankTrId);
            amount.setText(transferAmount);
            amount.setTextColor(ContextCompat.getColor(context, R.color.green));
            accNo.setText(accountNo);
//            mobNo.setText(mobileNo);
            beneName.setText(name);
            bankBeneName.setText(nameBene);
//            message.setVisibility(View.VISIBLE);
        } else {
            success.setVisibility(View.GONE);
            failed.setVisibility(View.VISIBLE);
            remarks.setText(remark);
            remarks.setTextColor(ContextCompat.getColor(context, R.color.red));
//            adminBal.setText(closingBal);
//            referenceId.setText(apiReference);
            bankTransferId.setText(bankTrId);
            amount.setText(transferAmount);
            amount.setTextColor(ContextCompat.getColor(context, R.color.red));
            accNo.setText(accountNo);
//            mobNo.setText(mobileNo);
            beneName.setText(name);
            bankBeneName.setText(nameBene);
        }

        Button share = dialog.findViewById(R.id.shareMoneyTr);
        share.setOnClickListener(v -> {
            if (CheckPermission()) {
                shareImage(getMainFrameBitmap(dialog.findViewById(R.id.rootViewMoneyTransfer)));
            }
        });

        dialog.show();

    }

    // share Image Process

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
////            SaveImage();
//            Button share = (Button) findViewById(R.id.shareDTH);
////            binding.shareMobile.performClick();
//            share.performClick();
//        } else {
//            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
//        }
//
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//    }


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
        int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return false;
        }
        return true;
    }

    private void shareImage(Bitmap bitmap) {
        try {
            String bitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "title", null);
            Uri bitmapUri = Uri.parse(bitmapPath);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/png");
            intent.putExtra(Intent.EXTRA_TEXT, ""); // add your custom msg here
            intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
            context.startActivity(Intent.createChooser(intent, "Share"));
        } catch (Exception e) {
            e.printStackTrace();
            // showMessage("Something Went Wrong...");
        }
    }


    private void deleteBene(BeneficiaryListGetSet listGetSet, int position) {
        showLoadingDialog();
        String remitterId = listGetSet.getRemitterID();
        String rPTid = listGetSet.getrPTid();
        String url = "username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&senderid=" + remitterId
                + "&beneficiaryid=" + rPTid;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.BENE_SENT_OTP + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);

                        try {
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject object = jsonObject.getJSONObject("apiSendOTPBen");

                                if (object.has("StatusCode")) {
                                    String status = "";
                                    status = object.getString("StatusCode");

                                    String message = "";
                                    if (object.has("Message")) {
                                        message = object.getString("Message");
                                    }

                                    if (status.equalsIgnoreCase("1")) {
                                        showOTPDialog(status, message, remitterId, rPTid, position);
                                    } else {
                                        showOTPDialog(status, message, remitterId, rPTid, position);
                                    }

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
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }

    private void showOTPDialog(String status, String message, String remitterId, String rPTid, int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_tpin);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.98f);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        ImageView close;
        close = dialog.findViewById(R.id.closeOTP);
        close.setOnClickListener(view -> {
            dialog.dismiss();
        });

        PinView otp = dialog.findViewById(R.id.beneDeleteOTP);
        TextView beneRPTidName = dialog.findViewById(R.id.deleteRPTid);
        beneRPTidName.setText(rPTid);
        Button deleteBtn = dialog.findViewById(R.id.deleteDone);

        deleteBtn.setOnClickListener(v -> {
            if (otp == null) {
                otp.setError("Please Enter OTP");
                otp.requestFocus();
            } else if (otp.length() != 6) {
                otp.setError("Please Enter 6 Digit OTP");
                otp.requestFocus();
            } else {
                deleteBeneDone(dialog, otp, remitterId, rPTid, position);
            }
        });

        dialog.show();

    }

    private void deleteBeneDone(Dialog dialog, PinView otp, String remitterId, String rPTid, int position) {
        dialog.dismiss();
        showLoadingDialog();
        String OTP = String.valueOf(otp.getText());
        String url = "username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&senderid=" + remitterId
                + "&beneficiaryid=" + rPTid
                + "&otp=" + OTP;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.DTM_DELETE_BENE + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);

                        try {
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject object = jsonObject.getJSONObject("apiDelBen");

                                if (object.has("StatusCode")) {
                                    String status = "";
                                    status = object.getString("StatusCode");

                                    String message = "";
                                    if (object.has("Message")) {
                                        message = object.getString("Message");
                                    }

                                    if (status.equalsIgnoreCase("0")) {
                                        showDeleteSuccessDialog(status, message, position);
                                    } else {
                                        showDeleteSuccessDialog(status, message, position);
                                    }

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
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void showDeleteSuccessDialog(String status, String message, int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_delete_bene_success);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.98f);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        GifImageView failed, success;
        TextView msg;
        Button done;

        failed = dialog.findViewById(R.id.gifDeleteBeneFailed);
        success = dialog.findViewById(R.id.gifDeleteBeneSuccess);
        msg = dialog.findViewById(R.id.deleteResultBene);
        done = dialog.findViewById(R.id.deleteDoneBene);


        if (status.equalsIgnoreCase("0")) {
            success.setVisibility(View.GONE);
            failed.setVisibility(View.VISIBLE);
            msg.setText(message);
            msg.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            failed.setVisibility(View.GONE);
            success.setVisibility(View.VISIBLE);
            msg.setText(message);
            msg.setTextColor(ContextCompat.getColor(context, R.color.green));
        }

        done.setOnClickListener(v -> {
            dialog.dismiss();
            // This how we remove list item in adapter view
            beneList.remove(position);
            notifyItemRemoved(position);

        });

        dialog.show();

    }


    private void verifyBeneAPI(BeneficiaryListGetSet listGetSet) {
        showLoadingDialog();
        String remitterId = listGetSet.getRemitterID();
        String rPTid = listGetSet.getrPTid();
        String url = "username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&senderid=" + remitterId
                + "&benid=" + rPTid;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.DMT_VERIFY_BENE + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
                        try {
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject object = jsonObject.getJSONObject("apiVerifyBen");

                                String status = "";
                                if (object.has("StatusCode")) {
                                    status = object.getString("StatusCode");
                                }

                                String error = "";
                                if (object.has("Message")) {
//                                    error.setVisibility(View.VISIBLE);
                                    error = object.getString("Message");
                                }

                                JSONObject obj = object.getJSONObject("Data");

                                String name = "";
                                if (obj.has("RecipientName")) {
                                    name = obj.getString("RecipientName");
                                }

                                String mobileNo = "";
                                if (obj.has("MobileNo")) {
                                    mobileNo = obj.getString("MobileNo");
                                }

                                String accountNo = "";
                                if (obj.has("BankAccNo")) {
                                    accountNo = obj.getString("BankAccNo");
                                }

                                String trId = "";
                                if (obj.has("BankTxnId")) {
                                    trId = obj.getString("BankTxnId");
                                }

                                String orId = "";
                                if (obj.has("OrderId")) {
                                    orId = obj.getString("OrderId");
                                }

                                String refNo = "";
                                if (obj.has("TxnRefNumber")) {
                                    refNo = obj.getString("TxnRefNumber");
                                }

                                if (status.equalsIgnoreCase("0")) {
                                    showVerifyBeneDialog(status, error, name, mobileNo, accountNo, trId, orId, refNo);
                                } else {
                                    showVerifyBeneDialog(status, error, name, mobileNo, accountNo, trId, orId, refNo);
                                }

                                System.out.println(obj);
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
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }

    private void showVerifyBeneDialog(String status, String error, String name, String mobileNo, String accountNo, String trId, String orId, String refNo) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_veryfy_beneficiary);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.98f);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);


        EditText name1, mobileNo1, accountNo1, trId1, orId1, refNo1;
        TextView error1;

        ImageView close;
        close = dialog.findViewById(R.id.beneClose);
        close.setOnClickListener(v -> {
            dialog.dismiss();
        });

        name1 = dialog.findViewById(R.id.nameBene);
        mobileNo1 = dialog.findViewById(R.id.mobNoBene);
        accountNo1 = dialog.findViewById(R.id.accountNoBene);
        trId1 = dialog.findViewById(R.id.trIdBene);
        orId1 = dialog.findViewById(R.id.orIdBene);
        refNo1 = dialog.findViewById(R.id.refNoBene);
        error1 = dialog.findViewById(R.id.errorBene);

        name1.setText(name);
        name1.setEnabled(false);

        mobileNo1.setText(mobileNo);
        mobileNo1.setEnabled(false);

        accountNo1.setText(accountNo);
        accountNo1.setEnabled(false);

        trId1.setText(trId);
        trId1.setEnabled(false);

        orId1.setText(orId);
        orId1.setEnabled(false);

        refNo1.setText(refNo);
        refNo1.setEnabled(false);

        error1.setText(error);
        error1.setVisibility(View.VISIBLE);

        dialog.show();

    }


    // check he serviceId is false or not (Electricity)
    public boolean isServiceId(String id) {
        // return true if status failed else return false if status success
        return id.equalsIgnoreCase("false");
    }

    private String getTextFromString(String data) {

        // check the data is null or not
        // second condition is optional you can remove this
        if (data == null || data.equalsIgnoreCase("null")) {
            return "";
        }
        return data;
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

    private void showLoadingDialog() {
        if (mDialog == null) {
            mDialog = new SimpleArcDialog(context);
            configuration = new ArcConfiguration(context);
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
