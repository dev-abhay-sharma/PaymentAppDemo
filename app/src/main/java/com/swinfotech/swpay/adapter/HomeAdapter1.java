package com.swinfotech.swpay.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
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
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.databinding.HomeLayoutBinding;
import com.swinfotech.swpay.fragment.HomeFragment;
import com.swinfotech.swpay.model.HomeModel;
import com.swinfotech.swpay.model.OPCommissionGETSET;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeAdapter1 extends RecyclerView.Adapter<HomeAdapter1.ViewHolder> {

    private ArrayList<HomeModel> homeModelArrayList;
    private Context context;
    private HomeFragment homeFragment;
    private Session session;

    public HomeAdapter1(ArrayList<HomeModel> homeModelArrayList, Context context, HomeFragment homeFragment) {
        this.homeModelArrayList = homeModelArrayList;
        this.context = context;
        this.homeFragment = homeFragment;
        this.session = new Session(context);
    }

    @NonNull
    @Override
    public HomeAdapter1.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeAdapter1.ViewHolder(HomeLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter1.ViewHolder holder, int position) {
        holder.homeLayoutBinding.titleText.setText(homeModelArrayList.get(position).getName());

        holder.homeLayoutBinding.imageView.setImageDrawable(ContextCompat.getDrawable(context, homeModelArrayList.get(position).getDrawableId()));

        holder.itemView.setOnClickListener(v -> {
//            context.startActivity(new Intent(context, homeModelArrayList.get(position).getAclass()));

            if (homeModelArrayList.get(position).getAclass() != null) {
                context.startActivity(new Intent(context, homeModelArrayList.get(position).getAclass()));
            } else {
                if (homeModelArrayList.get(position).getName().equalsIgnoreCase("Commission Report")) {
                    showCommisionDialog();
//                    showComingDialog();
                    return;
                }

                if (homeModelArrayList.get(position).getName().equalsIgnoreCase("UPI")) {
                    showComingDialog();
                    return;
                }
                Toast.makeText(context, "You are not authorized for this service.", Toast.LENGTH_SHORT).show();
            }


        });

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
//        SearchView search_view;
        androidx.appcompat.widget.SearchView search_view;

        recyclerView = dialog.findViewById(R.id.commissionList);
        close = dialog.findViewById(R.id.close);
//        search_view = dialog.findViewById(R.id.search_view);
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

    private void showComingDialog(){
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

    @Override
    public int getItemCount() {
        return homeModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // make binding object
        private HomeLayoutBinding homeLayoutBinding;
        public ViewHolder(@NonNull HomeLayoutBinding itemView) {
            // asign binding
            super(itemView.getRoot());
            this.homeLayoutBinding = itemView;
        }
    }
}
