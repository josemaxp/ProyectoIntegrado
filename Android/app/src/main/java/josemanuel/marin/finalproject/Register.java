package josemanuel.marin.finalproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

import josemanuel.marin.finalproject.controller.Connection;

public class Register extends AppCompatActivity {
    EditText email, username, password, repeatPassword;
    TextView registerError;
    Button registerButton;
    PrintWriter out;
    BufferedReader in;
    register register;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        email = findViewById(R.id.emailTextRegister);
        username = findViewById(R.id.usernameTextRegister);
        password = findViewById(R.id.passwordTextRegister);
        repeatPassword = findViewById(R.id.repeatPasswordTextRegister);
        registerError = findViewById(R.id.textViewRegisterError);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            if (email.getText().toString().equals("") || username.getText().toString().equals("") || password.getText().toString().equals("") || repeatPassword.getText().toString().equals("")) {
                registerError.setText("Los campos no pueden estar vacíos.");
            } else {
                if (repeatPassword.getText().toString().equals(password.getText().toString())) {
                    register = new register();
                    try {
                        String result = register.execute(username.getText().toString(), password.getText().toString(), email.getText().toString()).get();

                        registerError.setText("");
                        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }


                } else {
                    registerError.setText("Las contraseñas no coinciden.");
                }
            }
        });
    }

    class register extends AsyncTask<String, Void, String> {
        Socket s;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            s = Connection.getSocket();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                out = new PrintWriter(s.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));

                out.println("CL:" + "register:" + params[0] + ":" + params[1] + ":" + params[2]);
                String fromServer = in.readLine();

                if (fromServer.split(":")[2].equals("true")) {
                    result = "Usuario creado correctamete.";
                } else {
                    result = "Error al crear usuario.";
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
        }
    }
}
