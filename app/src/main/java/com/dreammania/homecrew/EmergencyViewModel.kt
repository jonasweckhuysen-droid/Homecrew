package com.dreammania.homecrew

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel

class EmergencyViewModel : ViewModel() {

    fun sendEmergencySignal(context: Context) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:112")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Log error if needed
        }
    }
    
    // Adding this method to match what might be called from EmergencyScreen
    fun sendEmergencyNotification() {
        // Implementation for notification if needed
    }
}
