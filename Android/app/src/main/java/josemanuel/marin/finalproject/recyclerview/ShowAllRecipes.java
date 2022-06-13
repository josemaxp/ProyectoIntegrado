package josemanuel.marin.finalproject.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import josemanuel.marin.finalproject.R;
import josemanuel.marin.finalproject.controller.Connection;
import josemanuel.marin.finalproject.model.ListRecipeItem;
import josemanuel.marin.finalproject.view.EditRecipe;
import josemanuel.marin.finalproject.view.MyRecipes;
import josemanuel.marin.finalproject.view.RecipeClicked;
import josemanuel.marin.finalproject.view.RecipesActivity;

public class ShowAllRecipes extends RecyclerView.Adapter<ShowAllRecipes.RecipeViewHolder> implements PopupMenu.OnMenuItemClickListener {
    List<ListRecipeItem> mData;
    List<ListRecipeItem> listaOriginal;
    private LayoutInflater mInflater;
    private Context context;
    PrintWriter out = null;
    BufferedReader in = null;
    getUser getUser;
    deleteRecipe deleteRecipe;
    reportRecipe reportRecipe;
    int recipeID;
    int position;
    ListRecipeItem recipe;

    public ShowAllRecipes(List<ListRecipeItem> mData, Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = mData;
        this.context = context;
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(mData);
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.recipe_list, parent, false);
        return new RecipeViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.bindData(mData.get(position));
        this.position = holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        public final TextView textViewRecipeName, textViewRecipeTime, textViewRecipePeople, textViewRecipeLikes, textViewRecipeUsername;
        public final ImageView imageViewRecipe, imageViewOptionsMenuRecipe;
        final ShowAllRecipes mAdapter;

        RecipeViewHolder(@NonNull View itemView, ShowAllRecipes adapter) {
            super(itemView);
            textViewRecipeName = itemView.findViewById(R.id.textViewRecipeName);
            textViewRecipeTime = itemView.findViewById(R.id.textViewRecipeTime);
            textViewRecipePeople = itemView.findViewById(R.id.textViewRecipePeople);
            textViewRecipeLikes = itemView.findViewById(R.id.textViewRecipeLikes);
            textViewRecipeUsername = itemView.findViewById(R.id.textViewRecipeUsername);
            imageViewRecipe = itemView.findViewById(R.id.imageViewRecipe);
            imageViewOptionsMenuRecipe = itemView.findViewById(R.id.imageViewOptionsMenuRecipe);
            this.mAdapter = adapter;
        }

        void bindData(final ListRecipeItem item) {
            String[] timeSeparated = item.getTime().trim().split("\\.");
            String newTime = "";
            if(timeSeparated.length == 2) {
                if (timeSeparated[0].equals("00")) {
                    newTime = timeSeparated[1] + " minuto(s)";
                } else {
                    newTime = timeSeparated[0] + " hora(s) y " + timeSeparated[1] + " minuto(s)";
                }
            }else{
                newTime = "Error de formato";
            }

            textViewRecipeName.setText(item.getName());
            textViewRecipeTime.setText(newTime);
            textViewRecipePeople.setText(item.getPeople() + "");
            textViewRecipeLikes.setText(item.getLikes() + "");
            textViewRecipeUsername.setText(item.getUsername());

            imageViewRecipe.setClipToOutline(true);
            if (item.getImage().equals("")) {
                imageViewRecipe.setImageResource(R.drawable.no_image_found);
            } else {
                String url = "http://" + item.getImage().substring(2).replace("\\", "/");
                Picasso.get().load(url).into(imageViewRecipe);
                imageViewRecipe.setBackgroundColor(Color.parseColor("#3F414E"));
            }

            if (imageViewRecipe.getDrawable() == null) {
                imageViewRecipe.setImageResource(R.drawable.no_image_found);
            }


            imageViewOptionsMenuRecipe.setOnClickListener(v -> {
                showMenu(v, item.getUsername(), item.getId(), item);
            });

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, RecipeClicked.class);
                intent.putExtra("Recipe", item);
                context.startActivity(intent);
            });
        }
    }

    public void filtrarDatos(String txtBuscar) {
        if (txtBuscar.length() == 0) {
            mData.clear();
            mData.addAll(listaOriginal);
        } else {
            //Elimino los espacios sobrantes y separo por espacio del texto que se usa para buscar
            String[] quitarEspacio = txtBuscar.trim().replaceAll(" +", " ").split(" ");
            mData.clear();

            for (String s : quitarEspacio) {
                for (ListRecipeItem c : listaOriginal) {
                    if (c.getName().toLowerCase().contains(txtBuscar.toLowerCase()) && !mData.contains(c)) {
                        mData.add(c);
                    }
                    for (int j = 0; j < c.getProducts().size(); j++) {
                        if (c.getProducts().get(j).contains(s.toLowerCase()) && !mData.contains(c)) {
                            mData.add(c);
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void showMenu(View v, String username, int recipeID, ListRecipeItem item) {
        getUser = new getUser();
        String[] currentUSer = null;
        try {
            currentUSer = getUser.execute().get().split(":");
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        PopupMenu menu = new PopupMenu(v.getContext(), v);
        menu.setOnMenuItemClickListener(this);
        menu.inflate(R.menu.recipe_menu);

        this.recipeID = recipeID;
        this.recipe = item;

        if (currentUSer.length > 2) {
            if (currentUSer[2].equals(username)) {
                menu.getMenu().findItem(R.id.update_recipe).setVisible(true);
                menu.getMenu().findItem(R.id.delete_recipe).setVisible(true);
                menu.getMenu().findItem(R.id.report_recipe).setVisible(false);
            } else {
                menu.getMenu().findItem(R.id.update_recipe).setVisible(false);
                menu.getMenu().findItem(R.id.delete_recipe).setVisible(false);
                menu.getMenu().findItem(R.id.report_recipe).setVisible(true);
            }

            menu.show();
        } else {
            Toast.makeText(context, "Inicia sesión para interactuar con la receta.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.report_recipe:
                reportRecipe = new reportRecipe();
                reportRecipe.execute();
                return true;
            case R.id.delete_recipe:
                deleteRecipe = new deleteRecipe();
                deleteRecipe.execute();

                if (context instanceof RecipesActivity) {
                    Intent intent = new Intent(context, RecipesActivity.class);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, MyRecipes.class);
                    context.startActivity(intent);
                }
                return true;
            case R.id.update_recipe:
                Intent intent = new Intent(context, EditRecipe.class);
                intent.putExtra("EditRecipe", recipe);
                context.startActivity(intent);

                return true;
            default:
                return false;
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

    class deleteRecipe extends AsyncTask<Void, Void, String> {
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
            out.println("CL:deleteRecipe:" + recipeID);
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

    class reportRecipe extends AsyncTask<Void, Void, String> {
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
            out.println("CL:reportRecipe:" + recipeID);
            try {
                result = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(context, "Receta denunciada", Toast.LENGTH_LONG).show();
        }
    }
}
