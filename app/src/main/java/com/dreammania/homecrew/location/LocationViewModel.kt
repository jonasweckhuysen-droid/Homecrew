package com.dreammania.homecrew.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserLocation(
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val lastUpdated: Long = 0L
)

class LocationViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance().getReference("user_locations")
    private val auth = FirebaseAuth.getInstance()

    private val _userLocations = MutableStateFlow<List<UserLocation>>(emptyList())
    val userLocations: StateFlow<List<UserLocation>> = _userLocations.asStateFlow()

    init {
        listenToLocations()
    }

    private fun listenToLocations() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<UserLocation>()
                for (child in snapshot.children) {
                    val location = child.getValue(UserLocation::class.java)
                    if (location != null) {
                        list.add(location)
                    }
                }
                _userLocations.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("LocationViewModel", "Firebase error: ${error.message}")
            }
        })
    }

    @SuppressLint("MissingPermission")
    fun updateMyLocation(context: Context) {
        val userId = auth.currentUser?.uid ?: return
        val userName = auth.currentUser?.displayName ?: "Onbekend"
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val data = UserLocation(
                        name = userName,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        lastUpdated = System.currentTimeMillis()
                    )
                    database.child(userId).setValue(data)
                }
            }
    }
}
