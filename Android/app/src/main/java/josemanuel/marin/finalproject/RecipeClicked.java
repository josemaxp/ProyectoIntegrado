package josemanuel.marin.finalproject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

import josemanuel.marin.finalproject.controller.Connection;
import josemanuel.marin.finalproject.model.ListRecipeItem;

public class RecipeClicked extends AppCompatActivity {
    TextView textViewRecipeNameOnClick, textViewProductsFromServer, textViewCookwareFromServer, textViewStepsFromServer, textViewTimeFromServer,
            textViewPeopleFromServer, textViewUsernameRecipeTitle;
    ImageView imageViewRecipeFromServer, imageViewLike, imageViewDislike;
    int recipeID = -1;
    String userUpload = "";
    PrintWriter out = null;
    BufferedReader in = null;
    getLikeRecipe getLikeRecipe;
    likeRecipe likeRecipe;
    dislikeRecipe dislikeRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_clicked);

        textViewRecipeNameOnClick = findViewById(R.id.textViewRecipeNameOnClick);
        textViewProductsFromServer = findViewById(R.id.textViewProductsFromServer);
        textViewCookwareFromServer = findViewById(R.id.textViewCookwareFromServer);
        textViewStepsFromServer = findViewById(R.id.textViewStepsFromServer);
        textViewTimeFromServer = findViewById(R.id.textViewTimeFromServer);
        textViewPeopleFromServer = findViewById(R.id.textViewPeopleFromServer);
        imageViewRecipeFromServer = findViewById(R.id.imageViewRecipeFromServer);
        textViewUsernameRecipeTitle = findViewById(R.id.textViewUsernameRecipeTitle);
        imageViewLike = findViewById(R.id.imageViewLike);
        imageViewDislike = findViewById(R.id.imageViewDislike);

        ListRecipeItem recipe = (ListRecipeItem) getIntent().getSerializableExtra("Recipe");

        recipeID = recipe.getId();
        userUpload = recipe.getUsername();

        String[] timeSeparated = recipe.getTime().trim().split("\\.");
        String newTime = "";

        if (timeSeparated[0].equals("00")) {
            newTime = timeSeparated[1] + " minuto(s)";
        } else {
            newTime = timeSeparated[0] + " hora(s) y " + timeSeparated[1] + " minuto(s)";
        }

        String products = "";
        for (int i = 0; i < recipe.getProducts().size(); i++) {
            String[] product = recipe.getProducts().get(i).split("\\|");
            products += "- " + product[0].substring(0, 1).toUpperCase() + product[0].substring(1) + ": " + product[1] + " " + product[2] + "\n";

        }

        textViewRecipeNameOnClick.setText(recipe.getName());
        textViewTimeFromServer.setText(newTime);
        textViewPeopleFromServer.setText(recipe.getPeople() + "");
        textViewUsernameRecipeTitle.setText(recipe.getUsername());
        textViewStepsFromServer.setText(recipe.getSteps().replace("|", "\n"));
        textViewProductsFromServer.setText(products);
        textViewCookwareFromServer.setText(recipe.getCookware().replace("|", "\n"));

        getLikeRecipe = new getLikeRecipe();
        String [] recipeLiked = null;
        try {
            recipeLiked = getLikeRecipe.execute().get().split(":");
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if (recipeLiked[2].equals("true")) {
            imageViewLike.setVisibility(View.VISIBLE);
            imageViewDislike.setVisibility(View.GONE);
        } else {
            imageViewLike.setVisibility(View.GONE);
            imageViewDislike.setVisibility(View.VISIBLE);
        }

        imageViewLike.setOnClickListener(v -> {
            dislikeRecipe = new dislikeRecipe();
            dislikeRecipe.execute();

            imageViewLike.setVisibility(View.GONE);
            imageViewDislike.setVisibility(View.VISIBLE);
        });

        imageViewDislike.setOnClickListener(v -> {
            likeRecipe = new likeRecipe();
            likeRecipe.execute();

            imageViewLike.setVisibility(View.VISIBLE);
            imageViewDislike.setVisibility(View.GONE);
        });
    }

    class getLikeRecipe extends AsyncTask<Void, Void, String> {
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

            String result = "";
            out.println("CL:getLikeRecipe:" + recipeID);
            try {
                result = in.readLine();

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

    class likeRecipe extends AsyncTask<Void, Void, Void> {
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
            } catch (IOException e) {
                e.printStackTrace();
            }

            out.println("CL:likeRecipe:" + recipeID +":"+ userUpload);


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    class dislikeRecipe extends AsyncTask<Void, Void, Void> {
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
            } catch (IOException e) {
                e.printStackTrace();
            }

            out.println("CL:dislikeRecipe:" + recipeID +":"+ userUpload);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
