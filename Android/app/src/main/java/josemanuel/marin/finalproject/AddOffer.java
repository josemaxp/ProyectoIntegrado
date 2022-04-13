package josemanuel.marin.finalproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

public class AddOffer extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView mImageView;
    Spinner spinner;
    Button addOfferButton;
    TextView error;
    addOffer addOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offer);

        mImageView = findViewById(R.id.imageViewAddImage);
        spinner = findViewById(R.id.spinnerUnity);
        addOfferButton = findViewById(R.id.buttonAddOffer);
        error = findViewById(R.id.textViewAddError);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.unidades, android.R.layout.simple_list_item_1);
        spinner.setAdapter(adapter);

        mImageView.setOnClickListener(view -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });

        addOfferButton.setOnClickListener(view -> {
            addOffer =  new addOffer();
            try {
                error.setText(addOffer.execute(username.getText().toString(),password.getText().toString()).get());
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setBackgroundColor(Color.parseColor("#292A33"));
            mImageView.setImageBitmap(imageBitmap);
        }
    }

    class addOffer extends AsyncTask<String, String, String> {
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
                    System.out.println(params[0] + "; " + params[1]);
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
}