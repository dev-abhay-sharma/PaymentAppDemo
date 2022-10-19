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
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.activities.TransferMoney;
import com.swinfotech.swpay.model.BenefListCFModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class BeneficiaryListAdapterCF extends RecyclerView.Adapter<BeneficiaryListAdapterCF.ViewHolder> {

    LayoutInflater inflater;
    List<BenefListCFModel> beneList;
    Context context;
    String type = "Select";

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    Session session;

    String NEFT = "127";
    String IMPS = "58";


    public BeneficiaryListAdapterCF(Context context, List<BenefListCFModel> beneList) {
        this.inflater = LayoutInflater.from(context);
        this.beneList = beneList;
        this.context = context;
        this.session = new Session(context);
    }

    @NonNull
    @Override
    public BeneficiaryListAdapterCF.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.beneficiary_list_api_cf, parent, false);
        return new BeneficiaryListAdapterCF.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BeneficiaryListAdapterCF.ViewHolder holder, int position) {

        holder.name.setText(getTextFromString(beneList.get(position).getBenef_name()));
        holder.accountNo.setText(getTextFromString(beneList.get(position).getBank_ac()));
        //   holder.accType.setText(getTextFromString(beneList.get(position).get()));
        holder.ifsc.setText(getTextFromString(beneList.get(position).getIfsc()));
        holder.bankName.setText(getTextFromString(beneList.get(position).getBank_name()));

        holder.beneId.setText(getTextFromString(beneList.get(position).getBenef_id()));
        //  holder.verify.setText(getTextFromString(beneList.get(position).getVerify()));
        if (beneList.get(position).isVerified()) {
            holder.isValidate.setVisibility(View.GONE);
        } else {
            holder.isValidate.setVisibility(View.VISIBLE);
        }
        holder.isValidate.setOnClickListener(v -> {
            openVerifyBeneCF(beneList.get(position), position);

        });

        holder.moneyTransfer.setOnClickListener(v -> {
            transferInfo(beneList.get(position));
        });

        holder.deleteBene.setOnClickListener(v -> {
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
        TextView name, accountNo, accType, ifsc, bankName, verify, beneId;
        LinearLayout deleteBene, moneyTransfer, isValidate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.listNameCF);
            accountNo = itemView.findViewById(R.id.listAccountNoCF);
            accType = itemView.findViewById(R.id.listAccountTypeCF);
            ifsc = itemView.findViewById(R.id.listIfscCF);
            bankName = itemView.findViewById(R.id.listBankNameCF);
            deleteBene = itemView.findViewById(R.id.listDeleteCF);
            moneyTransfer = itemView.findViewById(R.id.beneMoneyTransferCF);

            isValidate = itemView.findViewById(R.id.listVerifyCf);

            beneId = itemView.findViewById(R.id.listBeneId2CF);
            verify = itemView.findViewById(R.id.listVerify2CF);

        }
    }


    private void transferInfo(BenefListCFModel listMoney) {
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


        String[] itemsId = new String[]{"Mode", "58", "127"};
        String[] items = new String[]{"Mode", "IMPS", "NEFT"};
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

        name.setText(listMoney.getBenef_name());
        accountNo.setText(listMoney.getBank_ac());
        bankName.setText(listMoney.getBank_name());
        String mobileNo = listMoney.getBenef_mobile();
        //String beneId = listMoney.getCode();

//        String aadharNo = listMoney2.getAadharNo();
//        String panNo = listMoney2.getPanNo();
        aadhar.setVisibility(View.GONE);
        pan.setVisibility(View.GONE);
        pay.setOnClickListener(v -> {

           /* if (type.equalsIgnoreCase("Mode")){
                Toast.makeText(context, "Please Select Payment Method", Toast.LENGTH_SHORT).show();
            } else*/
            if (getStringFromEditText(amount) == null) {
                amount.setError("Please Enter Amount");
                amount.requestFocus();
            } else if (getStringFromEditText(tPin) == null) {
                tPin.setError("Please Enter TPin");
                tPin.requestFocus();
            } else if (getStringFromEditText(tPin).length() != 4) {
                tPin.setError("Please Enter 4 Digit TPIN");
                tPin.requestFocus();
            }



           /* else if (getStringFromEditText(aadharNo) == null) {
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
            }*/
            else {
                transferMoneyAPI(dialog, getStringFromEditText(amount), getStringFromEditText(tPin), listMoney.getBenef_id());
            }

        });


        dialog.show();

    }

    private void transferMoneyAPI(Dialog dialog, String amount, String tPin, String beneId) {
        showLoadingDialog();
        String url = "https://swpay.in/api/AndroidAPI/DMTCFDoTransaction";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
                        try {
                            JSONObject object = new JSONObject(response);


                            String amount = "";
                            if (object.has("AdminChargeNetAmt")) {
                                amount = object.getString("AdminChargeNetAmt");
                            }
                            String name = "";
                            if (object.has("benef_name")) {
                                name = object.getString("benef_name");
                            }
                            /*String ifsc = "";
                            if (object.has("AdminChargeNetAmt")) {
                                amount = object.getString("AdminChargeNetAmt");
                            }*/
                            String acno = "";
                            if (object.has("ac_no")) {
                                acno = object.getString("ac_no");
                            }

                            String resp = "";
                            if (object.has("Resp")) {
                                resp = object.getString("Resp");
                            }

                            String remark = "";
                            if (object.has("Remarks")) {
                                remark = object.getString("Remarks");
                            }

                            String status = "";
                            if (object.has("Status")) {
                                status = object.getString("Status");
                            }

                            String apiRef = "";
                            if (object.has("api_ref")) {
                                apiRef = object.getString("api_ref");
                            }


                            dialog.dismiss();
                            showSuccessTransferAPIBeta(remark, status, apiRef, amount, resp, name, acno);

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
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                        System.out.println(error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", session.getString(Session.MOBILE));
                params.put("password", session.getString(Session.PASSWORD));
                params.put("WithdrawAmtBank", amount);
                params.put("WithdrawTPIN", tPin);
                params.put("BenefIdBank", beneId);

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

    private void showSuccessTransferAPIBeta(String remarks, String status, String id, String amt, String resp, String holderName, String acno) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.money_transfer_success_cf);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.98f);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.findViewById(R.id.llMobileNumer).setVisibility(View.GONE);
        GifImageView failed, success;
        failed = dialog.findViewById(R.id.gifMoneyTransferBetaFailed);
        success = dialog.findViewById(R.id.gifMoneyTransferBetaSuccess);

        Button done = dialog.findViewById(R.id.moneyTrDoneBeta);
        done.setOnClickListener(v -> {
            dialog.dismiss();
        });

        TextView remark, acNo, ifsc, statusNew, amount, bankTrId1, name, Resp;

        remark = dialog.findViewById(R.id.moneyTrRemarksBeta);
        name = dialog.findViewById(R.id.moneyName);
        dialog.findViewById(R.id.moneyMobileNoBeta).setVisibility(View.GONE);
        //  ifsc = dialog.findViewById(R.id.moneyIFSC);
        statusNew = dialog.findViewById(R.id.moneyStatusBeta);
        amount = dialog.findViewById(R.id.moneyTrAmountBeta);
        bankTrId1 = dialog.findViewById(R.id.moneyTrBankTrIdBeta);
        acNo = dialog.findViewById(R.id.moneyAccount);
        LinearLayout Resp1 = dialog.findViewById(R.id.moneyTrRespBeta1);
        Resp = dialog.findViewById(R.id.moneyTrRespBeta);

        if (status.equalsIgnoreCase("SUCCESS")) {
            failed.setVisibility(View.GONE);
            remark.setText(remarks);
            acNo.setText(acno);
            name.setText(holderName);
            // ifsc.setText(holderIfsc);
            remark.setTextColor(ContextCompat.getColor(context, R.color.green));
            // beneficiaryCode.setText(benCode);
            statusNew.setText(status);
            amount.setText(amt);
            bankTrId1.setText(id);

        } else if (status.equalsIgnoreCase("PENDING")) {
            failed.setVisibility(View.GONE);
            remark.setText(remarks);
            remark.setTextColor(ContextCompat.getColor(context, R.color.blue));
            acNo.setText(acno);
            name.setText(holderName);
            //  ifsc.setText(holderIfsc);
            statusNew.setText(status);
            amount.setText(amt);
            bankTrId1.setText(id);

        } else {
            remark.setText(remarks);
            remark.setTextColor(ContextCompat.getColor(context, R.color.red));
            acNo.setText(acno);
            name.setText(holderName);
            //   ifsc.setText(holderIfsc);
            statusNew.setText(status);
            amount.setText(amt);
            bankTrId1.setText(id);
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

    private void deleteBeneShow(BenefListCFModel benefListCFModel, int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.delete_bene_dmt_list_beta);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.98f);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        TextView beneNo = dialog.findViewById(R.id.deleteBeneNo);
        Button delete, cancel;
        delete = dialog.findViewById(R.id.deleteBeneDMTBeta);
        cancel = dialog.findViewById(R.id.cancelBeneDMTBeta);

        // beneNo.setText(beneListDelete.getCode());
        String mobileNo = benefListCFModel.getBenef_mobile();
        String beneId = benefListCFModel.getBenef_id();

        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        delete.setOnClickListener(v -> {
            runDeleteBeneAPI(dialog, beneId, mobileNo, benefListCFModel, position);
        });


        dialog.show();
    }

    private void runDeleteBeneAPI(Dialog dialog, String beneId, String mobileNo, BenefListCFModel beneListDelete, int position) {
        showLoadingDialog();
        String url = Constant.DEL_DMTCF_BENF;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    hideLoadingDialog();
                    System.out.println(response);
                    try {
                        JSONObject object = new JSONObject(response);
                        String msg = object.getString("Resp");
                        dialog.dismiss();
                        showSuccessDelete(msg, position);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Successfully");
                },
                error -> {
                    hideLoadingDialog();
                    Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                    System.out.println(error);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", session.getString(Session.MOBILE));
                params.put("password", session.getString(Session.PASSWORD));
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

    private void showSuccessDelete(String msg, int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.delete_success_bene_dmt_cf);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.98f);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        Button deleteSuccess = dialog.findViewById(R.id.deleteBeneSuccessDMTCf);

        deleteSuccess.setOnClickListener(v -> {
            dialog.dismiss();
          //  beneList.remove(position);
            TransferMoney con = (TransferMoney) context;
            con.getDMTBeneAPICF();
            notifyItemRemoved(position);
        });


        TextView status;

        status = dialog.findViewById(R.id.deleteBeneStatus);
        status.setText(msg);


        dialog.show();
    }


    private void verifyBeneAPICF(Dialog dialog, String verifyBeneNo, String verifyBeneAadharNo, String verifyBenePanNo, String ifsc, String accNo, String mobileNo, int position) {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/DMTCFVerifyBenef";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    hideLoadingDialog();
                    Log.e("TAG", "onResponse: " + response);
                    try {
                        JSONObject object = new JSONObject(response);

                       /* String verifyAccNo = "";
                        if (object.has("VerifyACNo")) {
                            verifyAccNo = object.getString("VerifyACNo");
                        }*/

                        String verifyBankName = "";
                        if (object.has("BankName")) {
                            verifyBankName = object.getString("BankName");
                        }

                        String BranchName = "";
                        if (object.has("BranchName")) {
                            BranchName = object.getString("BranchName");
                        }

                        String AccountHolderName = "";
                        if (object.has("AccountHolderName")) {
                            AccountHolderName = object.getString("AccountHolderName");
                        }

                        String verifyIfscCode = "";
                        if (object.has("VerifyIFSCCode")) {
                            verifyIfscCode = object.getString("VerifyIFSCCode");
                        }

                        String Message = "";
                        if (object.has("Message")) {
                            Message = object.getString("Message");
                        }

                        String apistatus = "";
                        if (object.has("apistatus")) {
                            apistatus = object.getString("apistatus");
                        }

                        String BankCity = "";
                        if (object.has("BankCity")) {
                            BankCity = object.getString("BankCity");
                        }


                        dialog.dismiss();
                        showSuccessVerifyCF(verifyBankName, verifyBankName, BranchName, AccountHolderName, verifyIfscCode, Message, apistatus, BankCity, position);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Successfully");
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoadingDialog();
                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                        System.out.println(error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", session.getString(Session.MOBILE));
                params.put("password", session.getString(Session.PASSWORD));
                params.put("BenefId", verifyBeneNo);
                params.put("VerifyBankAc", accNo);
                params.put("VerifyBankIFSC", ifsc);

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

    private void showSuccessVerifyCF(String verifyBankName, String bankName, String branchName, String accountHolderName, String verifyIfscCode, String message, String apistatus, String bankCity, int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.verify_bene_dmt_done_cf);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.98f);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        GifImageView failed, success;
        failed = dialog.findViewById(R.id.gifVerifyCFFailed);
        success = dialog.findViewById(R.id.gifVerifyCFSuccess);

        TextView msg = dialog.findViewById(R.id.verifyBeneCFMessage);
        TextView BranchName = dialog.findViewById(R.id.verifyBranchName);
        TextView BankCity = dialog.findViewById(R.id.verifyBankCityCf);
        TextView BankName = dialog.findViewById(R.id.verifyBeneCFBankName);
        TextView name = dialog.findViewById(R.id.verifyHolderName);

        Button done = dialog.findViewById(R.id.verifyBeneCFSuccessDone);
        done.setOnClickListener(v -> {
            dialog.dismiss();
            TransferMoney con = (TransferMoney) context;
            con.getDMTBeneAPICF();

//            BeneficiaryListBetaGetSet verify = beneList.get(position);
//            verify.set


        });

        if (apistatus.equalsIgnoreCase("SUCCESS")) {
            failed.setVisibility(View.GONE);
            success.setVisibility(View.VISIBLE);
            msg.setText(message);
            BranchName.setText(branchName);
            //bankAccNo.setText(verifyAccNo);
            BankName.setText(verifyBankName);
            BankCity.setText(bankCity);
            name.setText(accountHolderName);
            //  mobileNo.setText(verifyBenMobile);
        } else {
            dialog.findViewById(R.id.llSuccess).setVisibility(View.GONE);
            msg.setText("Failed : " + message);
            msg.setTextColor(Color.RED);
            BranchName.setText(branchName);
            //bankAccNo.setText(verifyAccNo);
            BankName.setText(verifyBankName);
            BankCity.setText(bankCity);
            name.setText(accountHolderName);
            //   mobileNo.setText(verifyBenMobile);
        }

        dialog.show();

    }

    private void openVerifyBeneCF(BenefListCFModel listVerify, int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.verify_bene_data_dmt_cf);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.98f);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        ImageView imageBack = dialog.findViewById(R.id.trBackVerifyCF);
        EditText etHolderName = dialog.findViewById(R.id.verifyBeneNameCF);
        EditText etBankName = dialog.findViewById(R.id.verifyBeneBankNameCF);
        EditText etNumber = dialog.findViewById(R.id.verifyBeneAcCF);
        EditText etIFSC = dialog.findViewById(R.id.verifyBeneIFSCCF);
        Button verify = dialog.findViewById(R.id.verifyBeneDoneCF);

        etHolderName.setText(listVerify.getBenef_name());
        etHolderName.setEnabled(false);
        etNumber.setText(listVerify.getBank_ac());
        etNumber.setEnabled(false);
        etIFSC.setText(listVerify.getIfsc());
        etIFSC.setEnabled(false);
        etBankName.setText(listVerify.getBank_name());
        etBankName.setEnabled(false);


        imageBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        /*

        verifyBeneNo.setText(listVerify.getCode());
        verifyBeneNo.setEnabled(false);

        String ifsc = listVerify.getIfsc();
        String accNo = listVerify.getAccNo();
        String mobileNo = listVerify.getMobileNo();*/

        verify.setOnClickListener(v -> {

            verifyBeneAPICF(dialog, listVerify.getBenef_id(), "verifyBeneAadharNo"
                    , "verifyBenePanNo", listVerify.getIfsc()
                    , listVerify.getBank_ac(), listVerify.getBenef_mobile()
                    , position);


        });

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
