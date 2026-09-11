package com.dreammania.homecrew.house

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreammania.homecrew.ui.theme.ButtonTeal
import com.dreammania.homecrew.ui.theme.CardBrush
import com.dreammania.homecrew.ui.theme.TextColor

data class HouseItem(
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val lastMaintained: String = "",
    val nextMaintenance: String = ""
)

@Composable
fun HouseScreen(viewModel: HouseViewModel = viewModel()) {
    val houseItems by viewModel.houseItems.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<HouseItem?>(null) }

    if (showDialog) {
        HouseItemDialog(
            item = selectedItem,
            onDismiss = { 
                showDialog = false
                selectedItem = null
            },
            onConfirm = { name, type, last, next ->
                viewModel.addOrUpdateItem(selectedItem?.id, name, type, last, next)
                showDialog = false
                selectedItem = null
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Huisbeheer",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextColor
            )
            FloatingActionButton(
                onClick = { 
                    selectedItem = null
                    showDialog = true 
                },
                containerColor = ButtonTeal,
                contentColor = Color.White,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Toevoegen")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (houseItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Geen items gevonden. Voeg er een toe!", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(houseItems, key = { it.id }) { item ->
                    HouseItemCard(
                        item = item,
                        onClick = {
                            selectedItem = item
                            showDialog = true
                        },
                        onDelete = {
                            viewModel.deleteItem(item.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HouseItemDialog(
    item: HouseItem?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(item?.name ?: "") }
    var type by remember { mutableStateOf(item?.type ?: "") }
    var last by remember { mutableStateOf(item?.lastMaintained ?: "") }
    var next by remember { mutableStateOf(item?.nextMaintenance ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item == null) "Nieuw Item" else "Item Aanpassen") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Naam") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = last, onValueChange = { last = it }, label = { Text("Laatst Onderhouden") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = next, onValueChange = { next = it }, label = { Text("Volgend Onderhoud") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(name, type, last, next) }) {
                Text("Opslaan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuleren")
            }
        }
    )
}

@Composable
fun HouseItemCard(item: HouseItem, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.background(CardBrush).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = ButtonTeal.copy(alpha = 0.2f)
                ) {
                    Icon(
                        imageVector = Icons.Default.HomeWork,
                        contentDescription = null,
                        tint = ButtonTeal,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextColor
                    )
                    Text(
                        text = "Type: ${item.type}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextColor.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        Text(
                            text = "Laatst: ${item.lastMaintained}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Volgende: ${item.nextMaintenance}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
                
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Verwijderen", tint = Color.Gray)
                }
            }
        }
    }
}
