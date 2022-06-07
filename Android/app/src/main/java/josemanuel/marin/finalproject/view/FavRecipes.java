package josemanuel.marin.finalproject.view;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.TextView;

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

public class FavRecipes extends AppCompatActivity implements SearchView.OnQueryTextListener {
    SearchView searchViewRecipes;
    TextView textViewNoFav;
    List<ListRecipeItem> recipeItems;
    getRecipes getRecipes;
    PrintWriter out = null;
    BufferedReader in = null;
    ShowAllRecipes adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fav_recipes);

        searchViewRecipes = findViewById(R.id.searchViewFavRecipes);
        textViewNoFav = findViewById(R.id.textViewNoFav);

        showRecipes();
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

                ListRecipeItem recipe = new ListRecipeItem(Integer.parseInt(recipeFromServer[0]), recipeFromServer[1], recipeFromServer[2], products, Integer.parseInt(recipeFromServer[3]), recipeFromServer[4], Integer.parseInt(recipeFromServer[5]), recipeFromServer[6], "\\\\" + Connection.IP + "\\" + recipeFromServer[7], recipeFromServer[8]);
                totalRecetas.add(recipe);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        if(!totalRecetas.isEmpty()){
            textViewNoFav.setText("");
        }else{
            textViewNoFav.setText("No tienes ninguna receta guardada aún :(");
        }

        Collections.sort(totalRecetas);

        adapter = new ShowAllRecipes(totalRecetas, this);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewFavRecipes);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        searchViewRecipes.setOnQueryTextListener(this);
    }

    class getRecipes extends AsyncTask<String, Void, String> {
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

            out.println("CL:getAllLikeRecipe");
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
