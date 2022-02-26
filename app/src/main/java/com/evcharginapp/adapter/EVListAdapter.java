package com.evcharginapp.adapter;

import android.content.Context;
import android.util.Log;
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

public class EVListAdapter extends RecyclerView.Adapter<EVListAdapter.ProductViewHolder> {
    private Context mContext;
    private List<EVBeans> mEVBeanList;
    private DashboardFragment dashboardFragment;

    public EVListAdapter(DashboardFragment dashboardFrag,Context context, List<EVBeans> list) {
        this.mContext = context;
        this.mEVBeanList=list;
        dashboardFragment=dashboardFrag;
    }

    public ProductViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ProductViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_evlist, viewGroup, false));
    }

    public void onBindViewHolder(ProductViewHolder productViewHolder, int position)
    {
          EVBeans evBeans=mEVBeanList.get(position);
          productViewHolder.mNameTV.setText(""+evBeans.getmLocationName());
          productViewHolder.mDescTV.setText(""+evBeans.getmDescription());
          productViewHolder.mLocationTV.setText(""+evBeans.getmAddress());

          if (evBeans.getmFavStation()==1)
              productViewHolder.mFavrouteIV.setImageResource(R.drawable.ic_baseline_favorite_red);
          else
              productViewHolder.mFavrouteIV.setImageResource(R.drawable.ic_baseline_favorite_gray);


          productViewHolder.mFavrouteIV.setTag(""+position);
          productViewHolder.mFavrouteIV.setOnClickListener(dashboardFragment);



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
            mFavrouteIV=view.findViewById(R.id.iv_fav);
        }

        public void onClick(View view) {

        }
    }
}
