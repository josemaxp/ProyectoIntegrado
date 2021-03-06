package josemanuel.marin.finalproject.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import josemanuel.marin.finalproject.R;
import josemanuel.marin.finalproject.controller.Connection;
import josemanuel.marin.finalproject.dialogs.IPDialog;

public class Login extends AppCompatActivity implements LocationListener {
    Button loginButton, registerButton, guestButton;
    EditText username, password;
    TextView error;
    ImageView imageViewIP;
    public static Double latitud = 0.0, longitud = 0.0;
    public static String direccion, direccionCompleta, poblacion, comunidadAutonoma, provincia;
    login login;
    guestUser guestUser;
    Connection connection;
    LocationManager locationManager;
    PrintWriter out;
    BufferedReader in;
    SharedPreferences preferences;
    String ip;
    String usernameSaved;
    String passwordSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        username = findViewById(R.id.LoginUsernameText);
        password = findViewById(R.id.LoginPasswordText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButtonLogin);
        guestButton = findViewById(R.id.guestButton);
        error = findViewById(R.id.textViewError);
        imageViewIP = findViewById(R.id.imageViewIP);

        //Get location
        checkLocationPermissions();
        getLocationManager();


        preferences = getSharedPreferences("IP", MODE_PRIVATE);
        ip = preferences.getString("IP", "");

        preferences = getSharedPreferences("USERNAME", MODE_PRIVATE);
        usernameSaved = preferences.getString("USERNAME", "");

        preferences = getSharedPreferences("PASSWORD", MODE_PRIVATE);
        passwordSaved = preferences.getString("PASSWORD", "");

        if (!ip.equals("")) {
            Connection.setIP(ip);
            connection = new Connection();
            connection.start();
        }

        if (!usernameSaved.equals("") && !passwordSaved.equals("")) {
            username.setText(usernameSaved);
            password.setText(passwordSaved);
        }

        System.out.println(ip + username.getText().toString() + password.getText().toString());


        loginButton.setOnClickListener(view -> {

            preferences = getSharedPreferences("IP", MODE_PRIVATE);
            ip = preferences.getString("IP", "");

            boolean result = Connection.state;

            if (result) {
                login = new login();
                try {
                    error.setText(login.execute(username.getText().toString(), password.getText().toString()).get());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                IPDialog dialogo = new IPDialog();
                dialogo.show(getSupportFragmentManager(), "IP");
            }
        });

        guestButton.setOnClickListener(view -> {

            preferences = getSharedPreferences("IP", MODE_PRIVATE);
            ip = preferences.getString("IP", "");

            boolean result = Connection.state;

            if (result) {
                guestUser = new guestUser();
                guestUser.execute();
                Intent intent = new Intent(Login.this, WarnMarketActivity.class);
                startActivity(intent);

            } else {
                IPDialog dialogo = new IPDialog();
                dialogo.show(getSupportFragmentManager(), "IP");
            }
        });

        registerButton.setOnClickListener(view -> {

            preferences = getSharedPreferences("IP", MODE_PRIVATE);
            ip = preferences.getString("IP", "");

            boolean result = Connection.state;

            if (result) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);

            } else {
                IPDialog dialogo = new IPDialog();
                dialogo.show(getSupportFragmentManager(), "IP");
            }
        });

        imageViewIP.setOnClickListener(v -> {
            IPDialog dialogo = new IPDialog();
            dialogo.show(getSupportFragmentManager(), "IP");
        });

    }

    class login extends AsyncTask<String, String, String> {
        Socket s;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            s = Connection.getSocket();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                out = new PrintWriter(s.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));

                if (params[0].equals("") || params[1].equals("")) {
                    result = "Error, los campos no pueden estar vac??os.";
                } else {
                    out.println("CL:login:" + username.getText().toString() + ":" + password.getText().toString());
                    String fromServer = in.readLine();

                    if (fromServer.split(":")[2].equals("true")) {
                        SharedPreferences preferences = getSharedPreferences("USERNAME", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("USERNAME", username.getText().toString());
                        editor.apply();

                        SharedPreferences preferences2 = getSharedPreferences("PASSWORD", MODE_PRIVATE);
                        SharedPreferences.Editor editor2 = preferences2.edit();
                        editor2.putString("PASSWORD", password.getText().toString());
                        editor2.apply();

                        Intent intent = new Intent(Login.this, WarnMarketActivity.class);
                        startActivity(intent);
                    } else {
                        result = "Error, comprueba tu usuario o contrase??a.";
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    class guestUser extends AsyncTask<Void, Void, Void> {
        Socket s;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            s = Connection.getSocket();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                out = new PrintWriter(s.getOutputStream(), true);
                out.println("CL:guestUser");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocationManager() {
        try {
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location loc) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);

            latitud = addresses.get(0).getLatitude();
            longitud = addresses.get(0).getLongitude();
            poblacion = Normalizer.normalize(addresses.get(0).getLocality(), Normalizer.Form.NFD);
            poblacion = poblacion.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
            provincia = Normalizer.normalize(addresses.get(0).getSubAdminArea(), Normalizer.Form.NFD);
            provincia = provincia.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
            comunidadAutonoma = Normalizer.normalize(addresses.get(0).getAdminArea(), Normalizer.Form.NFD);
            comunidadAutonoma = comunidadAutonoma.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
            direccion = addresses.get(0).getAddressLine(0);
            direccionCompleta = addresses.get(0).getAddressLine(0) + "%" + poblacion + "%" + provincia + "%" + comunidadAutonoma;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(Login.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Login.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(Login.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(Login.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(Login.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    this.finishAffinity();
                }
                return;
            }
        }
    }
}