package com.swinfotech.swpay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.swinfotech.swpay.databinding.AepsViewBeneficiaryListBinding;
import com.swinfotech.swpay.model.AEPSViewBeneficiaryGetSet;

import java.util.List;

public class AEPSViewBeneficiaryAdapter extends RecyclerView.Adapter<AEPSViewBeneficiaryAdapter.ViewHolder> {

    LayoutInflater inflater;
    List<AEPSViewBeneficiaryGetSet> viewList;
    Context context;

    public AEPSViewBeneficiaryAdapter(Context context, List<AEPSViewBeneficiaryGetSet> viewList) {
        this.inflater = LayoutInflater.from(context);
        this.viewList = viewList;
        this.context = context;
    }

    @NonNull
    @Override
    public AEPSViewBeneficiaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AEPSViewBeneficiaryAdapter.ViewHolder(AepsViewBeneficiaryListBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AEPSViewBeneficiaryAdapter.ViewHolder holder, int position) {

        AEPSViewBeneficiaryGetSet data = viewList.get(position);
        holder.binding.listName.setText(data.getBenef_name());
        holder.binding.listMobNo.setText(data.getContacts());
        holder.binding.listBankName.setText(data.getBank_name());
        holder.binding.listIfsc.setText(data.getBank_ifsc());
        holder.binding.listAccountNo.setText(data.getAccount_no());

    }

    @Override
    public int getItemCount() {
        return viewList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private AepsViewBeneficiaryListBinding binding;

        public ViewHolder(@NonNull AepsViewBeneficiaryListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

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

}
