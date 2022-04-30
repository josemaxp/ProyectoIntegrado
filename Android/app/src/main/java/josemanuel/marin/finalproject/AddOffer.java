package josemanuel.marin.finalproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import josemanuel.marin.finalproject.controller.PagerAdapter;
import josemanuel.marin.finalproject.fragments.ImageFragment;
import josemanuel.marin.finalproject.fragments.LocationFragment;
import josemanuel.marin.finalproject.fragments.PriceFragment;
import josemanuel.marin.finalproject.fragments.TagsFragment;

public class AddOffer extends AppCompatActivity {
    Button addOfferButton;
    TextView error;
    addOffer addOffer;
    getMarkets getMarkets;
    TabLayout tabLayout;
    PrintWriter out = null;
    BufferedReader in = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offer);

        addOfferButton = findViewById(R.id.buttonAddOffer);
        error = findViewById(R.id.textViewAddError);
        tabLayout = findViewById(R.id.tabLayout);

        ViewPager2 viewPager = findViewById(R.id.pager);
        PagerAdapter adapter3 = new PagerAdapter(this);
        viewPager.setAdapter(adapter3);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Instantiate asynctask
        if (getMarkets == null) {
            getMarkets = new getMarkets();
        }

        //Get markets on spinner
        ArrayAdapter<String> adapter = null;
        try {
            String[] marketListServer = getMarkets.execute().get().split(":");

            List<String> marketList = new ArrayList();
            for (int i = 2; i < marketListServer.length; i++) {
                marketList.add(marketListServer[i]);
                System.out.println(marketListServer[i]);
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Add offer button
        addOfferButton.setOnClickListener(view -> {
            List<String> tagsList = TagsFragment.getTagsList();
            String tags = "";

            int contadorTags = 0;

            for (int i = 0; i < tagsList.size(); i++) {
                if (!tagsList.get(i).equals("-")) {
                    contadorTags++;
                }
            }

            if (contadorTags < 3) {
                error.setText(R.string.error_minimun_tags);
            } else if (PriceFragment.getPrice().equals("") || PriceFragment.getPriceUnity().equals("")) {
                error.setText(R.string.error_empty_price);
            } else if (PriceFragment.getUnity().equals("-")) {
                error.setText(R.string.error_empty_unity);
            } else if (ImageFragment.getBitmap() == null) {
                error.setText(R.string.error_empty_image);
            } else if (LocationFragment.getLocation().equals("")) {
                error.setText(R.string.error_empty_location);
            } else if (LocationFragment.getMarket().equals("")) {
                error.setText(R.string.error_empty_market);
            } else {
                for (int i = 0; i < tagsList.size(); i++) {
                    tags += tagsList.get(i) + ",";
                }

                error.setText("");
                addOffer = new addOffer();
                addOffer.execute(tags.trim(), PriceFragment.getPrice(), PriceFragment.getPriceUnity(),
                        PriceFragment.getUnity(), ImageFragment.getBitmap().toString(), LocationFragment.getMarket(), LocationFragment.getLocationData());
                Intent intent = new Intent(AddOffer.this, WarnMarketActivity.class);
                startActivity(intent);
                Toast.makeText(AddOffer.this, "Offer added", Toast.LENGTH_LONG).show();
            }
        });
    }

    class addOffer extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            out = MainActivity.out;
            in = MainActivity.in;

            out.println("CL:" + "AddOffer:" + params[0] + ":" + params[1] + ":" + params[2] + ":" + params[3] + ":" + params[4] + ":" + params[5] + ":" + params[6]);

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    class getMarkets extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            out = MainActivity.out;
            in = MainActivity.in;

            out.println("CL:" + "Markets");
            String markets = "";
            try {
                markets = in.readLine();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return markets;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}