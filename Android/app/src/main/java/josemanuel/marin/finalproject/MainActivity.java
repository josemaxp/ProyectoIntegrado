package josemanuel.marin.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    Button loginButton;
    Button registerButton;
    EditText username;
    EditText password;
    TextView error;
    MyTask mt;
    static PrintWriter out = null;
    static BufferedReader in = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mt == null) {
            mt = new MyTask();
            mt.execute();
        }


        username = findViewById(R.id.LoginUsernameText);
        password = findViewById(R.id.LoginPasswordText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        error = findViewById(R.id.textError);

        loginButton.setOnClickListener(view -> {
            try {
                if (username.getText().toString().equals("") || password.getText().toString().equals("")) {
                    error.setText(R.string.errorEmptyFields);
                } else {
                    out.println("CL:" + "login:" + username.getText().toString() + ":" + password.getText().toString());

                    System.out.println(username.getText().toString() + "; " + password.getText().toString());

                    String fromServer = in.readLine();

                    if (fromServer.split(":")[2].equals("true")) {
                        error.setText("");
                        Intent intent = new Intent(MainActivity.this, WarnMarketActivity.class);
                        startActivity(intent);
                    } else {
                        error.setText(R.string.errorUserPass);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        registerButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, WarnMarketActivity.class);
            startActivity(intent);
        });


        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);
    }

    static class MyTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Socket kkSocket;

            try {
                kkSocket = new Socket("192.168.0.124", 4444);
                out = new PrintWriter(kkSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
            } catch (UnknownHostException e) {
                System.err.println("Don't know about host: .");
                System.exit(1);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.err.println("Couldn't get I/O for the connection to: .");
                System.exit(1);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}