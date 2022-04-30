package josemanuel.marin.finalproject.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import josemanuel.marin.finalproject.AddOffer;
import josemanuel.marin.finalproject.R;
import josemanuel.marin.finalproject.WarnMarketActivity;

public class LocationFragment extends Fragment {
    FusedLocationProviderClient fusedLocationProviderClient;
    static EditText location,market;
    static String locationData = "";

    public LocationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);

        location = view.findViewById(R.id.editTextTextPersonName2);
        market = view.findViewById(R.id.editTextTextPersonName);

        //Get location
        fusedLocationProviderClient =  LocationServices.getFusedLocationProviderClient(getContext());

        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location2 = task.getResult();
                    if(location!=null){
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        try {
                            List<Address> addressList = geocoder.getFromLocation(location2.getLatitude(),location2.getLongitude(),1);
                            location.setText(addressList.get(0).getAddressLine(0));

                            locationData = addressList.get(0).getAddressLine(0)+":"+addressList.get(0).getLatitude()+":"+addressList.get(0).getLongitude();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(getContext(),"Location null error",Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }else{
            Intent intent = new Intent(getContext(), WarnMarketActivity.class);
            startActivity(intent);
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }

        return view;
    }

    static public String getLocation(){
        if(location != null) {
            return location.getText().toString();
        }else {
            return "";
        }
    }

    static public String getMarket(){
        if(market != null) {
            return market.getText().toString();
        }else {
            return "";
        }
    }

    static public String getLocationData(){
            return locationData;
    }
}