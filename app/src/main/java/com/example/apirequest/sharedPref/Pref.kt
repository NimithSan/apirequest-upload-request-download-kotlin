package com.example.apirequest.sharedPref

import android.content.Context

class Pref(context: Context) {

    val sharedPref = context.getSharedPreferences("myPref", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()

}