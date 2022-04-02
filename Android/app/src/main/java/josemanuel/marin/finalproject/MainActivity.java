package josemanuel.marin.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button loginButton;
    EditText username;
    EditText password;
    MyTask mt;
    PrintWriter out = null;
    BufferedReader in = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(mt == null) {
            mt = new MyTask();
            mt.execute();
        }
        username = (EditText) findViewById(R.id.LoginUsernameText);
        password = (EditText) findViewById(R.id.LoginPasswordText);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);
    }

    public void onClick(View v) {
        try {
            if(username.getText().toString().equals("") || password.getText().toString().equals("")){
                System.out.println("Por favor, rellene los campos.");
            }else {
                out.println("CL:" + "login:" + username.getText().toString() + ":" + password.getText().toString());

                System.out.println(username.getText().toString() + "; " + password.getText().toString());

                String fromServer = in.readLine();

                if (fromServer.split(":")[2].equals("true")) {
                    System.out.println("Login correcto");
                } else {
                    System.out.println("Login erróneo");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class MyTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Socket kkSocket;

            try {
                kkSocket = new Socket("192.168.1.139", 4444);
                System.out.println("Conected.");
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