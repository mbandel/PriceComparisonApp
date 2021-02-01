package com.pc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.pc.activity.LoginRegisterActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static boolean isFutureDate(String promotionDate) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date promotion;
        try {
            promotion = dateFormat.parse(promotionDate);
            if (!now.after(promotion)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException ignored) {}
        return false;
    }

    public static void signOut(Activity activity) {
        Intent intent = new Intent(activity.getApplicationContext(), LoginRegisterActivity.class);
        activity.finishAffinity();
        activity.startActivity(intent);
        Toast.makeText(activity.getApplicationContext(), "Wylogowano pomyślnie", Toast.LENGTH_SHORT).show();
    }
}
