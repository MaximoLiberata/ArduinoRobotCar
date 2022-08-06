package com.example.robotcarapp

import android.app.Activity
import android.app.AlertDialog
import android.widget.TextView

class LoadingDialog(val activity: Activity) {

    private lateinit var alertDialog: AlertDialog

    fun start(text: String?) {
        // Set view
        val inflater = activity.layoutInflater
        val dialogView = inflater.inflate(R.layout.loading_item, null)
        val txtLoadingDialog: TextView = dialogView.findViewById(R.id.txtLoadingDialog)
        txtLoadingDialog.text = text ?: "Cargando"

        // Set dialog
        val builder = AlertDialog.Builder(activity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog.show()
    }

    fun dismiss() {
        alertDialog.dismiss()
    }

}