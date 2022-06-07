package josemanuel.marin.finalproject.view;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import josemanuel.marin.finalproject.R;
import josemanuel.marin.finalproject.controller.PagerAdapter;

public class AddOffer extends AppCompatActivity {
    TabLayout tabLayout;
    Button buttonNext, buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offer);

        tabLayout = findViewById(R.id.tabLayout);
        buttonNext = findViewById(R.id.buttonNext);
        buttonBack = findViewById(R.id.buttonBack);

        buttonBack.setVisibility(View.GONE);

        ViewPager2 viewPager = findViewById(R.id.pager);
        PagerAdapter adapter = new PagerAdapter(this);
        viewPager.setAdapter(adapter);

        viewPager.setUserInputEnabled(false);

        LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }

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

        buttonNext.setOnClickListener(v -> {
            tabLayout.selectTab(tabLayout.getTabAt(tabLayout.getSelectedTabPosition() + 1));
            if (tabLayout.getSelectedTabPosition() >= 1) {
                buttonBack.setVisibility(View.VISIBLE);
            } else {
                buttonBack.setVisibility(View.GONE);
            }

            if (tabLayout.getSelectedTabPosition() == tabLayout.getTabCount() - 1) {
                buttonNext.setVisibility(View.GONE);
            }
        });

        buttonBack.setOnClickListener(v -> {
            tabLayout.selectTab(tabLayout.getTabAt(tabLayout.getSelectedTabPosition() - 1));

            if (tabLayout.getSelectedTabPosition() < tabLayout.getTabCount() - 1) {
                buttonNext.setVisibility(View.VISIBLE);
            } else {
                buttonNext.setVisibility(View.GONE);
            }

            if (tabLayout.getSelectedTabPosition() == 0) {
                buttonBack.setVisibility(View.GONE);
            }
        });

    }
}