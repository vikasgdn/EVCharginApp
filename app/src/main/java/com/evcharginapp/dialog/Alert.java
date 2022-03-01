package com.evcharginapp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.cardview.widget.CardView;

import com.evcharginapp.MainActivity;
import com.evcharginapp.R;
import com.evcharginapp.SplashActivity;
import com.evcharginapp.apppreferences.AppPreference;
import com.evcharginapp.ui.activity.LoginActivity;
import com.evcharginapp.ui.activity.RegisterActivity;


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
    public static void messageDialogWithYesNo(final Activity activity, String meesage) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_confirm_na);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (((float) activity.getResources().getDisplayMetrics().widthPixels) - activity.getResources().getDimension(R.dimen.d_10dp));
        dialog.getWindow().setAttributes(layoutParams);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        TextView textView = (TextView) dialog.findViewById(R.id.tv_dialog_message);
        //textView.setText(""+meesage);
        try {
            dialog.findViewById(R.id.tv_yes).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    AppPreference.INSTANCE.clearPreferences();
                    Intent intent=new Intent(activity, SplashActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.tv_no).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.show();
    }
    public static void messageDialogWithYesNoExit(final Activity activity, String meesage) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_confirm_na);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (((float) activity.getResources().getDisplayMetrics().widthPixels) - activity.getResources().getDimension(R.dimen.d_10dp));
        dialog.getWindow().setAttributes(layoutParams);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        TextView textView = (TextView) dialog.findViewById(R.id.tv_dialog_message);
        textView.setText("Do You want to Exit from App?");
        try {
            dialog.findViewById(R.id.tv_yes).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent a = new Intent(Intent.ACTION_MAIN);
                    a.addCategory(Intent.CATEGORY_HOME);
                    a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(a);
                    activity.finish();
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.tv_no).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.show();
    }
    public static void showLogoutAlertDialogNewDesgin(final Context context) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_logout_one_device);
        dialog.setCancelable(false);

        dialog.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(context, LoginActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent2);
                if (context instanceof MainActivity)
                    ((MainActivity)context).finish();
                else if(context instanceof LoginActivity)
                    ((LoginActivity)context).finish();
                else if(context instanceof RegisterActivity)
                    ((RegisterActivity)context).finish();
            }
        });

        dialog.show();

    }

    private void finishActivityContext(Context context)
    {
        if (context instanceof MainActivity)
            ((MainActivity)context).finish();
        else if(context instanceof LoginActivity)
            ((LoginActivity)context).finish();
        else if(context instanceof RegisterActivity)
            ((RegisterActivity)context).finish();


    }
}
