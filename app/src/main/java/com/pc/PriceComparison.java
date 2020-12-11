package com.pc;

import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

public class PriceComparison {

    public static Snackbar createSnackbar(View view, String message){
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("OK", v -> {});
        snackbar.setTextColor(view.getResources().getColor(R.color.colorBlack));
        snackbar.setActionTextColor(view.getResources().getColor(R.color.colorGreen));
        snackbar.setDuration(5000);
        View snackBarView = snackbar.getView();
        snackBarView.setBackground(view.getResources().getDrawable(R.drawable.snackbar_bg));
        TextView textView = snackBarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setMaxLines(5);
        return snackbar;
    }
}
