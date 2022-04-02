package josemanuel.marin.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class WarnMarketActivity extends AppCompatActivity{
    Button addButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warn_market);

        addButton = findViewById(R.id.addButton);


    }
}