package com.dreammania.homecrew.shopping

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreammania.homecrew.ui.theme.HomecrewTheme
import com.dreammania.homecrew.ui.theme.TextColor
import androidx.compose.runtime.key
import sh.calvin.reorderable.ReorderableColumn
import sh.calvin.reorderable.ReorderableItem

@Composable
fun ShoppingScreen(shoppingViewModel: ShoppingViewModel = viewModel()) {
    val shoppingLists by shoppingViewModel.shoppingLists.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Boodschappen",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = TextColor,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        if (shoppingLists.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                itemsIndexed(shoppingLists) { listIndex, list ->
                    ShoppingListCard(
                        list = list,
                        onToggleExpand = { shoppingViewModel.onToggleExpand(listIndex) },
                        onItemCheckedChanged = { itemIndex, isChecked ->
                            shoppingViewModel.onItemCheckedChanged(listIndex, itemIndex, isChecked)
                        },
                        onAddItem = { itemName ->
                            shoppingViewModel.onAddItem(listIndex, itemName)
                        },
                        onDeleteChecked = {
                            shoppingViewModel.onDeleteCheckedItems(listIndex)
                        },
                        onMoveItem = { from, to ->
                            shoppingViewModel.onMoveItem(listIndex, from, to)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ShoppingListCard(
    list: ShoppingList,
    onToggleExpand: () -> Unit,
    onItemCheckedChanged: (Int, Boolean) -> Unit,
    onAddItem: (String) -> Unit,
    onDeleteChecked: () -> Unit,
    onMoveItem: (Int, Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = list.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = list.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextColor
                    )
                    Text(
                        text = "${list.items.size} items",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Icon(
                    imageVector = if (list.isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.LightGray
                )
            }

            AnimatedVisibility(visible = list.isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 20.dp)
                ) {
                    HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    val haptic = LocalHapticFeedback.current
                    ReorderableColumn(
                        list = list.items,
                        onSettle = { from, to ->
                            onMoveItem(from, to)
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { _, item, isDragging ->
                        key(item.id) {
                            val elevation by animateDpAsState(if (isDragging) 8.dp else 0.dp, label = "elevation")
                            Surface(
                                shadowElevation = elevation,
                                color = if (isDragging) Color(0xFFF5F5F5) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                ShoppingItemRow(
                                    item = item,
                                    onCheckedChange = { isChecked -> 
                                        val currentItemIndex = list.items.indexOfFirst { it.id == item.id }
                                        if (currentItemIndex != -1) {
                                            onItemCheckedChanged(currentItemIndex, isChecked)
                                        }
                                    },
                                    modifier = Modifier.longPressDraggableHandle(
                                        onDragStarted = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        }
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    var newItemName by remember { mutableStateOf("") }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = newItemName,
                            onValueChange = { newItemName = it },
                            placeholder = { Text("Nieuw item...", fontSize = 14.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF8F8F8),
                                unfocusedContainerColor = Color(0xFFF8F8F8),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        IconButton(
                            onClick = {
                                if (newItemName.isNotBlank()) {
                                    onAddItem(newItemName)
                                    newItemName = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                        }
                    }

                    if (list.items.any { it.isChecked }) {
                        TextButton(
                            onClick = onDeleteChecked,
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Gekochte items verwijderen", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShoppingItemRow(
    item: ShoppingItem, 
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.DragHandle,
            contentDescription = "Sleep om te verplaatsen",
            tint = Color.LightGray,
            modifier = Modifier.size(24.dp).padding(4.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyLarge,
            color = if (item.isChecked) Color.Gray else TextColor,
            textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingScreenPreview() {
    HomecrewTheme {
        ShoppingScreen()
    }
}
