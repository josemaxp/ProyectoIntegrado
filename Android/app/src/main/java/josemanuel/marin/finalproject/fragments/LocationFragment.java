package josemanuel.marin.finalproject.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
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
import java.util.concurrent.ExecutionException;

import josemanuel.marin.finalproject.Login;
import josemanuel.marin.finalproject.R;
import josemanuel.marin.finalproject.WarnMarketActivity;
import josemanuel.marin.finalproject.controller.Connection;

public class LocationFragment extends Fragment {
    EditText location;
    AutoCompleteTextView market;
    String locationData;
    Button buttonAddOffer;
    TextView textViewAddError;
    addOffer addOffer;
    getMarkets getMarkets;
    getUser getUser;
    PrintWriter out = null;
    BufferedReader in = null;
    uploadImage uploadImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);

        location = view.findViewById(R.id.editTextAddLocation);
        market = view.findViewById(R.id.editTextAddMarket);
        buttonAddOffer = view.findViewById(R.id.buttonAddOffer);
        textViewAddError = view.findViewById(R.id.textViewAddError);

        location.setText(Login.direccion);

        //Add offer
        buttonAddOffer.setOnClickListener(v -> {
            List<String> tagsList = TagsFragment.getTagsList();
            String tags = "";

            int contadorTags = 0;

            for (int i = 0; i < tagsList.size(); i++) {
                if (!tagsList.get(i).equals("-")) {
                    contadorTags++;
                }
            }

            if (contadorTags < 3) {
                textViewAddError.setText(R.string.error_minimun_tags);
            } else if (PriceFragment.getPrice().equals("") || PriceFragment.getPriceUnity().equals("")) {
                textViewAddError.setText(R.string.error_empty_price);
            } else if (PriceFragment.getUnity().equals("-")) {
                textViewAddError.setText(R.string.error_empty_unity);
            } else if (ImageFragment.getBitmap() == null) {
                textViewAddError.setText(R.string.error_empty_image);
            } else if (getLocation().equals("")) {
                textViewAddError.setText(R.string.error_empty_location);
            } else if (getMarket().equals("")) {
                textViewAddError.setText(R.string.error_empty_market);
            } else {

                for (int i = 0; i < tagsList.size(); i++) {
                    tags += tagsList.get(i).trim() + ",";
                }
                TagsFragment.restartTagsList();

                textViewAddError.setText("");
                addOffer = new addOffer();
                addOffer.execute(tags, PriceFragment.getPrice(), PriceFragment.getPriceUnity(),
                        PriceFragment.getUnity(), ImageFragment.getBitmap().toString(), getMarket(), getLocationData());

                transformImage();
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
        locationData = Login.direccion + ":" + Login.latitud + ":" + Login.longitud;
        return locationData;
    }

    private void transformImage() {
        getUser = new getUser();
        String[] username;
        try {
            username = getUser.execute().get().split(":");
            //LocalDateTime
            File f = new File(getContext().getCacheDir(), "D" + LocalDate.now() + "%H" + LocalDateTime.now().getHour() + "%M" + LocalDateTime.now().getMinute() + "%S" + LocalDateTime.now().getSecond() + "%" + username[2]);
            try {
                f.createNewFile();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageFragment.getBitmap().compress(Bitmap.CompressFormat.PNG, 0, bos);
                byte[] bitmapdata = bos.toByteArray();

                FileOutputStream fos = new FileOutputStream(f);
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
            OutputStream os = null;
            try {

                byte[] filebyte = new byte[(int) params[0].length()];
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(params[0]));
                os = s.getOutputStream();
                int numeroBytesLeidos = 0;
                while (numeroBytesLeidos != filebyte.length) {
                    numeroBytesLeidos += bis.read(filebyte, 0, filebyte.length);
                    os.write(filebyte, 0, filebyte.length);
                }
                os.flush();
                in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(File result) {
            Intent intent = new Intent(getContext(), WarnMarketActivity.class);
            startActivity(intent);
            Toast.makeText(getContext(), "Oferta creada", Toast.LENGTH_LONG).show();
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