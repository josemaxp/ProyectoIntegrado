package josemanuel.marin.finalproject;

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
import java.util.List;
import java.util.concurrent.ExecutionException;

import josemanuel.marin.finalproject.controller.Connection;
import josemanuel.marin.finalproject.recyclerview.ListOfferItem;
import josemanuel.marin.finalproject.recyclerview.ShowOffer;

public class WarnMarketActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    Button addButton;
    SearchView searchView;
    List<ListOfferItem> offerItems;
    PrintWriter out = null;
    BufferedReader in = null;
    getOffers getOffers;
    getUser getUser;
    ShowOffer adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warn_market);

        addButton = findViewById(R.id.addButton);
        searchView = findViewById(R.id.searchView);

        getUser = new getUser();
        String[] username = null;
        try {
            username = getUser.execute().get().split(":");

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        init();

        String[] finalUsername = username;
        addButton.setOnClickListener(view -> {
            if (finalUsername.length == 2) {
                Toast.makeText(this, "Error, debes iniciar sesión para poder crear una oferta.", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(WarnMarketActivity.this, AddOffer.class);
                startActivity(intent);
            }
        });
    }

    public void init() {
        offerItems = new ArrayList<>();

        getOffers = new getOffers();

        try {
            String fromServer = getOffers.execute().get();
            //Separo la información usando ':'. Así se quedaría dividido en todas las ofertas existentes.
            String[] allOffers = fromServer.split(":");

            boolean comprobarMetros = false;

            //Ahora divido cada una de las ofertas. Las ofertas empiezan en la posición 2.
            for (int i = 2; i < allOffers.length; i++) {
                String[] offer = allOffers[i].split("_");
                List<String> tags = new ArrayList<>();

                //Ahora calculo la distancia de la persona al supermercado
                double distancia = Double.parseDouble(offer[4]);

                if (distancia < 1) {
                    distancia *= 1000;
                    comprobarMetros = true;
                } else {
                    distancia = Math.round(distancia);
                    comprobarMetros = false;
                }

                int distanciaParsed = (int) distancia;

                //Obtengo las etiquetas, que están desde la posición 6 hasta el final
                for (int j = 5; j < offer.length; j++) {
                    tags.add(offer[j].toLowerCase());
                }

                //Añado la oferta a la lista de ofertas para que se visualicen en el recyclerview
                if (comprobarMetros) {
                    offerItems.add(new ListOfferItem(offer[3], tags, "A " + distanciaParsed + " m. de distancia", offer[1] + "€", offer[2], offer[0]));
                } else {
                    offerItems.add(new ListOfferItem(offer[3], tags, "A " + distanciaParsed + " km. de distancia", offer[1] + "€", offer[2], offer[0]));
                }
                //tags = "";
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        //Arrays.sort(offerItems, (a, b) -> a..compareTo(b.name));

        adapter = new ShowOffer(offerItems, this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(this);
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

    class getOffers extends AsyncTask<Void, Void, String> {
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

            out.println("CL:showOffer:" + Login.latitud + ":" + Login.longitud);
            String offers = "";
            try {
                offers = in.readLine();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return offers;
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
}