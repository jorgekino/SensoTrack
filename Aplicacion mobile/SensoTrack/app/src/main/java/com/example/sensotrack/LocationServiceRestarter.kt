package com.example.sensotrack

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

class LocationServiceRestarter : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, LocationService::class.java)
        context.startForegroundService(serviceIntent)
    }
}
