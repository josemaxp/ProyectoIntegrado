package josemanuel.marin.finalproject.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import josemanuel.marin.finalproject.R;


public class PriceFragment extends Fragment {
    static EditText editTextPrice2, editTextPrice3;
    static Spinner unity;

    public PriceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price, container, false);

        editTextPrice2 = view.findViewById(R.id.editTextPrice);
        editTextPrice3 = view.findViewById(R.id.editTextPriceUnity);
        unity = view.findViewById(R.id.spinnerUnity);

        //Get unities on spinner
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),R.array.unidades, R.layout.color_spinner_layout);
        unity.setAdapter(adapter2);

        return view;
    }

    static public String getPrice(){
        if(editTextPrice2 != null) {
            return editTextPrice2.getText().toString();
        }else {
            return "";
        }
    }

    static public String getPriceUnity(){
        if(editTextPrice3 != null) {
            return editTextPrice3.getText().toString();
        }else {
            return "";
        }
    }

    static public String getUnity(){
        return unity.getSelectedItem().toString();
    }
}