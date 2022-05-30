package josemanuel.marin.finalproject.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import josemanuel.marin.finalproject.R;
import josemanuel.marin.finalproject.controller.Connection;
import josemanuel.marin.finalproject.model.ListOfferItem;

public class ShowOffer extends RecyclerView.Adapter<ShowOffer.OfferViewHolder> implements PopupMenu.OnMenuItemClickListener {
    List<ListOfferItem> mData;
    List<ListOfferItem> listaOriginal;
    private LayoutInflater mInflater;
    private Context context;
    PrintWriter out = null;
    BufferedReader in = null;
    getUser getUser;
    deleteOffer deleteOffer;
    reportOffer reportOffer;
    int offerID;
    int position;

    public ShowOffer(List<ListOfferItem> mData, Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = mData;
        this.context = context;
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(mData);
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.offer_list, parent, false);
        return new OfferViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        holder.bindData(mData.get(position));
        this.position = holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class OfferViewHolder extends RecyclerView.ViewHolder {
        public final TextView textViewOfferMarket, textViewOfferTags, textViewOfferDistance, textViewOfferPrice, textViewOfferPriceUnity, textViewOfferUsername;
        public final ImageView imageViewOffer, imageViewApprovedOffer, imageViewOptionsMenu;
        final ShowOffer mAdapter;

        OfferViewHolder(@NonNull View itemView, ShowOffer adapter) {
            super(itemView);
            textViewOfferMarket = itemView.findViewById(R.id.textViewOfferMarket);
            textViewOfferTags = itemView.findViewById(R.id.textViewOfferTags);
            textViewOfferDistance = itemView.findViewById(R.id.textViewOfferDistance);
            textViewOfferPrice = itemView.findViewById(R.id.textViewOfferPrice);
            textViewOfferPriceUnity = itemView.findViewById(R.id.textViewOfferPriceUnity);
            textViewOfferUsername = itemView.findViewById(R.id.textViewOfferUsername);
            imageViewOffer = itemView.findViewById(R.id.imageViewOffer);
            imageViewApprovedOffer = itemView.findViewById(R.id.imageViewApprovedOffer);
            imageViewOptionsMenu = itemView.findViewById(R.id.imageViewOptionsMenu);
            this.mAdapter = adapter;
        }

        void bindData(final ListOfferItem item) {
            String tags = "";
            for (int i = 0; i < item.getTags().size(); i++) {
                //Capitalizo las palabras para mostrarlas
                if (i < item.getTags().size() - 1) {
                    tags += item.getTags().get(i).substring(0, 1).toUpperCase() + item.getTags().get(i).substring(1) + " - ";
                } else {
                    tags += item.getTags().get(i).substring(0, 1).toUpperCase() + item.getTags().get(i).substring(1);
                }
            }

            textViewOfferMarket.setText(item.getMarket());
            textViewOfferTags.setText(tags);
            textViewOfferDistance.setText(item.getDistance());
            textViewOfferPrice.setText(item.getPrice());
            textViewOfferPriceUnity.setText("(" + item.getPriceUnity() + ")");
            textViewOfferUsername.setText(item.getUsername());

            if (item.getApprovedOffer()) {
                imageViewApprovedOffer.setVisibility(View.VISIBLE);
            } else {
                imageViewApprovedOffer.setVisibility(View.GONE);
            }

            File file = new File(item.getImage());
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            imageViewOffer.setImageBitmap(bitmap);

            imageViewOptionsMenu.setOnClickListener(v -> {
                showMenu(v, item.getUsername(), item.getID());
            });

            itemView.setOnClickListener(v -> {
                Uri gmmIntentUri = Uri.parse("google.streetview:cbll=" + item.getLatitud() + "," + item.getLongitud());

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                itemView.getContext().startActivity(mapIntent);
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
                for (ListOfferItem c : listaOriginal) {
                    if (c.getMarket().toLowerCase().contains(txtBuscar.toLowerCase()) && !mData.contains(c)) {
                        mData.add(c);
                    }
                    for (int j = 0; j < c.getTags().size(); j++) {
                        if (c.getTags().get(j).contains(s.toLowerCase()) && !mData.contains(c)) {
                            mData.add(c);
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void showMenu(View v, String username, int offerID) {
        getUser = new getUser();
        String[] currentUSer = null;
        try {
            currentUSer = getUser.execute().get().split(":");
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        PopupMenu menu = new PopupMenu(v.getContext(), v);
        menu.setOnMenuItemClickListener(this);
        menu.inflate(R.menu.offer_menu);

        this.offerID = offerID;

        System.out.println(offerID);

        if (currentUSer.length > 2 && currentUSer[2].equals(username)) {
            menu.getMenu().findItem(R.id.delete_offer).setVisible(true);
            menu.getMenu().findItem(R.id.report_offer).setVisible(false);
        } else {
            menu.getMenu().findItem(R.id.delete_offer).setVisible(false);
            menu.getMenu().findItem(R.id.report_offer).setVisible(true);
        }

        menu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.report_offer:
                reportOffer = new reportOffer();
                reportOffer.execute();
                return true;
            case R.id.delete_offer:
                deleteOffer = new deleteOffer();
                deleteOffer.execute();
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

    class deleteOffer extends AsyncTask<Void, Void, String> {
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
            out.println("CL:deleteOffer:" + offerID);
            try {
                result = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            notifyItemRemoved(position);
        }
    }

    class reportOffer extends AsyncTask<Void, Void, String> {
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
            out.println("CL:reportOffer:" + offerID);
            try {
                result = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(context, "Oferta denunciada", Toast.LENGTH_LONG).show();
        }
    }
}
