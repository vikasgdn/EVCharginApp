package com.evcharginapp.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.evcharginapp.AppConstants.AppConstant;
import com.evcharginapp.R;
import com.evcharginapp.adapter.FavrouteEVListAdapter;
import com.evcharginapp.apppreferences.AppPreference;
import com.evcharginapp.databinding.FragmentFavrouteBinding;
import com.evcharginapp.model.EVBeans;
import com.evcharginapp.network.INetworkEvent;
import com.evcharginapp.network.NetworkModel;
import com.evcharginapp.network.NetworkService;
import com.evcharginapp.network.NetworkStatus;
import com.evcharginapp.network.NetworkURL;
import com.evcharginapp.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FavrouteFragment extends Fragment implements INetworkEvent, View.OnClickListener {

    private FavrouteEVListAdapter evListAdapter;
    private List<EVBeans> mEVBeanList;
    private FragmentFavrouteBinding binding;
    private int mPosition=0;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFavrouteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mEVBeanList=new ArrayList<>();
        evListAdapter=new FavrouteEVListAdapter(getContext(),mEVBeanList,this);
        binding.rvEvlist.setAdapter(evListAdapter);
        getFavEVlistAPI();

    }


    private void getFavEVlistAPI()
    {
        if (NetworkStatus.isNetworkConnected(getActivity())) {
            try {
                JSONObject jsonObject = new JSONObject();

                //  jsonObject.put("user_id", AppPreference.INSTANCE.getGetUserId());
                jsonObject.put("uid", AppPreference.INSTANCE.getGetUId());
                jsonObject.put("session_id", AppPreference.INSTANCE.getGetSessionId());

                NetworkService networkService = new NetworkService(jsonObject, NetworkURL.GET_FAVEV_URL, AppConstant.METHOD_POST, this, getContext());
                networkService.call(new NetworkModel());
            }
            catch (Exception e) { e.printStackTrace(); }
        } else
            AppUtils.toast(getActivity(), getString(R.string.internet_error));
    }
    private void saveFavroteEVAPI(int stationId)
    {
        if (NetworkStatus.isNetworkConnected(getActivity())) {
            try {

                JSONObject jsonObject = new JSONObject();
                // jsonObject.put("user_id", AppPreference.INSTANCE.getGetUserId());
                jsonObject.put("uid", AppPreference.INSTANCE.getGetUId());
                jsonObject.put("session_id", AppPreference.INSTANCE.getGetSessionId());
                jsonObject.put("stationid",""+stationId);
                jsonObject.put("favourite","0");

                NetworkService networkService = new NetworkService(jsonObject, NetworkURL.ADD_FAVEV_URL, AppConstant.METHOD_POST, this, getContext());
                networkService.call(new NetworkModel());
            }
            catch (Exception e) { e.printStackTrace(); }
        } else
            AppUtils.toast(getActivity(), getString(R.string.internet_error));
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onNetworkCallInitiated(String service) {
        binding.pbProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response) {
        Log.e("RESPONSE FavL SUCCESS","=== > "+response);
        binding.pbProgress.setVisibility(View.GONE);
        if (service.equalsIgnoreCase(NetworkURL.GET_FAVEV_URL)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optInt("status") == 200) {
                    if (jsonObject.optJSONArray("data") != null && jsonObject.optJSONArray("data").length() > 0) {
                        JSONArray dataArray = jsonObject.optJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject jsonObject1 = dataArray.getJSONObject(i);
                            EVBeans evBeans = new EVBeans();
                            evBeans.setId(jsonObject1.optInt("id"));
                            evBeans.setmDescription(jsonObject1.optString("description"));
                            evBeans.setmLocationName(jsonObject1.optString("location_name"));
                            evBeans.setmAddress(jsonObject1.optString("address"));

                            mEVBeanList.add(evBeans);
                        }
                        evListAdapter.notifyDataSetChanged();
                    } else {
                        binding.tvNodatafound.setVisibility(View.VISIBLE);
                    }
                } else {
                    AppUtils.toast(getActivity(), "" + jsonObject.optString("msg"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            Log.e("RESPONSE FavL SUCCESS","=== > "+response);
            binding.pbProgress.setVisibility(View.GONE);
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optInt("status") == 200)
                {
                    mEVBeanList.remove(mPosition);
                    evListAdapter.notifyDataSetChanged();

                    // AppUtils.toast(getActivity(), "" + jsonObject.optString("msg"));
                }
                else {
                    AppUtils.toast(getActivity(), "" + jsonObject.optString("msg"));
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }


    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        binding.pbProgress.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.iv_fav:
                mPosition= Integer.parseInt(view.getTag().toString());
                EVBeans evBeans=mEVBeanList.get(mPosition);
                saveFavroteEVAPI(evBeans.getId());
                break;
        }
    }
}