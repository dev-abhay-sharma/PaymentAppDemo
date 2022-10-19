package com.swinfotech.swpay.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.swinfotech.swpay.activities.BBPSCategorie;
import com.swinfotech.swpay.model.CreditBalUsersListGetSet;
import com.swinfotech.swpay.model.HomeModel;
import com.swinfotech.swpay.model.ViewUsersGetSet;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewUsersAdapter extends RecyclerView.Adapter<ViewUsersAdapter.ViewHolder> implements Filterable {

    LayoutInflater inflater;
    List<ViewUsersGetSet> VuReport;
    List<ViewUsersGetSet> backupList;
    //    ArrayList<ViewUsersGetSet> backupList;
    private Context context;

    // Credit balance details
    List<CreditBalUsersListGetSet> CreditList = new ArrayList<>();
    CreditBalUsersAdapter creditBalUsersAdapter;
    private boolean isDataGetSuccessfully = false;

    Session session;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    public ViewUsersAdapter(Context context, List<ViewUsersGetSet> VuReport) {
        this.inflater = LayoutInflater.from(context);
        this.VuReport = VuReport;
        this.context = context;
        this.backupList = VuReport;
        this.session = new Session(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.view_users_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.User_id.setText(getTextFromString(VuReport.get(position).getUserId()));
        holder.User_Name.setText(getTextFromString(VuReport.get(position).getUserName()));
        holder.User_Mobile.setText(getTextFromString(VuReport.get(position).getUserMobile()));
        holder.User_Email.setText(getTextFromString(VuReport.get(position).getUserEmail()));
        if (getTextFromString(VuReport.get(position).getUserStatus()).equalsIgnoreCase("1")) {
            holder.User_Status.setText("Active");
            holder.User_Status.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else {
            holder.User_Status.setText("In Active");
            holder.User_Status.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

//        holder.User_FirmName.setText(getTextFromString(VuReport.get(position).getUserFirmName()));

        holder.Balance_Amount.setText(getTextFromString(getAmountFormat(VuReport.get(position).getBalanceAmt())));

        if (session.getString(Session.SERVICE_ID_WALLET).equalsIgnoreCase("5")) {
            if (session.getString(Session.SERVICE_STATUS_WALLET).equalsIgnoreCase("1")) {
                holder.addBal.setOnClickListener(v -> {
                    openCreditBalDialog(position);
                });
            } else {
                holder.addBal.setOnClickListener(v -> {
                    Toast.makeText(context, "You are not authorized for this service.", Toast.LENGTH_SHORT).show();
                });
                System.out.println("Services Not Working");
            }
        }

        // old
//        holder.addBal.setOnClickListener(v -> {
//            openCreditBalDialog(position);
////            Intent intent = new Intent(context, CreditBalance.class);
////            context.startActivity(intent);
//
//
//        });

    }

    private void openCreditBalDialog(int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_credit_balance);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.98f);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        ImageView back;
        back = (ImageView) dialog.findViewById(R.id.gotoMainDASHBO);

        back.setOnClickListener(v -> {
            dialog.dismiss();
        });

        String userId = VuReport.get(position).getUserId();

        EditText name = (EditText) dialog.findViewById(R.id.userName1);
        EditText mobNo = (EditText) dialog.findViewById(R.id.userMobile1);
        EditText amount = (EditText) dialog.findViewById(R.id.userAmount);
        EditText tPin = (EditText) dialog.findViewById(R.id.userTPIN);
        EditText remark = (EditText) dialog.findViewById(R.id.userRemark);
        Button transfer = (Button) dialog.findViewById(R.id.walletAddBal);

        name.setText(VuReport.get(position).getUserName());
        mobNo.setText(VuReport.get(position).getUserMobile());

        transfer.setOnClickListener(v -> {
            if (getStringFromEditText(name) == null) {
                name.setError("Please Enter Username");
                name.requestFocus();
            } else if (getStringFromEditText(mobNo) == null) {
                mobNo.setError("Please Enter Mobile No");
                mobNo.requestFocus();
            } else if (getStringFromEditText(mobNo).length() != 10) {
                mobNo.setError("Please Enter 10 Digit Mobile No");
                mobNo.requestFocus();
            } else if (getStringFromEditText(amount) == null) {
                amount.setError("Please Enter Amount");
                amount.requestFocus();
            } else if (getStringFromEditText(tPin) == null) {
                tPin.setError("Please Enter TPin");
                tPin.requestFocus();
            }
//            else if (getStringFromEditText(tPin).length() != 4) {
//                tPin.setError("Please Enter 4 Digit TPin");
//                tPin.requestFocus();
//            }
            else if (getStringFromEditText(remark) == null) {
                remark.setError("Please Enter Remark");
                remark.requestFocus();
            } else {
                creditAddAPI(dialog, userId, amount, tPin, remark);
            }

        });


        dialog.show();

    }

    private void creditAddAPI(Dialog dialog, String userId, EditText amount, EditText tPin, EditText remark) {
        showLoadingDialog();
        String user_type = "";
        // check if user is MDT
        if (session.getString(Session.USER_TYPE).equalsIgnoreCase("1")) {
            // then usertype is 2 for DT
            user_type = "2";
        } // if user is DT
        else if (session.getString(Session.USER_TYPE).equalsIgnoreCase("2")) {
            // then user type is 3 for RT
            user_type = "3";
        }

        String Url = "https://mobile.swpay.in/api/AndroidAPI/CreditAdd?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD) + "&credit_amount=" + amount.getText().toString() + "&tpin=" + tPin.getText().toString() + "&user_type=" + user_type + "&target_user_id=" + userId + "&comment=" + remark.getText().toString() + "&tok=" + session.getString(Session.TOKEN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        Log.d("Response", response);
                        dialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.has("Resp")) {
                                Toast.makeText(context, obj.getString("Resp"), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(context, "Credit Add Successfully", Toast.LENGTH_SHORT).show();

                            // here we get the response when all api is ready then we get response here

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoadingDialog();
                        //displaying the error in toast if occurrs
                        try {
                            Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                        }

                        System.out.println(error);
                        dialog.dismiss();
                    }
                });

        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(context));
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }


    @Override
    public int getItemCount() {
        return VuReport.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence keyword) {
            ArrayList<ViewUsersGetSet> filteredData = new ArrayList<>();

            if (keyword.toString().isEmpty()) {
                VuReport = backupList;
            } else {
                for (ViewUsersGetSet obj : backupList) {
                    if (obj.getUserName().toString().toLowerCase().contains(keyword.toString().toLowerCase())
                            || obj.getUserMobile().toString().toLowerCase().contains(keyword.toString().toLowerCase()))
                        filteredData.add(obj);
                }
                VuReport = filteredData;
            }

            FilterResults results = new FilterResults();
            results.values = VuReport;
            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            VuReport = ((List<ViewUsersGetSet>) results.values);
            notifyDataSetChanged();
        }
    };


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView User_id, User_Name, User_Mobile, User_Email, User_Status, User_FirmName, Balance_Amount;
        LinearLayout addBal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            User_id = itemView.findViewById(R.id.vuUserId);
            User_Name = itemView.findViewById(R.id.vuUserName);
            User_Mobile = itemView.findViewById(R.id.vuUserMobile);
            User_Email = itemView.findViewById(R.id.vuUserEmail);
            User_Status = itemView.findViewById(R.id.vuStatus);
//            User_FirmName = itemView.findViewById(R.id.vuFirmName);
            Balance_Amount = itemView.findViewById(R.id.vuBalance);
            addBal = itemView.findViewById(R.id.addBalance);

        }
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

    private String getAmountFormat(String data) {
        BigDecimal bd = new BigDecimal(data).setScale(2, RoundingMode.HALF_UP);
//        double newInput = bd.doubleValue();
        String newInput = String.valueOf(bd.doubleValue());
        return newInput;
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
