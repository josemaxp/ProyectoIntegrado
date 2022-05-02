package josemanuel.marin.finalproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import java.io.BufferedReader;
import java.io.PrintWriter;

import josemanuel.marin.finalproject.controller.PagerAdapter;

public class AddOffer extends AppCompatActivity {
    TabLayout tabLayout;
    PrintWriter out = null;
    BufferedReader in = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offer);

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
    }
}