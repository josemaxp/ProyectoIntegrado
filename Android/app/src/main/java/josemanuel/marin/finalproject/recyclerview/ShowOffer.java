package josemanuel.marin.finalproject.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import josemanuel.marin.finalproject.R;

public class ShowOffer extends RecyclerView.Adapter<ShowOffer.OfferViewHolder> {
    List<ListOfferItem> mData;
    List<ListOfferItem> listaOriginal;
    private LayoutInflater mInflater;
    private Context context;

    public ShowOffer(List<ListOfferItem> mData, Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = mData;
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
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class OfferViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView textViewOfferMarket, textViewOfferTags, textViewOfferDistance, textViewOfferPrice, textViewOfferPriceUnity, textViewOfferUsername;
        //public final ImageView imageViewOffer;
        final ShowOffer mAdapter;

        OfferViewHolder(@NonNull View itemView, ShowOffer adapter) {
            super(itemView);
            textViewOfferMarket = itemView.findViewById(R.id.textViewOfferMarket);
            textViewOfferTags = itemView.findViewById(R.id.textViewOfferTags);
            textViewOfferDistance = itemView.findViewById(R.id.textViewOfferDistance);
            textViewOfferPrice = itemView.findViewById(R.id.textViewOfferPrice);
            textViewOfferPriceUnity = itemView.findViewById(R.id.textViewOfferPriceUnity);
            textViewOfferUsername = itemView.findViewById(R.id.textViewOfferUsername);
            this.mAdapter = adapter;
            itemView.setOnClickListener((View.OnClickListener) this);
        }

        void bindData(final ListOfferItem item) {
            String tags = "";
            for (int i = 0; i < item.getTags().size();i++){
                //Capitalizo las palabras para mostrarlas
                if(i < item.getTags().size() -1){
                    tags += item.getTags().get(i).substring(0, 1).toUpperCase()+item.getTags().get(i).substring(1)+" - ";
                }else{
                    tags += item.getTags().get(i).substring(0, 1).toUpperCase()+item.getTags().get(i).substring(1);
                }
            }

            textViewOfferMarket.setText(item.getMarket());
            textViewOfferTags.setText(tags);
            textViewOfferDistance.setText(item.getDistance());
            textViewOfferPrice.setText(item.getPrice());
            textViewOfferPriceUnity.setText("(" + item.getPriceUnity() + ")");
            textViewOfferUsername.setText(item.getUsername());
        }

        public void onClick(View view) {
            /*int mPosition = getLayoutPosition();
            Item element = item.get(mPosition);

            ArrayList<String> textoElementos = new ArrayList();
            textoElementos.add(element.getNombre());
            textoElementos.add(element.getDescripcion());
            textoElementos.add(element.getEstado());

            Intent intent = new Intent(mcon,mostrarInfo.class);
            intent.putExtra(EXTRA_MESSAGE, textoElementos);
            mcon.startActivity(intent);

            mAdapter.notifyDataSetChanged();*/
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filtrarDatos(String txtBuscar) {
        if (txtBuscar.length() == 0) {
            mData.clear();
            mData.addAll(listaOriginal);
        } else {
            //Elimino los espacios sobrantes y separo por espacio del texto que se usa para buscar
            String [] quitarEspacio = txtBuscar.trim().replaceAll(" +", " ").split(" ");
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
}
