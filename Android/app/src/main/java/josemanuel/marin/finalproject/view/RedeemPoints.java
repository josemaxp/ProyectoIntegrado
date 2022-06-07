package josemanuel.marin.finalproject.view;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

import josemanuel.marin.finalproject.R;
import josemanuel.marin.finalproject.controller.Connection;

public class RedeemPoints extends AppCompatActivity {
    TextView textViewPointsRedeem;
    PrintWriter out;
    BufferedReader in;
    initData initData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.redeem_points);

        textViewPointsRedeem = findViewById(R.id.textViewPointsRedeem);

        initData = new initData();
        try {
            String[] userInfo = initData.execute().get().split(":");
            textViewPointsRedeem.setText("Puntos: " + userInfo[4]);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
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

                out.println("CL:" + "userInfo");
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
}
