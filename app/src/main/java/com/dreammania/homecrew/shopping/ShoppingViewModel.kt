package com.dreammania.homecrew.shopping

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Villa
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ShoppingViewModel : ViewModel() {

    // Gebruik de gecorrigeerde URL voor de database
    private val database = FirebaseDatabase.getInstance("https://homecrew-d8e21-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("shopping_lists")
    
    private val _shoppingLists = MutableStateFlow<List<ShoppingList>>(listOf(
        ShoppingList("Vinegard Mansion", Icons.Default.Home),
        ShoppingList("Chateau Trois Champs", Icons.Default.Villa)
    ))
    val shoppingLists: StateFlow<List<ShoppingList>> = _shoppingLists.asStateFlow()

    init {
        listenToFirebase()
    }

    private fun listenToFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    Log.d("ShoppingVM", "Database leeg, standaard structuur voorbereiden")
                    // Alleen de namen instellen zodat de lijsten verschijnen
                    _shoppingLists.value.forEach { list ->
                        database.child(list.name).child("name").setValue(list.name)
                        database.child(list.name).child("isExpanded").setValue(true)
                    }
                    return
                }

                val updatedLists = mutableListOf<ShoppingList>()
                for (listSnapshot in snapshot.children) {
                    val listName = listSnapshot.key ?: continue
                    val isExpanded = listSnapshot.child("isExpanded").getValue(Boolean::class.java) ?: true
                    
                    val items = mutableListOf<ShoppingItem>()
                    listSnapshot.child("items").children.forEach { itemSnapshot ->
                        val itemName = itemSnapshot.child("name").getValue(String::class.java) ?: ""
                        val isChecked = itemSnapshot.child("isChecked").getValue(Boolean::class.java) ?: false
                        val order = itemSnapshot.child("order").getValue(Int::class.java) ?: 0
                        val itemId = itemSnapshot.key ?: ""
                        
                        items.add(ShoppingItem(itemId, itemName, isChecked, order))
                    }
                    
                    val icon = if (listName.contains("Chateau", true)) Icons.Default.Villa else Icons.Default.Home
                    updatedLists.add(ShoppingList(listName, icon, items.sortedBy { it.order }, isExpanded))
                }
                
                Log.d("ShoppingVM", "Gegevens succesvol geladen")
                _shoppingLists.value = updatedLists
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ShoppingVM", "Firebase Fout: ${error.message}")
            }
        })
    }

    fun onItemCheckedChanged(listIndex: Int, itemIndex: Int, isChecked: Boolean) {
        val lists = _shoppingLists.value
        if (listIndex in lists.indices && itemIndex in lists[listIndex].items.indices) {
            val list = lists[listIndex]
            val item = list.items[itemIndex]
            database.child(list.name).child("items").child(item.id).child("isChecked").setValue(isChecked)
        }
    }

    fun onAddItem(listIndex: Int, newItemName: String) {
        val lists = _shoppingLists.value
        if (listIndex in lists.indices) {
            val list = lists[listIndex]
            val itemRef = database.child(list.name).child("items").push()
            
            val maxOrder = list.items.maxOfOrNull { it.order } ?: -1
            
            val itemData = mapOf(
                "name" to newItemName,
                "isChecked" to false,
                "order" to maxOrder + 1
            )
            
            itemRef.setValue(itemData)
        }
    }

    fun onMoveItem(listIndex: Int, fromIndex: Int, toIndex: Int) {
        val lists = _shoppingLists.value
        if (listIndex !in lists.indices) return
        
        val list = lists[listIndex]
        val items = list.items.toMutableList()
        
        if (fromIndex !in items.indices || toIndex !in items.indices) return
        
        val movedItem = items.removeAt(fromIndex)
        items.add(toIndex, movedItem)
        
        // Update order values in Firebase
        val updates = mutableMapOf<String, Any>()
        items.forEachIndexed { index, item ->
            updates["${list.name}/items/${item.id}/order"] = index
        }
        database.updateChildren(updates)
    }

    fun onDeleteCheckedItems(listIndex: Int) {
        val lists = _shoppingLists.value
        if (listIndex in lists.indices) {
            val list = lists[listIndex]
            list.items.forEach { item ->
                if (item.isChecked) {
                    database.child(list.name).child("items").child(item.id).removeValue()
                }
            }
        }
    }

    fun onToggleExpand(listIndex: Int) {
        val lists = _shoppingLists.value
        if (listIndex in lists.indices) {
            val list = lists[listIndex]
            database.child(list.name).child("isExpanded").setValue(!list.isExpanded)
        }
    }
}
