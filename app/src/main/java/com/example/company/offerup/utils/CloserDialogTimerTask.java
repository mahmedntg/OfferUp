package com.example.company.offerup.utils;

import android.app.ProgressDialog;

import java.util.TimerTask;

/**
 * Created by Mohamed Sayed on 10/21/2017.
 */

public class CloserDialogTimerTask extends TimerTask {
    private ProgressDialog progressDialog;

    public CloserDialogTimerTask(ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    @Override
    public void run() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
