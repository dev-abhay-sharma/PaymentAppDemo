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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

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
import com.swinfotech.swpay.model.BeneficiaryListBetaGetSet;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class BeneficiaryListAdapterBeta extends RecyclerView.Adapter<BeneficiaryListAdapterBeta.ViewHolder> {

    LayoutInflater inflater;
    List<BeneficiaryListBetaGetSet> beneList;
    List<BeneficiaryListBetaGetSet> beneList2;
    Context context;
    String type="Select";

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    Session session;

    String NEFT = "127";
    String IMPS = "58";

    EditText verifyBeneAadharNo;
    EditText verifyBenePanNo;

    public BeneficiaryListAdapterBeta(Context context, List<BeneficiaryListBetaGetSet> beneList, List<BeneficiaryListBetaGetSet> beneList2) {
        this.inflater = LayoutInflater.from(context);
        this.beneList = beneList;
        this.beneList2 = beneList2;
        this.context = context;
        this.session = new Session(context);
    }

    @NonNull
    @Override
    public BeneficiaryListAdapterBeta.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.beneficiary_list_api_beta, parent, false);
        return new BeneficiaryListAdapterBeta.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BeneficiaryListAdapterBeta.ViewHolder holder, int position) {
        holder.code.setText(getTextFromString(beneList.get(position).getCode()));
        holder.name.setText(getTextFromString(beneList.get(position).getName()));
        holder.accountNo.setText(getTextFromString(beneList.get(position).getAccNo()));
        holder.accType.setText(getTextFromString(beneList.get(position).getAccType()));
        holder.ifsc.setText(getTextFromString(beneList.get(position).getIfsc()));
        holder.bankName.setText(getTextFromString(beneList.get(position).getBankName()));

        holder.beneId.setText(getTextFromString(beneList.get(position).getBeneId1()));
        holder.verify.setText(getTextFromString(beneList.get(position).getVerify()));



        holder.isValidate.setOnClickListener(v -> {
            openVerifyBeneBeta(beneList.get(position), position);

        });


        // verify beneficiary

//        if (beneList.get(position).getCode().equals(beneList2.get(position).getBeneId1())) {
//            if (isServiceId(beneList2.get(position).getVerify())) {
//                holder.isValidate.setVisibility(View.VISIBLE);
//                holder.isValidate.setOnClickListener(v -> {
//                    openVerifyBeneBeta(beneList.get(position), position);
//                });
//            } else {
//                holder.isValidate.setVisibility(View.GONE);
//            }
//        } else {
//            System.out.println("Bene Id Not Matched !");
//        }


        holder.moneyTransfer.setOnClickListener( v -> {
            transferInfo(beneList.get(position));
        });

        holder.deleteBene.setOnClickListener( v -> {
            deleteBeneShow(beneList.get(position), position); // pass the position and list.remove(position) in the end
        });



    }

    public boolean isServiceId(String id) {
        // return true if status failed else return false if status success
        return id.equalsIgnoreCase("false");
    }




    @Override
    public int getItemCount() {
        return beneList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView code, name, accountNo, accType, ifsc, bankName, verify, beneId;
        LinearLayout isValidate, deleteBene, moneyTransfer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            code = itemView.findViewById(R.id.listCodeBeta);
            name = itemView.findViewById(R.id.listNameBeta);
            accountNo = itemView.findViewById(R.id.listAccountNoBeta);
            accType = itemView.findViewById(R.id.listAccountTypeBeta);
            ifsc = itemView.findViewById(R.id.listIfscBeta);
            bankName = itemView.findViewById(R.id.listBankNameBeta);
            isValidate = itemView.findViewById(R.id.listVerifyBeta);
            deleteBene = itemView.findViewById(R.id.listDeleteBeta);
            moneyTransfer = itemView.findViewById(R.id.beneMoneyTransferBeta);

            beneId = itemView.findViewById(R.id.listBeneId2Beta);
            verify = itemView.findViewById(R.id.listVerify2Beta);

        }
    }

    private void openVerifyBeneBeta(BeneficiaryListBetaGetSet listVerify, int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.verify_bene_data_dmt_beta);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.98f);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        ImageView imageBack = dialog.findViewById(R.id.trBackVerifyBeta);
        EditText verifyBeneNo = dialog.findViewById(R.id.verifyBeneIdBeta);
        verifyBeneAadharNo = dialog.findViewById(R.id.verifyBeneAadharBeta);
        verifyBenePanNo = dialog.findViewById(R.id.verifyBenePanNoBeta);
        Button verify = dialog.findViewById(R.id.verifyBeneDoneBeta);

        imageBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        verifyBeneNo.setText(listVerify.getCode());
        verifyBeneNo.setEnabled(false);

        String ifsc = listVerify.getIfsc();
        String accNo = listVerify.getAccNo();
        String mobileNo = listVerify.getMobileNo();

        verify.setOnClickListener(v -> {
            if (getStringFromEditText(verifyBeneAadharNo) == null) {
                verifyBeneAadharNo.setError("Please Enter Aadhar No");
                verifyBeneAadharNo.requestFocus();
            } else if (getStringFromEditText(verifyBeneAadharNo).length() != 12) {
                verifyBeneAadharNo.setError("Please Enter 12 Digit Aadhar No");
                verifyBeneAadharNo.requestFocus();
            } else if (getStringFromEditText(verifyBenePanNo) == null) {
                verifyBenePanNo.setError("Please Enter Pan No");
                verifyBenePanNo.requestFocus();
            } else if (getStringFromEditText(verifyBenePanNo).length() != 10) {
                verifyBenePanNo.setError("Please Enter Pan No");
                verifyBenePanNo.requestFocus();
            } else {
                verifyBeneAPIBeta(dialog, verifyBeneNo, verifyBeneAadharNo, verifyBenePanNo, ifsc, accNo, mobileNo, position);
            }


        });

        dialog.show();

    }

    private void verifyBeneAPIBeta(Dialog dialog, EditText verifyBeneNo, EditText verifyBeneAadharNo, EditText verifyBenePanNo, String ifsc, String accNo, String mobileNo, int position) {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/DMTVerifyBenBeta";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
                        try {
                            JSONObject object = new JSONObject(response);

                            String verifyAccNo = "";
                            if (object.has("VerifyACNo")){
                                verifyAccNo = object.getString("VerifyACNo");
                            }

                            String verifyBankName = "";
                            if (object.has("VerifyBankName")){
                                verifyBankName = object.getString("VerifyBankName");
                            }

                            String verifyBenMobile = "";
                            if (object.has("VerifyBenMobile")){
                                verifyBenMobile = object.getString("VerifyBenMobile");
                            }

                            String verifyBenName= "";
                            if (object.has("VerifyBenName")){
                                verifyBenName = object.getString("VerifyBenName");
                            }

                            String verifyIfscCode= "";
                            if (object.has("VerifyIFSCCode")){
                                verifyIfscCode = object.getString("VerifyIFSCCode");
                            }

                            String verifyMessage= "";
                            if (object.has("VerifyMessage")){
                                verifyMessage = object.getString("VerifyMessage");
                            }

                            String verifyStatus= "";
                            if (object.has("VerifyStatusCode")){
                                verifyStatus = object.getString("VerifyStatusCode");
                            }

                            String verifyTrId= "";
                            if (object.has("VerifyTrId")){
                                verifyTrId = object.getString("VerifyTrId");
                            }


                            dialog.dismiss();
                            showSuccessVerifyBeta(verifyAccNo, verifyBankName, verifyBenMobile, verifyBenName, verifyIfscCode, verifyMessage, verifyStatus, verifyTrId,position);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Successfully");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoadingDialog();
                        Toast.makeText(context,error.toString(),Toast.LENGTH_LONG).show();
                        System.out.println(error);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("username", session.getString(Session.MOBILE));
                params.put("password", session.getString(Session.PASSWORD));
                params.put("VerifyBenId", getStringFromEditText(verifyBeneNo));
                params.put("VerifyRemMobile", mobileNo);
                params.put("VerifyBenAcNo", accNo);
                params.put("VerifyBenIFSC", ifsc);
                params.put("VerifyAadharBenef", getStringFromEditText(verifyBeneAadharNo));
                params.put("VerifyPANBenef", getStringFromEditText(verifyBenePanNo));
                params.put("tok", session.getString(Session.TOKEN));
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

    private void showSuccessVerifyBeta(String verifyAccNo, String verifyBankName, String verifyBenMobile, String verifyBenName, String verifyIfscCode, String verifyMessage, String verifyStatus, String verifyTrId, int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.verify_bene_dmt_done_beta);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.98f);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        GifImageView failed, success;
        failed = dialog.findViewById(R.id.gifVerifyBetaFailed);
        success = dialog.findViewById(R.id.gifVerifyBetaSuccess);

        TextView message = dialog.findViewById(R.id.verifyBeneBetaMessage);
        TextView transactionId = dialog.findViewById(R.id.verifyBeneBetaTrId);
        TextView bankAccNo = dialog.findViewById(R.id.verifyBeneBetaAccNo);
        TextView bankName = dialog.findViewById(R.id.verifyBeneBetaBankAccName);
        TextView ifsc = dialog.findViewById(R.id.verifyBeneBetaIfsc);
        TextView name = dialog.findViewById(R.id.verifyBeneBetaBeneName);
        TextView mobileNo = dialog.findViewById(R.id.verifyBeneBetaBeneMobNo);

        Button done = dialog.findViewById(R.id.verifyBeneBetaSuccessDone);
        done.setOnClickListener(v -> {
            dialog.dismiss();
//            BeneficiaryListBetaGetSet verify = beneList.get(position);
//            verify.set


        });

        if (verifyStatus.equalsIgnoreCase("SUCCESS")) {
            failed.setVisibility(View.GONE);
            success.setVisibility(View.VISIBLE);
            message.setText(verifyMessage);
            transactionId.setText(verifyTrId);
            bankAccNo.setText(verifyAccNo);
            bankName.setText(verifyBankName);
            ifsc.setText(verifyIfscCode);
            name.setText(verifyBenName);
            mobileNo.setText(verifyBenMobile);
        } else {
            message.setText(verifyMessage);
            transactionId.setText(verifyTrId);
            bankAccNo.setText(verifyAccNo);
            bankName.setText(verifyBankName);
            ifsc.setText(verifyIfscCode);
            name.setText(verifyBenName);
            mobileNo.setText(verifyBenMobile);
        }

        dialog.show();

    }

    private void transferInfo(BeneficiaryListBetaGetSet listMoney) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_transfer_info);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.98f);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        RelativeLayout aadhar, pan;
        aadhar = dialog.findViewById(R.id.aadharBeta);
        pan = dialog.findViewById(R.id.panBeta);

        aadhar.setVisibility(View.VISIBLE);
        pan.setVisibility(View.VISIBLE);


        ImageView close;
        close = dialog.findViewById(R.id.trBack);

        close.setOnClickListener(v -> {
            dialog.dismiss();
        });

        EditText name, accountNo, bankName, amount, tPin, aadharNo, panNo;
        TextView errorTPin;
        Button pay;


        String[] itemsId = new String[]{ "Mode", "58", "127"};
        String[] items = new String[]{ "Mode", "IMPS", "NEFT"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, items);
        Spinner select = dialog.findViewById(R.id.trSelectTypeBene);
        select.setAdapter(adapter);
        select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = itemsId[select.getSelectedItemPosition()];
//                Toast.makeText(context, type, Toast.LENGTH_SHORT).show();
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
        aadharNo = dialog.findViewById(R.id.trAadharBeneBeta);
        panNo = dialog.findViewById(R.id.trPanBeneBeta);



        errorTPin = dialog.findViewById(R.id.trErrorTPin);
        pay = dialog.findViewById(R.id.payNowBene);

        name.setText(listMoney.getName());
        accountNo.setText(listMoney.getAccNo());
        bankName.setText(listMoney.getBankName());
        String mobileNo = listMoney.getMobileNo();
        String beneId = listMoney.getCode();

//        String aadharNo = listMoney2.getAadharNo();
//        String panNo = listMoney2.getPanNo();

        pay.setOnClickListener(v -> {

            if (type.equalsIgnoreCase("Mode")){
                Toast.makeText(context, "Please Select Payment Method", Toast.LENGTH_SHORT).show();
            } else if (getStringFromEditText(amount) == null) {
                amount.setError("Please Enter Amount");
                amount.requestFocus();
            } else if (getStringFromEditText(tPin) == null) {
                tPin.setError("Please Enter TPin");
                tPin.requestFocus();
            }
//            else if (getStringFromEditText(tPin).length() != 4) {
//                tPin.setError("Please Enter 4 Digit TPIN");
//                tPin.requestFocus();
//            }
            else if (getStringFromEditText(aadharNo) == null) {
                aadharNo.setError("Please Enter Aadhar No");
                aadharNo.requestFocus();
            } else if (getStringFromEditText(aadharNo).length() != 12) {
                aadharNo.setError("Please Enter 12 Digit Aadhar No");
                aadharNo.requestFocus();
            } else if (getStringFromEditText(panNo) == null) {
                panNo.setError("Please Enter Pan No");
                panNo.requestFocus();
            } else if (getStringFromEditText(panNo).length() != 10) {
                panNo.setError("Please Enter 10 Digit Pan No");
                panNo.requestFocus();
            }
            else {
                transferMoneyAPI(dialog, accountNo, amount, type, tPin, mobileNo, name, aadharNo, panNo, beneId);
            }

        });


        dialog.show();

    }

    private void transferMoneyAPI(Dialog dialog, EditText accountNo, EditText amount, String type, EditText tPin, String mobileNo, EditText name, EditText aadharNo, EditText panNo, String beneId) {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/DMTDoTransactionBeta";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
                        try {
                            JSONObject object = new JSONObject(response);


                            String amount = "";
                            if (object.has("TotalAmount")){
                                amount = object.getString("TotalAmount");
                            }

                            String resp = "";
                            if (object.has("Resp")){
                                resp = object.getString("Resp");
                            }

                            String mobileNo = "";
                            if (object.has("ben_mobile")){
                                mobileNo = object.getString("ben_mobile");
                            }

                            String benCode = "";
                            if (object.has("ben_code")){
                                benCode = object.getString("ben_code");
                            }

                            String status = "";
                            if (object.has("apistatus")){
                                status = object.getString("apistatus");
                            }

                            String apiRef= "";
                            if (object.has("api_ref")){
                                apiRef = object.getString("api_ref");
                            }

                            String remarks= "";
                            if (object.has("remarks")){
                                remarks = object.getString("remarks");
                            }

                            String bankTrId= "";
                            if (object.has("bank_tr_id")){
                                bankTrId = object.getString("bank_tr_id");
                            }

                            String bankAcc= "";
                            if (object.has("bank_ac_no")){
                                bankAcc = object.getString("bank_ac_no");
                            }

                            if (status.equalsIgnoreCase("SUCCESS")){
                                dialog.dismiss();
                                showSuccessTransferAPIBeta(status, mobileNo, benCode, apiRef, remarks, bankTrId, bankAcc, amount, resp);
                            } else {
                                dialog.dismiss();
                                showSuccessTransferAPIBeta(status, mobileNo, benCode, apiRef, remarks, bankTrId, bankAcc, amount, resp);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Successfully");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoadingDialog();
                        Toast.makeText(context,error.toString(),Toast.LENGTH_LONG).show();
                        System.out.println(error);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("username", session.getString(Session.MOBILE));
                params.put("password", session.getString(Session.PASSWORD));
                params.put("SenderIdTrans", mobileNo);
                params.put("RecipientIdTrans", beneId);
                params.put("BenAadharNo", getStringFromEditText(aadharNo));
                params.put("BenPanNo", getStringFromEditText(panNo));
                params.put("BenMobileNo", mobileNo);
                params.put("BenTrAmount", getStringFromEditText(amount));
                params.put("BenTrTPIN", getStringFromEditText(tPin));
                params.put("trans_type", type);
                params.put("BenAcNo", getStringFromEditText(accountNo));
                params.put("tok", session.getString(Session.TOKEN));
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

    private void showSuccessTransferAPIBeta(String status, String mobileNo, String benCode, String apiRef, String remarks, String bankTrId, String bankAcc, String amt, String resp) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.money_transfer_success_beta);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.98f);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        GifImageView failed, success;
        failed = dialog.findViewById(R.id.gifMoneyTransferBetaFailed);
        success = dialog.findViewById(R.id.gifMoneyTransferBetaSuccess);

        Button done = dialog.findViewById(R.id.moneyTrDoneBeta);
        done.setOnClickListener(v -> {
            dialog.dismiss();
        });

        TextView remark, mobNo, beneficiaryCode, statusNew, amount, bankTrId1, apiReference, Resp;

        remark = dialog.findViewById(R.id.moneyTrRemarksBeta);
        mobNo = dialog.findViewById(R.id.moneyMobileNoBeta);
        beneficiaryCode = dialog.findViewById(R.id.moneyBeneCodeBeta);
        statusNew = dialog.findViewById(R.id.moneyStatusBeta);
        amount = dialog.findViewById(R.id.moneyTrAmountBeta);
        bankTrId1 = dialog.findViewById(R.id.moneyTrBankTrIdBeta);
        apiReference = dialog.findViewById(R.id.moneyAPIRefBeta);
        LinearLayout Resp1 = dialog.findViewById(R.id.moneyTrRespBeta1);
        Resp = dialog.findViewById(R.id.moneyTrRespBeta);

        if (status.equalsIgnoreCase("SUCCESS")) {
            failed.setVisibility(View.GONE);
            remark.setText(remarks);
            remark.setTextColor(ContextCompat.getColor(context, R.color.green));
            mobNo.setText(mobileNo);
            beneficiaryCode.setText(benCode);
            statusNew.setText(status);
            amount.setText(amt);
            bankTrId1.setText(bankTrId);
            apiReference.setText(apiRef);
        } else if (status.equalsIgnoreCase("PENDING")) {
            failed.setVisibility(View.GONE);
            remark.setText(remarks);
            remark.setTextColor(ContextCompat.getColor(context, R.color.blue));
            mobNo.setText(mobileNo);
            beneficiaryCode.setText(benCode);
            statusNew.setText(status);
            amount.setText(amt);
            bankTrId1.setText(bankTrId);
            apiReference.setText(apiRef);
        } else {
            remark.setText(remarks);
            remark.setTextColor(ContextCompat.getColor(context, R.color.red));
            mobNo.setText(mobileNo);
            beneficiaryCode.setText(benCode);
            statusNew.setText(status);
            amount.setText(amt);
            bankTrId1.setText(bankTrId);
            apiReference.setText(apiRef);
            Resp1.setVisibility(View.VISIBLE);
            Resp.setText(resp);
        }

        Button share = dialog.findViewById(R.id.shareMoneyTrBeta);
        share.setOnClickListener(v -> {
            if (CheckPermission()) {
                shareImage(getMainFrameBitmap(dialog.findViewById(R.id.rootViewMoneyTransferBeta)));
            }
        });

        dialog.show();

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

    private void deleteBeneShow(BeneficiaryListBetaGetSet beneListDelete, int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.delete_bene_dmt_list_beta);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.98f);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        TextView beneNo = dialog.findViewById(R.id.deleteBeneNo);
        Button delete, cancel;
        delete = dialog.findViewById(R.id.deleteBeneDMTBeta);
        cancel = dialog.findViewById(R.id.cancelBeneDMTBeta);

        beneNo.setText(beneListDelete.getCode());
        String mobileNo = beneListDelete.getMobileNo();
        String beneId = beneListDelete.getCode();

        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        delete.setOnClickListener(v -> {
            runDeleteBeneBetaAPI(dialog, beneId, mobileNo, beneListDelete, position);
        });


        dialog.show();
    }

    private void runDeleteBeneBetaAPI(Dialog dialog, String beneId, String mobileNo, BeneficiaryListBetaGetSet beneListDelete, int position) {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/DMTDelBenBeta";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
                        try {
                            JSONObject object = new JSONObject(response);

                            String deleteBenMobile = "";
                            if (object.has("DelBenMobile")){
                                deleteBenMobile = object.getString("DelBenMobile");
                            }

                            String deleteBenName = "";
                            if (object.has("DelBenName")){
                                deleteBenName = object.getString("DelBenName");
                            }

                            String deleteMessage = "";
                            if (object.has("DelMessage")){
                                deleteMessage = object.getString("DelMessage");
                            }

                            String deleteStatus = "";
                            if (object.has("DelStatusCode")){
                                deleteStatus = object.getString("DelStatusCode");
                            }

                            String deleteVerifyRefNo = "";
                            if (object.has("VerifyRefNo")){
                                deleteVerifyRefNo = object.getString("VerifyRefNo");
                            }


                            if (deleteStatus.equalsIgnoreCase("SUCCESS")){
                                dialog.dismiss();
                                showSuccessDeleteBeta(deleteBenMobile, deleteBenName, deleteMessage, deleteStatus, deleteVerifyRefNo, beneListDelete, position);
                            } else {
                                dialog.dismiss();
                                showSuccessDeleteBeta(deleteBenMobile, deleteBenName, deleteMessage, deleteStatus, deleteVerifyRefNo, beneListDelete, position);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Successfully");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoadingDialog();
                        Toast.makeText(context,error.toString(),Toast.LENGTH_LONG).show();
                        System.out.println(error);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("username", session.getString(Session.MOBILE));
                params.put("password", session.getString(Session.PASSWORD));
                params.put("RemMobileDel", mobileNo);
                params.put("BenIdDel", beneId);
                params.put("tok", session.getString(Session.TOKEN));
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

    private void showSuccessDeleteBeta(String deleteBenMobile, String deleteBenName, String deleteMessage, String deleteStatus, String deleteVerifyRefNo, BeneficiaryListBetaGetSet beneListDelete, int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.delete_success_bene_dmt_beta);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.98f);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        Button deleteSuccess = dialog.findViewById(R.id.deleteBeneSuccessDMTBeta);

        deleteSuccess.setOnClickListener(v -> {
            dialog.dismiss();
            beneList.remove(position);
            notifyItemRemoved(position);
        });

        GifImageView failed, success;
        failed = dialog.findViewById(R.id.deleteGifFailedBeta);
        success = dialog.findViewById(R.id.deleteGifSuccessBeta);

        TextView mobileNo, name, message, status, refNo;
        mobileNo = dialog.findViewById(R.id.deleteBeneNoSuccessBeta);
        name = dialog.findViewById(R.id.deleteBeneNameSuccessBeta);
        message = dialog.findViewById(R.id.deleteBeneMessageSuccessBeta);
        status = dialog.findViewById(R.id.deleteBeneStatusSuccessBeta);
        refNo = dialog.findViewById(R.id.deleteBeneRefNoSuccessBeta);

        if (deleteStatus.equalsIgnoreCase("SUCCESS")) {
            failed.setVisibility(View.GONE);
            mobileNo.setText(deleteBenMobile);
            name.setText(deleteBenName);
            message.setText(deleteMessage);
            status.setText(deleteStatus);
            refNo.setText(deleteVerifyRefNo);
        } else {
            mobileNo.setText(deleteBenMobile);
            name.setText(deleteBenName);
            message.setText(deleteMessage);
            status.setText(deleteStatus);
            refNo.setText(deleteVerifyRefNo);
        }

        dialog.show();
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
