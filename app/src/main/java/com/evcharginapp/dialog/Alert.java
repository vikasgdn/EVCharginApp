package com.evcharginapp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.cardview.widget.CardView;

import com.evcharginapp.R;


public class Alert {
    public static void show(Activity activity, String title, String message) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(1);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);
        dialog.getWindow().setBackgroundDrawable((Drawable) null);
        ((TextView) dialog.findViewById(R.id.txt_heading)).setText(title);
        ((TextView) dialog.findViewById(R.id.txt_message)).setText(message);
        ((CardView) dialog.findViewById(R.id.okBtn)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(-1, -2);
    }

    public static void show(Activity activity, String title, String message, final DialogInterface.OnClickListener listener) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(1);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);
        dialog.getWindow().setBackgroundDrawable((Drawable) null);
        ((TextView) dialog.findViewById(R.id.txt_heading)).setText(title);
        ((TextView) dialog.findViewById(R.id.txt_message)).setText(message);
        ((CardView) dialog.findViewById(R.id.okBtn)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                listener.onClick(dialog, 0);
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(-1, -2);
    }

}
