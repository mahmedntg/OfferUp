package com.example.company.offerup.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Mohamed Sayed on 10/21/2017.
 */

public class MyAlertDialog {
    public static AlertDialog createAlertDialog(Context context, String title) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        return alertDialogBuilder.create();
    }
}
