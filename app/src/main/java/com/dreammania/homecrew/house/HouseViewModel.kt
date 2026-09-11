package com.dreammania.homecrew.house

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HouseViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance("https://homecrew-d8e21-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("house_management")

    private val _houseItems = MutableStateFlow<List<HouseItem>>(emptyList())
    val houseItems: StateFlow<List<HouseItem>> = _houseItems.asStateFlow()

    init {
        listenToFirebase()
    }

    private fun listenToFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedItems = mutableListOf<HouseItem>()
                for (itemSnapshot in snapshot.children) {
                    val id = itemSnapshot.key ?: continue
                    val name = itemSnapshot.child("name").getValue(String::class.java) ?: ""
                    val type = itemSnapshot.child("type").getValue(String::class.java) ?: ""
                    val lastMaintained = itemSnapshot.child("lastMaintained").getValue(String::class.java) ?: ""
                    val nextMaintenance = itemSnapshot.child("nextMaintenance").getValue(String::class.java) ?: ""
                    
                    updatedItems.add(HouseItem(id, name, type, lastMaintained, nextMaintenance))
                }
                _houseItems.value = updatedItems
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HouseVM", "Firebase error: ${error.message}")
            }
        })
    }

    fun addOrUpdateItem(id: String?, name: String, type: String, lastMaintained: String, nextMaintenance: String) {
        val itemRef = if (id == null) database.push() else database.child(id)
        val itemData = mapOf(
            "name" to name,
            "type" to type,
            "lastMaintained" to lastMaintained,
            "nextMaintenance" to nextMaintenance
        )
        itemRef.setValue(itemData)
    }

    fun deleteItem(id: String) {
        database.child(id).removeValue()
    }
}
