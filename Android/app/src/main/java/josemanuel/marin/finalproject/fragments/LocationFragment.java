package josemanuel.marin.finalproject.fragments;

import static android.content.Context.LOCATION_SERVICE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import josemanuel.marin.finalproject.R;
import josemanuel.marin.finalproject.WarnMarketActivity;
import josemanuel.marin.finalproject.controller.Connection;

public class LocationFragment extends Fragment implements LocationListener {
    EditText location;
    AutoCompleteTextView market;
    String locationData = "";
    double latitud, longitud;
    Button buttonAddOffer2;
    TextView textViewAddError2;
    LocationManager locationManager;
    addOffer addOffer;
    getMarkets getMarkets;
    getUser getUser;
    PrintWriter out = null;
    BufferedReader in = null;
    uploadImage uploadImage;

    public LocationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);

        location = view.findViewById(R.id.editTextAddLocation);
        market = view.findViewById(R.id.editTextAddMarket);
        buttonAddOffer2 = view.findViewById(R.id.buttonAddOffer);
        textViewAddError2 = view.findViewById(R.id.textViewAddError);

        //Get location
        getLocationManager();

        //Add offer button
        buttonAddOffer2.setOnClickListener(v -> {
            List<String> tagsList = TagsFragment.getTagsList();
            String tags = "";

            int contadorTags = 0;

            for (int i = 0; i < tagsList.size(); i++) {
                if (!tagsList.get(i).equals("-")) {
                    contadorTags++;
                }
            }

            if (contadorTags < 3) {
                textViewAddError2.setText(R.string.error_minimun_tags);
            } else if (PriceFragment.getPrice().equals("") || PriceFragment.getPriceUnity().equals("")) {
                textViewAddError2.setText(R.string.error_empty_price);
            } else if (PriceFragment.getUnity().equals("-")) {
                textViewAddError2.setText(R.string.error_empty_unity);
            } else if (ImageFragment.getBitmap() == null) {
                textViewAddError2.setText(R.string.error_empty_image);
            } else if (getLocation().equals("")) {
                textViewAddError2.setText(R.string.error_empty_location);
            } else if (getMarket().equals("")) {
                textViewAddError2.setText(R.string.error_empty_market);
            } else {

                for (int i = 0; i < tagsList.size(); i++) {
                    tags += tagsList.get(i).trim() + ",";
                }
                TagsFragment.restartTagsList();

                textViewAddError2.setText("");
                addOffer = new addOffer();
                addOffer.execute(tags, PriceFragment.getPrice(), PriceFragment.getPriceUnity(),
                        PriceFragment.getUnity(), ImageFragment.getBitmap().toString(), getMarket(), getLocationData());

                transformImage();

                Intent intent = new Intent(getContext(), WarnMarketActivity.class);
                startActivity(intent);
                Toast.makeText(getContext(), "Offer added", Toast.LENGTH_LONG).show();
            }
        });

        //Instantiate asynctask
        if (getMarkets == null) {
            getMarkets = new getMarkets();
        }

        //Get markets
        try {
            String[] marketListServer = getMarkets.execute().get().split(":");

            List<String> marketList = new ArrayList();
            for (int i = 2; i < marketListServer.length; i++) {
                marketList.add(marketListServer[i]);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, marketList);
            market.setAdapter(adapter);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return view;
    }

    @SuppressLint("MissingPermission")
    private void getLocationManager() {
        try {
            locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLocation() {
        if (location != null) {
            return location.getText().toString();
        } else {
            return "";
        }
    }

    public String getMarket() {
        if (market != null) {
            return market.getText().toString();
        } else {
            return "";
        }
    }

    public String getLocationData() {
        return locationData;
    }

    private void transformImage() {
        getUser = new getUser();
        String[] username;
        try {
            username = getUser.execute().get().split(":");
            File f = new File(getContext().getCacheDir(), LocalDate.now() + "_" + username[2]);
            try {
                f.createNewFile();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageFragment.getBitmap().compress(Bitmap.CompressFormat.PNG, 0, bos);
                byte[] bitmapdata = bos.toByteArray();

                FileOutputStream fos = null;

                fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();

                uploadImage = new uploadImage();
                uploadImage.execute(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onLocationChanged(@NonNull Location loc) {
        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);

            location.setText(address);

            locationData = addresses.get(0).getAddressLine(0) + ":" + addresses.get(0).getLatitude() + ":" + addresses.get(0).getLongitude();
            latitud = addresses.get(0).getLatitude();
            longitud = addresses.get(0).getLongitude();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class addOffer extends AsyncTask<String, Void, String> {
        Socket s;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            s = Connection.getSocket();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                out = new PrintWriter(s.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            out.println("CL:" + "AddOffer:" + params[0] + ":" + params[1] + ":" + params[2] + ":" + params[3] + ":" + params[4] + ":" + params[5] + ":" + params[6]);

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    class uploadImage extends AsyncTask<File, Void, File> {
        Socket s;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            s = Connection.getSocket();
        }

        @Override
        protected File doInBackground(File... params) {
            out.println("CL:img:" + params[0].getName() + ":" + params[0].length());
            try {

                byte[] filebyte = new byte[(int) params[0].length()];
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(params[0]));
                OutputStream os = s.getOutputStream();
                bis.read(filebyte, 0, filebyte.length);
                os.write(filebyte, 0, filebyte.length);
                System.out.println(in.readLine());
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(File result) {
            super.onPostExecute(result);
        }
    }

    class getMarkets extends AsyncTask<Void, Void, String> {
        Socket s;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            s = Connection.getSocket();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                out = new PrintWriter(s.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            out.println("CL:" + "Markets");
            String markets = "";
            try {
                markets = in.readLine();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return markets;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    class getUser extends AsyncTask<Void, Void, String> {
        Socket s;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            s = Connection.getSocket();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                out = new PrintWriter(s.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            String user = "";
            out.println("CL:getUser");
            try {
                user = in.readLine();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return user;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}