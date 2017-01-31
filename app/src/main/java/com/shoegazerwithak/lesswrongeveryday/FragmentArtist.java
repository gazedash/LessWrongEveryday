package com.shoegazerwithak.lesswrongeveryday;

import android.app.Fragment;
import android.content.Context;

import java.util.Map;

public class FragmentArtist extends Fragment {
    private ArtistsFragmentInteractionListener mListener;

    public FragmentArtist() {
    }

    public static FragmentArtist newInstance() {
        return new FragmentArtist();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ArtistsFragmentInteractionListener) {
            mListener = (ArtistsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener");
        }
    }

    public interface ArtistsFragmentInteractionListener {
        void onListItemClick(Map<String, String> artistItem);
    }
}


