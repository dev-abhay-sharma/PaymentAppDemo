package com.swinfotech.swpay.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.activities.AllReports;
import com.swinfotech.swpay.activities.Telecom;
import com.swinfotech.swpay.databinding.AllReportsLytBinding;
import com.swinfotech.swpay.fragment.HomeFragment;
import com.swinfotech.swpay.model.HomeModel;
import com.swinfotech.swpay.model.OPCommissionGETSET;
import com.ezypayaeps.AepsNewActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllReportsAdapter extends RecyclerView.Adapter<AllReportsAdapter.ViewHolder> {
    private ArrayList<HomeModel> homeModelArrayList;
    private Activity context;
    private HomeFragment homeFragment;
    private Session session;

    OnClickListenerAeps listener;


    public AllReportsAdapter(ArrayList<HomeModel> homeModelArrayList, Activity context, HomeFragment homeFragment, OnClickListenerAeps listener) {
        this.homeModelArrayList = homeModelArrayList;
        this.context = context;
        this.homeFragment = homeFragment;
        this.session = new Session(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // binding in recyclerview
        // return binding object to itemview
        return new ViewHolder(AllReportsLytBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // set the name and image

        holder.homeLayoutBinding.titleText.setText(homeModelArrayList.get(position).getName());

        holder.homeLayoutBinding.imageView.setImageDrawable(ContextCompat.getDrawable(context, homeModelArrayList.get(position).getDrawableId()));

        holder.itemView.setOnClickListener(v -> {
            // check if class is not null
            if (homeModelArrayList.get(position).getAclass() != null) {

                // for opening Telecom activity with specific intent value

                if (homeModelArrayList.get(position).getAclass() == Telecom.class) {
                    // match if activity is telecom
                    Intent intent = new Intent(context, homeModelArrayList.get(position).getAclass());
                    // then check what is the title of clicked item
                    if (homeModelArrayList.get(position).getName().equalsIgnoreCase("Mobile")) {
                        Constant.RECHARGE_AMOUNT = "";
                        intent.putExtra("fragment", "Mobile");
                    }

                    if (homeModelArrayList.get(position).getName().equalsIgnoreCase("DTH")) {
                        intent.putExtra("fragment", "DTH");
                    }

                    if (homeModelArrayList.get(position).getName().equalsIgnoreCase("Postpaid")) {
                        intent.putExtra("fragment", "Postpaid");
                    }

                    context.startActivity(intent);
                    // return the method here so it can't reach further code
                    return;
                }

                // else call the class
                context.startActivity(new Intent(context, homeModelArrayList.get(position).getAclass()));
            } else {
                // we pass null class from home fragment
                // here we check if the title is payment request then open dialog in home fragment
                if (homeModelArrayList.get(position).getName().equalsIgnoreCase("Payment Request")) {
                    if (homeFragment != null) {
                        homeFragment.showPaymentDialog();
                    }

                    return;
                }
                if (homeModelArrayList.get(position).getName().equalsIgnoreCase("Search Transaction")) {

                    if (context instanceof AllReports) {
                        ((AllReports) context).openTranscationReport();
                    }

                    return;
                }

                if (homeModelArrayList.get(position).getName().equalsIgnoreCase("Transaction Report")) {

//                    if (context instanceof AllReports) {
//                        ((AllReports) context).openStatementReport();
//                    }

                    return;
                }

                if (homeModelArrayList.get(position).getName().equalsIgnoreCase("Commission Report")) {
                    showCommisionDialog();

                    return;
                }

                // coming soon services here the dialog appear to show coming soon services

                if (homeModelArrayList.get(position).getName().equalsIgnoreCase("Gas")) {
//                    showCommisionDialog();
                    showComingDialog();
                    return;
                }

                if (homeModelArrayList.get(position).getName().equalsIgnoreCase("AEPS")) {
//                    showCommisionDialog();
//                    showComingServiceDialog();
//                    showComingDialog();

//                    listener.onContainerClick(position);


                    Intent intent = new Intent(context, AepsNewActivity.class);
                    intent.putExtra("picture", R.drawable.swpay_removebg);
                    intent.putExtra("cmp_name", "S W Pay");
                    intent.putExtra("Merchantid", session.getString(Session.AEPS_MERCHANT_ID));
                    intent.putExtra("Retailerid", session.getString(Session.USER_ID_TWO));
                    intent.putExtra("Authcode", "65c43ccd6a7b41c884");
                    intent.putExtra("Mpin", "7170");
                    context.startActivityForResult(intent, 111);


                    return;
                }

                if (homeModelArrayList.get(position).getName().equalsIgnoreCase("Electricity")) {
//                    showCommisionDialog();
                    showComingDialog();
                    return;
                }

                if (homeModelArrayList.get(position).getName().equalsIgnoreCase("FasTag")) {
//                    showCommisionDialog();
                    showComingDialog();
                    return;
                }

                if (homeModelArrayList.get(position).getName().equalsIgnoreCase("Water")) {
//                    showCommisionDialog();
                    showComingDialog();
                    return;
                }

                if (homeModelArrayList.get(position).getName().equalsIgnoreCase("Money")) {
//                    showCommisionDialog();
                    showComingDialog();
                    return;
                }

                if (homeModelArrayList.get(position).getName().equalsIgnoreCase("Insurance")) {
//                    showCommisionDialog();
                    showComingDialog();
                    return;
                }


                Toast.makeText(context, "Please Add a class for this click into homeFragment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showComingDialog() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.coming_soon_service);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        Button btn;
        btn = dialog.findViewById(R.id.comingBtn);

        btn.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.show();

    }


    private void showComingServiceDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Service Coming Soon....");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

//        builder1.setNegativeButton(
//                "No",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void showCommisionDialog() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_operator_commission);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.98f);

        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        ImageView close;
        RecyclerView recyclerView;
        List<OPCommissionGETSET> commission;
        OPCommissionAdapter opCommissionAdapter;
        androidx.appcompat.widget.SearchView search_view;

        recyclerView = dialog.findViewById(R.id.commissionList);
        close = dialog.findViewById(R.id.close);
        search_view = dialog.findViewById(R.id.search_view_commission);
        commission = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        opCommissionAdapter = new OPCommissionAdapter(context, commission);
        recyclerView.setAdapter(opCommissionAdapter);


        search_view.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                opCommissionAdapter.getFilter().filter(s);
                return false;
            }
        });

        // pass our views for access later
        extractAPI(opCommissionAdapter, commission);

        if (close != null) {
            close.setOnClickListener(v -> {
                dialog.dismiss();
            });
        }

        dialog.show();
    }

    private void extractAPI(OPCommissionAdapter opCommissionAdapter, List<OPCommissionGETSET> commission) {
        String JSON_URL = "https://mobile.swpay.in/api/AndroidAPI/Commisions?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD) + "&tok=" + session.getString(Session.TOKEN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e("TAG", "onResponse: " + response);
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("Resp")) {
                                Toast.makeText(context, jsonObject.getString("Resp"), Toast.LENGTH_SHORT).show();
                            }
                            //old
//                            JSONObject obj = jsonObject.getJSONObject("Commision");
//                            JSONArray jsonArray = obj.getJSONArray("data");
                            // new
                            JSONArray jsonArray = jsonObject.getJSONArray("Commision");

                            if (jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String op = object.getString("operator_name");
                                    String comPer = object.getString("comm_per");
                                    String comVal = object.getString("comm_val");
                                    String serChPer = object.getString("service_charge_per");
                                    String serChVal = object.getString("service_charge_val");
                                    String image = object.getString("image");
                                    commission.add(new OPCommissionGETSET(op, comPer, comVal, serChPer, serChVal, image));
                                }
                                opCommissionAdapter.notifyDataSetChanged();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", "onErrorResponse: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                String user = "9876543210";
                String pass = "983fc53b5";
                params.put("username", user);
                params.put("password", pass);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }

    @Override
    public int getItemCount() {
        return homeModelArrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // make binding object
        private AllReportsLytBinding homeLayoutBinding;
        OnClickListenerAeps listener;

        public ViewHolder(@NonNull AllReportsLytBinding itemView) {
            // asign binding
            super(itemView.getRoot());
            this.homeLayoutBinding = itemView;
            this.listener = listener;

        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MyAdapter", "onActivityResult");
    }

    public interface OnClickListenerAeps {
        void onContainerClick(int position);
    }

}
