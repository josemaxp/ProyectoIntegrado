package josemanuel.marin.finalproject.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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

public class AddRecipe extends AppCompatActivity {
    AutoCompleteTextView editTextProductName;
    EditText editTextRecipeName, editTextProductQuantity, editTextSteps, editTextCookware, editTextProductPeople,
            editTextProductTime;
    TextView textViewProducts, textViewAddRecipeError;
    Spinner spinnerProductUnity;
    Button buttonAddProduct, buttonAddRecipe;
    ImageView imageViewAddImage;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap imageBitmap = null;
    String products = "", imageName = "";
    PrintWriter out = null;
    BufferedReader in = null;
    addRecipe addRecipe;
    getUser getUser;
    uploadImage uploadImage;
    getProductsName getProductsName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        editTextRecipeName = findViewById(R.id.editTextRecipeName);
        editTextProductName = findViewById(R.id.editTextProductName);
        editTextProductQuantity = findViewById(R.id.editTextProductQuantity);
        editTextSteps = findViewById(R.id.editTextSteps);
        editTextCookware = findViewById(R.id.editTextCookware);
        editTextProductPeople = findViewById(R.id.editTextProductPeople);
        editTextProductTime = findViewById(R.id.editTextProductTime);
        textViewProducts = findViewById(R.id.textViewProducts);
        textViewAddRecipeError = findViewById(R.id.textViewAddRecipeError);
        spinnerProductUnity = findViewById(R.id.spinnerProductUnity);
        buttonAddProduct = findViewById(R.id.buttonAddProduct);
        buttonAddRecipe = findViewById(R.id.buttonAddRecipe);
        imageViewAddImage = findViewById(R.id.imageViewAddImage);
        textViewProducts.setText("");

        setProductsAdapter();

        buttonAddProduct.setOnClickListener(v -> {
            if (!editTextProductName.getText().toString().equals("") && !editTextProductQuantity.getText().toString().equals("") && !spinnerProductUnity.getSelectedItem().equals("-")) {
                textViewProducts.setText(textViewProducts.getText().toString() + "\n-" + editTextProductName.getText().toString() + ": " + editTextProductQuantity.getText().toString() + " " + spinnerProductUnity.getSelectedItem());

                products += editTextProductName.getText().toString() + "_" + editTextProductQuantity.getText().toString() + "_" + spinnerProductUnity.getSelectedItem() + ":";

                editTextProductName.setText("");
                editTextProductQuantity.setText("");
                spinnerProductUnity.setSelection(0);
            }
        });

        buttonAddRecipe.setOnClickListener(v -> {
            if (products.equals("")) {
                textViewAddRecipeError.setText("Error, añade algún producto.");
            } else if (editTextRecipeName.getText().toString().equals("")) {
                textViewAddRecipeError.setText("Error, escribe el nombre de la receta.");
            } else if (editTextSteps.getText().toString().equals("")) {
                textViewAddRecipeError.setText("Error, escribe los pasos de la receta.");
            } else if (editTextCookware.getText().toString().equals("")) {
                textViewAddRecipeError.setText("Error, escribe los utensilios necesarios para la receta.");
            } else if (editTextProductPeople.getText().toString().equals("")) {
                textViewAddRecipeError.setText("Error, escribe el número de comensales.");
            } else if (editTextProductTime.getText().toString().equals("")) {
                textViewAddRecipeError.setText("Error, escribe el tiempo de duración total.");
            } else if (imageBitmap == null) {
                textViewAddRecipeError.setText("Error, la receta debe tener una imagen.");
            } else {
                textViewAddRecipeError.setText("");
                addRecipe = new addRecipe();
                //Sustituyo los saltos de línea por | y los : por .
                addRecipe.execute(editTextRecipeName.getText().toString(), editTextSteps.getText().toString().replace("\n", "|").replace(":", "."), editTextCookware.getText().toString().replace("\n", "|").replace(":", "."), editTextProductPeople.getText().toString(), editTextProductTime.getText().toString().replace(":", "."), products);
            }
        });

        imageViewAddImage.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageViewAddImage.setBackgroundColor(Color.parseColor("#292A33"));
            imageViewAddImage.setImageBitmap(imageBitmap);
        }
    }

    private void transformImage() {
        getUser = new getUser();
        String[] username;
        try {
            username = getUser.execute().get().split(":");

            imageName = "D" + LocalDate.now() + "H" + LocalDateTime.now().getHour() + "M" + LocalDateTime.now().getMinute() + "S" + LocalDateTime.now().getSecond() + username[2];

            File f = new File(getCacheDir(), imageName);
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

    class addRecipe extends AsyncTask<String, Void, String> {
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
            for (int i = 0; i < params.length; i++) {
                System.out.println(params[i]);

            }

            out.println("CL:" + "updateRecipe:" + params[0] + ":" + params[1] + ":" + params[2] + ":" + params[3] + ":" + params[4] + ":" + params[5]);

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            transformImage();
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
            out.println("CL:imgRecipe:" + params[0].getName() + ":" + params[0].length());
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
            Intent intent = new Intent(AddRecipe.this, RecipesActivity.class);
            startActivity(intent);
            Toast.makeText(AddRecipe.this, "Receta creada", Toast.LENGTH_LONG).show();
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

    class getProductsName extends AsyncTask<Void, Void, String> {
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

            String products = "";
            out.println("CL:getProductsName");
            try {
                products = in.readLine();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return products;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    private void setProductsAdapter() {
        try {
            getProductsName = new getProductsName();
            String[] productsNameFromServer = getProductsName.execute().get().split(":");
            List<String> productsName = new ArrayList<>();

            for (int i = 2; i < productsNameFromServer.length; i++) {
                productsName.add(productsNameFromServer[i]);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productsName);
            editTextProductName.setAdapter(adapter);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
