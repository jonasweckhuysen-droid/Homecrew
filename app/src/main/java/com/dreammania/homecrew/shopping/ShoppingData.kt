package com.dreammania.homecrew.shopping

import androidx.compose.ui.graphics.vector.ImageVector

data class ShoppingItem(
    val id: String = "",
    val name: String = "",
    val isChecked: Boolean = false,
    val order: Int = 0
)

data class ShoppingList(
    val name: String = "",
    val icon: ImageVector,
    val items: List<ShoppingItem> = emptyList(),
    val isExpanded: Boolean = true
)
