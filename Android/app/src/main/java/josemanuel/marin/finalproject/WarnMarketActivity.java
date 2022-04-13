package josemanuel.marin.finalproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

public class WarnMarketActivity extends AppCompatActivity{
    Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warn_market);

        addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener(view -> {
            Intent intent = new Intent(WarnMarketActivity.this, AddOffer.class);
            startActivity(intent);
        });
    }
}