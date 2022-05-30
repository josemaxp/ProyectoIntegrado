package josemanuel.marin.finalproject.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import josemanuel.marin.finalproject.R;
import josemanuel.marin.finalproject.controller.Connection;

public class IPDialog extends DialogFragment {
    connectServer Con;
    EditText editTextIP;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.ip_dialog, null);

        editTextIP = view.findViewById(R.id.EditTextIP);

        SharedPreferences preferences = getActivity().getSharedPreferences("IP", Context.MODE_PRIVATE);
        String ip = preferences.getString("IP", "");

        if (!ip.equals("")) {
            editTextIP.setText(ip);
        }

        builder.setView(view)
                .setMessage("Introduce la IP del servidor")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String ip = editTextIP.getText().toString();

                        if (!ip.equals("")) {
                            Connection.setIP(ip);

                            Con = new connectServer();
                            Con.execute();

                            SharedPreferences preferences = getActivity().getSharedPreferences("IP", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("IP", ip);
                            editor.apply();
                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Objects.requireNonNull(IPDialog.this.getDialog()).cancel();
                    }
                });

        return builder.create();
    }

    class connectServer extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            new Connection();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
