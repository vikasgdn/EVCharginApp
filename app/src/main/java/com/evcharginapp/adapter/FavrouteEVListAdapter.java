package com.evcharginapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.evcharginapp.R;
import com.evcharginapp.model.EVBeans;
import com.evcharginapp.ui.DashboardFragment;

import java.util.List;

public class FavrouteEVListAdapter extends RecyclerView.Adapter<FavrouteEVListAdapter.ProductViewHolder> {

    private Context mContext;
    private List<EVBeans> mEVBeanList;


    public FavrouteEVListAdapter(Context context,List<EVBeans> list) {
        this.mContext = context;
        mEVBeanList=list;
    }



    public ProductViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ProductViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_favroute_evlist, viewGroup, false));


    }

    public void onBindViewHolder(ProductViewHolder productViewHolder, int position)
    {
        EVBeans evBeans=mEVBeanList.get(position);
        productViewHolder.mNameTV.setText(""+evBeans.getmLocationName());
        productViewHolder.mDescTV.setText(""+evBeans.getmDescription());
        productViewHolder.mLocationTV.setText(""+evBeans.getmAddress());

    }

    public int getItemCount() {
        return mEVBeanList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mNameTV;
        private TextView mLocationTV;
        private TextView mDescTV;
        private ImageView mFavrouteIV;

        public ProductViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            initView(view);
        }

        private void initView(View view) {
            this.mNameTV = (TextView) view.findViewById(R.id.tv_title);
            this.mLocationTV = (TextView) view.findViewById(R.id.tv_location);
            this.mDescTV = (TextView) view.findViewById(R.id.tv_dec);
        }

        public void onClick(View view) {

        }
    }
}
