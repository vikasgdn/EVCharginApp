package com.evcharginapp.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.evcharginapp.AppConstants.AppConstant;
import com.evcharginapp.MainActivity;
import com.evcharginapp.R;
import com.evcharginapp.apppreferences.AppPreference;
import com.evcharginapp.databinding.FragmentAddstationBinding;
import com.evcharginapp.network.INetworkEvent;
import com.evcharginapp.network.NetworkModel;
import com.evcharginapp.network.NetworkService;
import com.evcharginapp.network.NetworkStatus;
import com.evcharginapp.network.NetworkURL;
import com.evcharginapp.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddEVStationFragment extends Fragment implements INetworkEvent, View.OnClickListener {

    private FragmentAddstationBinding binding;
    private List<String> mStateList;
    private List<String> mZipCodeList;
    ArrayAdapter<String>  StateAdapter,ZipAdapter;



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAddstationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mStateList=new ArrayList<>();
        mZipCodeList=new ArrayList<>();
        binding.btnAdd.setOnClickListener(this);
        getStateAPI();
        getZIPAPI();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void addEVStationDataAPI() {
        if (NetworkStatus.isNetworkConnected(getContext())) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("user_id", AppPreference.INSTANCE.getGetUserId());
                jsonObject.put("session_id", AppPreference.INSTANCE.getGetSessionId());
                jsonObject.put("Email", binding.etEmail.getText().toString());
                jsonObject.put("LocationName", binding.etTitle.getText().toString());
                jsonObject.put("Latitude",((MainActivity)getActivity()).mLocationUtils.getLatitude());
                jsonObject.put("Longitude",((MainActivity)getActivity()).mLocationUtils.getLongitude());
                jsonObject.put("Address", binding.etAddress.getText().toString());
                jsonObject.put("Description", binding.etDescription.getText().toString());
                jsonObject.put("Charging_plugs_available", binding.etChargePoints.getText().toString());
                jsonObject.put("State", binding.spState.getSelectedItem().toString());
                jsonObject.put("Zipcode", binding.spZipcode.getSelectedItem().toString());

                NetworkService networkService = new NetworkService(jsonObject, NetworkURL.ADD_EV_URL, AppConstant.METHOD_POST, this, getContext());
                networkService.call(new NetworkModel());
            }
            catch (Exception e) { e.printStackTrace(); }
        } else
            AppUtils.toast(getActivity(), getString(R.string.internet_error));
    }
    private void getStateAPI() {
        if (NetworkStatus.isNetworkConnected(getContext())) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("user_id", AppPreference.INSTANCE.getGetUserId());
                jsonObject.put("session_id", AppPreference.INSTANCE.getGetSessionId());
                NetworkService networkService = new NetworkService(jsonObject, NetworkURL.GET_STATE_URL, AppConstant.METHOD_POST, this, getContext());
                networkService.call(new NetworkModel());
            }
            catch (Exception e) { e.printStackTrace(); }
        } else
            AppUtils.toast(getActivity(), getString(R.string.internet_error));
    }
    private void getZIPAPI() {
        if (NetworkStatus.isNetworkConnected(getContext())) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("user_id", AppPreference.INSTANCE.getGetUserId());
                jsonObject.put("session_id", AppPreference.INSTANCE.getGetSessionId());
                NetworkService networkService = new NetworkService(jsonObject, NetworkURL.GET_ZIP_URL, AppConstant.METHOD_POST, this, getContext());
                networkService.call(new NetworkModel());
            }
            catch (Exception e) { e.printStackTrace(); }
        } else
            AppUtils.toast(getActivity(), getString(R.string.internet_error));
    }

    @Override
    public void onNetworkCallInitiated(String service) {
           binding.pbProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response) {
        Log.e("STATE RESPONSE ",""+response);
        binding.pbProgress.setVisibility(View.GONE);
        if (service.equalsIgnoreCase(NetworkURL.GET_STATE_URL))
        {
            try {
                JSONObject jsonObject=new JSONObject(response);
                if (jsonObject.optInt("status")==200)
                {
                    JSONArray jsonArray= jsonObject.optJSONArray("data");
                    for (int i=0;i<jsonArray.length();i++)
                    {
                        mStateList.add(jsonArray.getJSONObject(i).optString("state"));
                    }
                    mStateList.add(0,"Select State");
                    StateAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mStateList);
                    binding.spState.setAdapter(StateAdapter);
                }
                else
                {
                    AppUtils.toast(getActivity(),jsonObject.optString("msg"));
                }
            }
            catch (Exception e) { e.printStackTrace(); }
        }
        else if(service.equalsIgnoreCase(NetworkURL.GET_ZIP_URL))
        {
            try {
                JSONObject jsonObject=new JSONObject(response);
                if (jsonObject.optInt("status")==200)
                {
                    Log.e("Zip response :",response);
                    JSONArray jsonArray= jsonObject.optJSONArray("data");
                    for (int i=0;i<jsonArray.length();i++)
                    {
                        mZipCodeList.add(jsonArray.getJSONObject(i).optString("zipcode"));
                    }
                    mZipCodeList.add(0,"Select ZipCode");
                    ZipAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mZipCodeList);
                    binding.spZipcode.setAdapter(ZipAdapter);
                }
                else
                {
                    AppUtils.toast(getActivity(),jsonObject.optString("msg"));
                }
            }
            catch (Exception e) { e.printStackTrace(); }

        }
        else
        {
            try {
                JSONObject jsonObject=new JSONObject(response);
                if(jsonObject.optInt("status")==AppConstant.SUCCESS)
                {
                    AppUtils.toast(getActivity(), "Thanks!! Station added successfully");
                    binding.etTitle.setText("");
                    binding.etEmail.setText("");
                    binding.etChargePoints.setText("");
                    binding.etAddress.setText("");
                    binding.etDescription.setText("");
                    binding.spState.setSelection(0);
                    binding.spZipcode.setSelection(0);

                }
                else
                    AppUtils.toast(getActivity(),jsonObject.optString("msg"));
            }
            catch (Exception e){e.printStackTrace();}

        }

    }

    @Override
    public void onNetworkCallError(String service, String errorMessage)
    {
        binding.pbProgress.setVisibility(View.GONE);
        AppUtils.toast(getActivity(),""+errorMessage);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_add:
                if (TextUtils.isEmpty(binding.etTitle.getText().toString()))
                    AppUtils.toast(getActivity(),"Please Enter Location Title");
                else if(TextUtils.isEmpty(binding.etEmail.getText().toString()) || !AppUtils.isValidEmail(binding.etEmail.getText().toString()))
                    AppUtils.toast(getActivity(),"Please Enter Email");
                else if(TextUtils.isEmpty(binding.etChargePoints.getText().toString()))
                    AppUtils.toast(getActivity(),"Please Enter Available Charging Point");
                else if(Integer.parseInt(binding.etChargePoints.getText().toString())<1)
                    AppUtils.toast(getActivity(),"Please Enter minimum one Charging Point");
                else if(TextUtils.isEmpty(binding.etAddress.getText().toString()))
                    AppUtils.toast(getActivity(),"Please Enter Address");
                else if(TextUtils.isEmpty(binding.etDescription.getText().toString()))
                    AppUtils.toast(getActivity(),"Please Enter Description");
                else if(binding.spState.getSelectedItem().toString().equalsIgnoreCase("Select State"))
                    AppUtils.toast(getActivity(),"Please Select State");
                else if(binding.spZipcode.getSelectedItem().toString().equalsIgnoreCase("Select zipCode"))
                    AppUtils.toast(getActivity(),"Please Select ZipCode");
                else {
                    AppUtils.hideKeyboard(getActivity());
                    addEVStationDataAPI();
                }

                break;
        }

    }
}