package com.dreammania.homecrew.emergency

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Date

class EmergencyViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance().getReference("emergencies")
    private val auth = FirebaseAuth.getInstance()

    @SuppressLint("MissingPermission")
    fun sendEmergencySignal(context: Context) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location: Location? ->
                val userName = auth.currentUser?.displayName ?: "Onbekende gebruiker"
                val timestamp = Date().time
                val lat = location?.latitude ?: 0.0
                val lng = location?.longitude ?: 0.0

                val emergencyData = mapOf(
                    "userName" to userName,
                    "timestamp" to timestamp,
                    "latitude" to lat,
                    "longitude" to lng,
                    "active" to true
                )

                val key = database.push().key
                if (key != null) {
                    database.child(key).setValue(emergencyData)
                        .addOnSuccessListener {
                            Log.d("EmergencyViewModel", "Emergency signal sent to Firebase")
                        }
                        .addOnFailureListener {
                            Log.e("EmergencyViewModel", "Failed to send emergency signal", it)
                        }
                }
            }
            .addOnFailureListener {
                Log.e("EmergencyViewModel", "Failed to get location", it)
                // Fallback: send without location if it fails
                sendEmergencyWithoutLocation()
            }
    }

    private fun sendEmergencyWithoutLocation() {
        val userName = auth.currentUser?.displayName ?: "Onbekende gebruiker"
        val timestamp = Date().time
        val emergencyData = mapOf(
            "userName" to userName,
            "timestamp" to timestamp,
            "latitude" to 0.0,
            "longitude" to 0.0,
            "active" to true
        )
        database.push().setValue(emergencyData)
    }
}
