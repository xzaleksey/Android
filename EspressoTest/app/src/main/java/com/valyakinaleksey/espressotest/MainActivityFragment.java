package com.valyakinaleksey.espressotest;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private int width;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        width = getWidth();
        final TextView textViewFront = (TextView) view.findViewById(R.id.textFront);
        final TextView textViewBack = (TextView) view.findViewById(R.id.textBack);
        view.findViewById(R.id.btn_change_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ObjectAnimator outAnim = ObjectAnimator.ofFloat(textViewFront, "x", width / 2 - textViewFront.getWidth() / 2, -width);
                outAnim.setDuration(1000);
                textViewBack.setVisibility(View.VISIBLE);
                ObjectAnimator inAnim = ObjectAnimator.ofFloat(textViewBack, "x", width, width / 2 - textViewFront.getWidth() / 2);
                inAnim.setDuration(1000);
                outAnim.start();
                inAnim.start();
            }
        });
        return view;
    }

    private int getWidth() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }
}
