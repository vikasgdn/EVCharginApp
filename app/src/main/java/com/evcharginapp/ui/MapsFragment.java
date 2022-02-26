package com.evcharginapp.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evcharginapp.AppConstants.AppConstant;
import com.evcharginapp.MainActivity;
import com.evcharginapp.R;
import com.evcharginapp.apppreferences.AppPreference;
import com.evcharginapp.databinding.FragmentFavrouteBinding;
import com.evcharginapp.databinding.FragmentMapsBinding;
import com.evcharginapp.model.EVBeans;
import com.evcharginapp.network.INetworkEvent;
import com.evcharginapp.network.NetworkModel;
import com.evcharginapp.network.NetworkService;
import com.evcharginapp.network.NetworkStatus;
import com.evcharginapp.network.NetworkURL;
import com.evcharginapp.utils.AppUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsFragment extends Fragment implements INetworkEvent {

    private FragmentMapsBinding binding;
    private GoogleMap mGoogleMap;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap=googleMap;
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            getEVLISTAPI();

        }
    };
    private void getEVLISTAPI()
    {
        if (NetworkStatus.isNetworkConnected(getActivity())) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("user_id", AppPreference.INSTANCE.getGetUserId());
                jsonObject.put("session_id", AppPreference.INSTANCE.getGetSessionId());
               // jsonObject.put("latitude","25.4422");
                //jsonObject.put("longitude","35.1144");

                  jsonObject.put("latitude",""+((MainActivity)getActivity()).mLocationUtils.getLatitude());
                jsonObject.put("longitude", ""+((MainActivity)getActivity()).mLocationUtils.getLongitude());
                NetworkService networkService = new NetworkService(jsonObject, NetworkURL.GET_EVLIST_URL, AppConstant.METHOD_POST, this, getContext());
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

        if (service.equalsIgnoreCase(NetworkURL.GET_EVLIST_URL))
        {
            Log.e("RESPONSE EvL SUCCESS","=== > "+response);
            binding.pbProgress.setVisibility(View.GONE);
            try {
                binding.pbProgress.setVisibility(View.GONE);
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optInt("status")==200)
                {
                    if (jsonObject.optJSONArray("data")!=null && jsonObject.optJSONArray("data").length()>0)
                    {
                        JSONArray dataArray = jsonObject.optJSONArray("data");
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker);

                        for (int i=0;i<dataArray.length();i++)
                        {
                            JSONObject jsonObject1=dataArray.getJSONObject(i);
                            LatLng latLng = new LatLng(jsonObject1.optDouble("latitude"),jsonObject1.optDouble("longitude"));
                            MarkerOptions markerOpt = new MarkerOptions();
                            MarkerOptions title = markerOpt.position(latLng).title(jsonObject1.optString("location_name"));
                            title.snippet("");
                            title.icon(icon);
                            this.mGoogleMap.addMarker(markerOpt);
                            this.mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 6.0f));
                        }
                     //   double lat=((MainActivity)getActivity()).mLocationUtils.getLatitude();
                       // double longt= ((MainActivity)getActivity()).mLocationUtils.getLongitude();
                        //this.mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,longt), 10.0f));
                    }
                }
                else
                {
                    AppUtils.toast(getActivity(),""+jsonObject.optString("msg"));
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        binding.pbProgress.setVisibility(View.GONE);
        AppUtils.toast(getActivity(),"Opps Something went wrong..");
    }
}