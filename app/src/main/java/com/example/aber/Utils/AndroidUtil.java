package com.example.aber.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

public class AndroidUtil {
    public static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showLoadingDialog(ProgressDialog progressDialog) {
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void hideLoadingDialog(ProgressDialog progressDialog) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
