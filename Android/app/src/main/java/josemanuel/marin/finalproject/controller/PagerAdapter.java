package josemanuel.marin.finalproject.controller;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import josemanuel.marin.finalproject.view.AddOffer;
import josemanuel.marin.finalproject.fragments.ImageFragment;
import josemanuel.marin.finalproject.fragments.LocationFragment;
import josemanuel.marin.finalproject.fragments.PriceFragment;
import josemanuel.marin.finalproject.fragments.TagsFragment;

public class PagerAdapter extends FragmentStateAdapter {

    public PagerAdapter(AddOffer addOffer) {
        super(addOffer);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new TagsFragment();
            case 1:
                return new PriceFragment();
            case 2:
                return new ImageFragment();
            case 3:
                return new LocationFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}