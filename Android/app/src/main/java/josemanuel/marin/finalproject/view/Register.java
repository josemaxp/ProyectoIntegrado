package josemanuel.marin.finalproject.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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

import josemanuel.marin.finalproject.R;
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
                registerError.setText(R.string.errorEmptyFields);
            } else {
                if (isValidEmail(email.getText().toString())) {
                    if (repeatPassword.getText().toString().equals(password.getText().toString())) {
                        register = new register();
                        try {
                            boolean result = register.execute(username.getText().toString(), password.getText().toString(), email.getText().toString()).get();
                            String message = "Error al crear usuario.";

                            if (result) {
                                message = "Usuario creado correctamete.";
                            }

                            registerError.setText("");
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        registerError.setText(R.string.error_passwords);
                    }
                } else {
                    registerError.setText(R.string.errorEmail);
                }
            }
        });
    }

    class register extends AsyncTask<String, Void, Boolean> {
        Socket s;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            s = Connection.getSocket();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean result = false;
            try {
                out = new PrintWriter(s.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));

                out.println("CL:" + "register:" + params[0] + ":" + params[1] + ":" + params[2]);
                String fromServer = in.readLine();

                if (fromServer.split(":")[2].equals("true")) {
                    result = true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
