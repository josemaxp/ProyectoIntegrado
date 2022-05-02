package josemanuel.marin.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
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
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    Button loginButton;
    Button registerButton;
    EditText username;
    EditText password;
    TextView error;
    connectServer Con;
    login login;
    register register;
    public static PrintWriter out = null;
    public static BufferedReader in = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Con == null) {
            Con = new connectServer();
            Con.execute();
        }

        username = findViewById(R.id.LoginUsernameText);
        password = findViewById(R.id.LoginPasswordText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        error = findViewById(R.id.textViewMainError);

        loginButton.setOnClickListener(view -> {
            login =  new login();
            try {
                error.setText(login.execute(username.getText().toString(),password.getText().toString()).get());
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        registerButton.setOnClickListener(view -> {
            register = new register();
            register.execute();
        });
    }

    class connectServer extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Socket kkSocket;
            try {
                kkSocket = new Socket("192.168.1.139", 4444);
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

    class login extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                if (params[0].equals("") || params[1].equals("")) {
                    result = "Fields can't be empty.";
                } else {
                    out.println("CL:" + "login:" + username.getText().toString() + ":" + password.getText().toString());
                    String fromServer = in.readLine();

                    if (fromServer.split(":")[2].equals("true")) {
                        Intent intent = new Intent(MainActivity.this, WarnMarketActivity.class);
                        startActivity(intent);
                    } else {
                        result = "Error. Check your username or your password";
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

    class register extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Intent intent = new Intent(MainActivity.this, WarnMarketActivity.class);
            startActivity(intent);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}