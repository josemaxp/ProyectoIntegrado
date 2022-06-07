package josemanuel.marin.finalproject.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import josemanuel.marin.finalproject.R;
import josemanuel.marin.finalproject.controller.Connection;
import josemanuel.marin.finalproject.model.ListOfferItem;

public class EditOffer extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    AutoCompleteTextView newTag;
    TextView editTextPriceEdit, editTextPriceUnityEdit, errorText;
    Button addTag, buttonTag1, buttonTag2, buttonTag3, buttonTag4, buttonTag5, buttonTag6, buttonTag7, buttonTag8, buttonTag9, buttonTag10, buttonEditOffer;
    Spinner spinnerUnityEdit;
    ImageView imageViewAddImageEdit;
    int offerID = -1;
    List<String> tagsList = new ArrayList<>();
    List<Button> buttonsList = new ArrayList<>();
    Bitmap imageBitmap;
    PrintWriter out = null;
    BufferedReader in = null;
    getTagsName getTagsName;
    uploadImage uploadImage;
    getUser getUser;
    updateOffer updateOffer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_offer);

        ListOfferItem offer = (ListOfferItem) getIntent().getSerializableExtra("EditOffer");

        offerID = offer.getID();

        newTag = findViewById(R.id.editTextTextAddTagEdit);
        addTag = findViewById(R.id.buttonAddTagEdit);
        buttonTag1 = findViewById(R.id.buttonTag1Edit);
        buttonTag2 = findViewById(R.id.buttonTag2Edit);
        buttonTag3 = findViewById(R.id.buttonTag3Edit);
        buttonTag4 = findViewById(R.id.buttonTag4Edit);
        buttonTag5 = findViewById(R.id.buttonTag5Edit);
        buttonTag6 = findViewById(R.id.buttonTag6Edit);
        buttonTag7 = findViewById(R.id.buttonTag7Edit);
        buttonTag8 = findViewById(R.id.buttonTag8Edit);
        buttonTag9 = findViewById(R.id.buttonTag9Edit);
        buttonTag10 = findViewById(R.id.buttonTag10Edit);
        editTextPriceEdit = findViewById(R.id.editTextPriceEdit);
        editTextPriceUnityEdit = findViewById(R.id.editTextPriceUnityEdit);
        spinnerUnityEdit = findViewById(R.id.spinnerUnityEdit);
        imageViewAddImageEdit = findViewById(R.id.imageViewAddImageEdit);
        errorText = findViewById(R.id.textViewErrorEdit);
        buttonEditOffer = findViewById(R.id.buttonEditOffer);

        buttonsList.add(buttonTag1);
        buttonsList.add(buttonTag2);
        buttonsList.add(buttonTag3);
        buttonsList.add(buttonTag4);
        buttonsList.add(buttonTag5);
        buttonsList.add(buttonTag6);
        buttonsList.add(buttonTag7);
        buttonsList.add(buttonTag8);
        buttonsList.add(buttonTag9);
        buttonsList.add(buttonTag10);

        setNewTagAdapter();

        editTextPriceEdit.setText(offer.getPrice());
        editTextPriceUnityEdit.setText(offer.getPriceUnity().split("€")[0]);
        String[] stringArray = getResources().getStringArray(R.array.unidades);
        for (int i = 0; i < stringArray.length; i++) {
            if (offer.getPriceUnity().split("/")[1].equals(stringArray[i])) {
                spinnerUnityEdit.setSelection(i);
            }
        }

        for (int i = 0; i < offer.getTags().size(); i++) {
            buttonsList.get(i).setText(offer.getTags().get(i) + "  X");
            buttonsList.get(i).setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < 10; i++) {
            tagsList.add("-");
        }

        for (int i = 0; i < offer.getTags().size(); i++) {
            tagsList.set(i, offer.getTags().get(i));
        }

        for (int i = 0; i < buttonsList.size(); i++) {
            Button removeTag = buttonsList.get(i);
            int num = i;

            removeTag.setOnClickListener(v -> {
                removeTag.setVisibility(View.GONE);
                tagsList.set(num, "-");

                int contador = 0;
                for (int j = 0; j < tagsList.size(); j++) {
                    if (!tagsList.get(j).equals("-")) {
                        contador++;
                    }
                }

                if (contador == tagsList.size()) {
                    errorText.setText(R.string.error_too_many_tags);
                } else {
                    errorText.setText("");
                }
            });
        }

        //Take picture
        imageViewAddImageEdit.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });

        addTag.setOnClickListener(v -> {
            for (int i = 0; i < tagsList.size(); i++) {
                if (buttonsList.get(i).getText().toString().equals("-")) {
                    buttonsList.get(i).setVisibility(View.GONE);
                }

                if (!newTag.getText().toString().equals("") && tagsList.get(i).equals("-")) {
                    if (!tagsList.contains(newTag.getText().toString().toLowerCase().trim())) {
                        errorText.setText("");
                        tagsList.set(i, newTag.getText().toString().toLowerCase().trim());
                        buttonsList.get(i).setVisibility(View.VISIBLE);
                        buttonsList.get(i).setText(tagsList.get(i) + "  X");
                        newTag.setText("");
                        break;
                    } else {
                        errorText.setText(R.string.repeated_tags);
                    }
                }
            }

            int contador = 0;
            for (int i = 0; i < tagsList.size(); i++) {
                if (!tagsList.get(i).equals("-")) {
                    contador++;
                }
            }

            if (contador == tagsList.size()) {
                errorText.setText(R.string.error_too_many_tags);
            } else {
                errorText.setText("");
            }
        });

        buttonEditOffer.setOnClickListener(v -> {
            int contadorTags = 0;
            String tags = "";

            for (int i = 0; i < tagsList.size(); i++) {
                if (!tagsList.get(i).equals("-")) {
                    contadorTags++;
                }
            }

            if (contadorTags < 3) {
                errorText.setText(R.string.error_minimun_tags);
            } else if (editTextPriceEdit.getText().toString().equals("") || editTextPriceUnityEdit.getText().toString().equals("")) {
                errorText.setText(R.string.error_empty_price);
            } else if (spinnerUnityEdit.getSelectedItem().toString().equals("-")) {
                errorText.setText(R.string.error_empty_unity);
            } else {

                for (int i = 0; i < tagsList.size(); i++) {
                    tags += tagsList.get(i).trim() + ",";
                }

                restartTagsList();

                errorText.setText("");

                updateOffer = new updateOffer();
                updateOffer.execute(tags, editTextPriceEdit.getText().toString(), editTextPriceUnityEdit.getText().toString(),
                        spinnerUnityEdit.getSelectedItem().toString(), "null", String.valueOf(offer.getID()));

                if (imageBitmap != null) {
                    transformImage();
                }
            }
        });
    }

    private void setNewTagAdapter() {
        try {
            getTagsName = new getTagsName();
            String[] tagsNameFromServer = getTagsName.execute().get().split(":");
            List<String> tagsName = new ArrayList<>();

            for (int i = 2; i < tagsNameFromServer.length; i++) {
                tagsName.add(tagsNameFromServer[i]);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tagsName);
            newTag.setAdapter(adapter);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    class getTagsName extends AsyncTask<Void, Void, String> {
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

            out.println("CL:tagsName");
            String tagsName = "";
            try {
                tagsName = in.readLine();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return tagsName;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageViewAddImageEdit.setBackgroundColor(Color.parseColor("#292A33"));
            imageViewAddImageEdit.setImageBitmap(imageBitmap);
        }
    }

    public void restartTagsList() {
        tagsList.clear();
        for (int i = 0; i < 10; i++) {
            tagsList.add("-");
        }
    }

    private void transformImage() {
        getUser = new getUser();
        String[] username;
        try {
            username = getUser.execute().get().split(":");
            //LocalDateTime
            File f = new File(getCacheDir(), "D" + LocalDate.now() + "H" + LocalDateTime.now().getHour() + "M" + LocalDateTime.now().getMinute() + "S" + LocalDateTime.now().getSecond() + username[2]);
            try {
                f.createNewFile();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
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

    class uploadImage extends AsyncTask<File, Void, File> {
        Socket s;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            s = Connection.getSocket();
        }

        @Override
        protected File doInBackground(File... params) {
            out.println("CL:updateImgOffer:" + params[0].getName() + ":" + params[0].length() + ":" + offerID);
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
            Intent intent = new Intent(getApplicationContext(), WarnMarketActivity.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "Oferta actualizada", Toast.LENGTH_LONG).show();
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

    class updateOffer extends AsyncTask<String, Void, String> {
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
            out.println("CL:" + "updateOffer:" + params[0] + ":" + params[1] + ":" + params[2] + ":" + params[3] + ":" + params[4] + ":" + params[5]);

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Intent intent = new Intent(getApplicationContext(), WarnMarketActivity.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "Oferta actualizada", Toast.LENGTH_LONG).show();
        }
    }
}
