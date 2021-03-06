package josemanuel.marin.finalproject.view;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.SearchView;

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
import josemanuel.marin.finalproject.model.ListOfferItem;
import josemanuel.marin.finalproject.recyclerview.ShowOffer;

public class MyOffers extends AppCompatActivity implements SearchView.OnQueryTextListener {
    SearchView searchView;
    List<ListOfferItem> offerItems;
    getUser getUser;
    getOffers getOffers;
    PrintWriter out = null;
    BufferedReader in = null;
    ShowOffer adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_offers);

        searchView = findViewById(R.id.searchViewMyOffers);

        showOffers();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        showOffers();
    }

    public void showOffers() {
        getUser = new getUser();
        String[] username = null;
        try {
            username = getUser.execute().get().split(":");
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        offerItems = new ArrayList<>();
        getOffers = new getOffers();
        List<ListOfferItem> totalOfertas = new ArrayList<>();
        try {
            String fromServer = getOffers.execute(username[2]).get();
            //Separo la información usando ':'. Así se quedaría dividido en todas las ofertas existentes.
            String[] allOffers = fromServer.split(":");

            boolean comprobarMetros;

            //Divido cada una de las ofertas. Las ofertas empiezan en la posición 2.
            for (int i = 2; i < allOffers.length; i++) {
                String[] ofertaFromServer = allOffers[i].split("_");
                List<String> tags = new ArrayList<>();
                //Obtengo las etiquetas, que están desde la posición 13 hasta el final
                for (int j = 13; j < ofertaFromServer.length; j++) {
                    tags.add(ofertaFromServer[j].toLowerCase());
                }
                boolean approvedOffer = false;
                if(ofertaFromServer[6].equals("true")){
                    approvedOffer = true;
                }

                ListOfferItem oferta = new ListOfferItem(ofertaFromServer[3], tags, ofertaFromServer[4], ofertaFromServer[1], ofertaFromServer[2], ofertaFromServer[0], "\\\\" + Connection.IP + "\\" + ofertaFromServer[5],approvedOffer,Integer.parseInt(ofertaFromServer[7]),Double.parseDouble(ofertaFromServer[8]),Double.parseDouble(ofertaFromServer[9]));
                totalOfertas.add(oferta);
            }

            Collections.sort(totalOfertas);

            for (int i = 0; i < totalOfertas.size(); i++) {
                //Calculo la distancia de la persona al supermercado
                double distancia = Double.parseDouble(totalOfertas.get(i).getDistance());

                if (distancia < 1) {
                    distancia *= 1000;
                    comprobarMetros = true;
                } else {
                    distancia = Math.round(distancia);
                    comprobarMetros = false;
                }

                int distanciaParsed = (int) distancia;

                if (comprobarMetros) {
                    totalOfertas.get(i).setDistance("A " + distanciaParsed + " m. de distancia");
                } else {
                    totalOfertas.get(i).setDistance("A " + distanciaParsed + " km. de distancia");
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        adapter = new ShowOffer(totalOfertas, this);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewMyOffers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(this);
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

    class getOffers extends AsyncTask<String, Void, String> {
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

            out.println("CL:myOffers:" + params[0] + ":" + Login.latitud + ":" + Login.longitud);
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
