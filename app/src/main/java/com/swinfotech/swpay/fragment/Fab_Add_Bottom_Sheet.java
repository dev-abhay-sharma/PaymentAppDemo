package com.swinfotech.swpay.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.swinfotech.swpay.R;
import com.swinfotech.swpay.activities.SearchByNumber;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class Fab_Add_Bottom_Sheet extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fab_Add_Bottom_Sheet() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Fab_Add_Bottom_Sheet newInstance(String param1, String param2) {
        Fab_Add_Bottom_Sheet fragment = new Fab_Add_Bottom_Sheet();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fab__add__bottom__sheet, container, false);

        ImageView imageView = (ImageView) v.findViewById(R.id.b_s_img);
        TextView search = (TextView) v.findViewById(R.id.search_by_num);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchByNumber.class));
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchByNumber.class));
            }
        });

        return v;
    }
}