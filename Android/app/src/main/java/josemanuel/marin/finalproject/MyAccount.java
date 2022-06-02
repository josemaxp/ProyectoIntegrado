package josemanuel.marin.finalproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

import josemanuel.marin.finalproject.controller.Connection;

public class MyAccount extends AppCompatActivity {
    EditText username, email, password, repeatPassword;
    TextView error, points;
    Button update, myOffers, redeem, myRecipes;
    PrintWriter out;
    BufferedReader in;
    updateUser updateUser;
    initData initData;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_account);

        email = findViewById(R.id.editTextEmail);
        username = findViewById(R.id.editTextUsername);
        password = findViewById(R.id.editTextPassword);
        repeatPassword = findViewById(R.id.editTextPasswordRepeat);
        points = findViewById(R.id.textViewPoints);
        error = findViewById(R.id.textViewErrorUpdate);
        update = findViewById(R.id.buttonUpdate);
        myOffers = findViewById(R.id.buttonMyOffers);
        myRecipes = findViewById(R.id.buttonMyRecipes);
        redeem = findViewById(R.id.buttonRedeem);

        initData = new initData();
        try {
            String[] userInfo = initData.execute().get().split(":");

            if(userInfo.length > 2) {
                username.setText(userInfo[2]);
                email.setText(userInfo[3]);
                points.setText("Puntos: " + userInfo[4]);
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        update.setOnClickListener(v -> {
            if (email.getText().toString().equals("") || username.getText().toString().equals("")) {
                error.setText("Los campos de usuario o email no pueden estar vacíos.");
            } else {
                if (repeatPassword.getText().toString().equals(password.getText().toString())) {
                    updateUser = new updateUser();
                    try {
                        String result = updateUser.execute(username.getText().toString(), password.getText().toString(), email.getText().toString()).get();

                        error.setText(result);
                        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }


                } else {
                    error.setText("Las contraseñas no coinciden.");
                }
            }
        });

        myOffers.setOnClickListener(v -> {
            Intent intent = new Intent(MyAccount.this, MyOffers.class);
            startActivity(intent);
        });

        myRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(MyAccount.this, MyRecipes.class);
            startActivity(intent);
        });

        redeem.setOnClickListener(v -> {
            Intent intent = new Intent(MyAccount.this, RedeemPoints.class);
            startActivity(intent);
        });
    }

    class initData extends AsyncTask<String, Void, String> {
        Socket s;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            s = Connection.getSocket();
        }

        @Override
        protected String doInBackground(String... params) {
            String fromServer = "";

            try {
                out = new PrintWriter(s.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));

                out.println("CL:userInfo");
                fromServer = in.readLine();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return fromServer;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    class updateUser extends AsyncTask<String, Void, String> {
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

                out.println("CL:" + "updateUser:" + params[0] + ":" + params[1] + ":" + params[2]);
                String fromServer = in.readLine();

                if (fromServer.split(":")[2].equals("true")) {
                    result = "Usuario actualizado correctamete.";
                } else {
                    result = "Error al actualizar el usuario.";
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            initData = new initData();
            try {
                String[] userInfo = initData.execute().get().split(":");
                username.setText(userInfo[2]);
                email.setText(userInfo[3]);
                points.setText("Puntos: " + userInfo[4]);

                password.setText("");
                repeatPassword.setText("");
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}