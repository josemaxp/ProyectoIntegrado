package josemanuel.marin.finalproject.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import josemanuel.marin.finalproject.R;
import josemanuel.marin.finalproject.controller.Connection;
import josemanuel.marin.finalproject.model.ListRecipeItem;
import josemanuel.marin.finalproject.recyclerview.ShowAllRecipes;

public class RecipesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    Button addRecipeButton, profileButton2, offersButton;
    SearchView searchViewRecipes;
    List<ListRecipeItem> recipeItems;
    getUser getUser;
    getRecipes getRecipes;
    PrintWriter out = null;
    BufferedReader in = null;
    ShowAllRecipes adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        addRecipeButton = findViewById(R.id.addRecipeButton);
        searchViewRecipes = findViewById(R.id.searchViewRecipes);
        profileButton2 = findViewById(R.id.profileButton2);
        offersButton = findViewById(R.id.offersButton);

        getUser = new getUser();
        String[] username = null;
        try {
            username = getUser.execute().get().split(":");
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        showRecipes();

        String[] finalUsername = username;
        addRecipeButton.setOnClickListener(view -> {
            if (finalUsername.length == 2) {
                Toast.makeText(this, "Error, debes iniciar sesión para poder crear una receta.", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(RecipesActivity.this, AddRecipe.class);
                startActivity(intent);
            }
        });

        profileButton2.setOnClickListener(view -> {
            if (finalUsername.length == 2) {
                Toast.makeText(this, "Error, debes iniciar sesión para poder acceder a los ajustes de usuario.", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(RecipesActivity.this, MyAccount.class);
                startActivity(intent);
            }
        });

        offersButton.setOnClickListener(v -> {
            Intent intent = new Intent(RecipesActivity.this, WarnMarketActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onRestart() {
        super.onRestart();
        showRecipes();
    }

    public void showRecipes() {
        recipeItems = new ArrayList<>();
        getRecipes = new getRecipes();
        List<ListRecipeItem> totalRecetas = new ArrayList<>();
        try {
            String fromServer = getRecipes.execute().get();
            //Separo la información usando ':'. Así se quedaría dividido en todas las recetas existentes.
            String[] allRecipes = fromServer.split(":");

            //Divido cada una de las recetas. Las recetas empiezan en la posición 2.
            for (int i = 2; i < allRecipes.length; i++) {
                String[] recipeFromServer = allRecipes[i].split("_");
                List<String> products = new ArrayList<>();
                //Obtengo los productos, que están desde la posición 9 hasta el final
                for (int j = 9; j < recipeFromServer.length; j++) {
                    products.add(recipeFromServer[j].toLowerCase());
                }

                ListRecipeItem oferta = new ListRecipeItem(Integer.parseInt(recipeFromServer[0]), recipeFromServer[1], recipeFromServer[2], products, Integer.parseInt(recipeFromServer[3]), recipeFromServer[4],Integer.parseInt(recipeFromServer[5]),recipeFromServer[6],"\\\\"+Connection.IP+"\\"+recipeFromServer[7],recipeFromServer[8]);
                totalRecetas.add(oferta);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        Collections.sort(totalRecetas);

        adapter = new ShowAllRecipes(totalRecetas, this);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewRecipes);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        searchViewRecipes.setOnQueryTextListener(this);
    }

    class getRecipes extends AsyncTask<Void, Void, String> {
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

            out.println("CL:getRecipes");
            String recipes = "";
            try {
                recipes = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return recipes;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filtrarDatos(newText);
        return false;
    }
}
