package com.dreammania.homecrew.location

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreammania.homecrew.ui.theme.ButtonBlue
import com.dreammania.homecrew.ui.theme.CardBrush
import com.dreammania.homecrew.ui.theme.TextColor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LocationScreen(locationViewModel: LocationViewModel = viewModel()) {
    val userLocations by locationViewModel.userLocations.collectAsState()
    val context = LocalContext.current

    // Set initial camera position to a default or first user's location
    val initialPos = remember(userLocations) {
        if (userLocations.isNotEmpty()) {
            LatLng(userLocations.first().latitude, userLocations.first().longitude)
        } else {
            LatLng(51.0543, 3.7174) // Default Gent, Belgium
        }
    }
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPos, 12f)
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
                text = "Wie is waar?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextColor
            )
            IconButton(onClick = { locationViewModel.updateMyLocation(context) }) {
                Icon(Icons.Default.Refresh, contentDescription = "Update mijn locatie", tint = ButtonBlue)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Map View
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = true),
                uiSettings = MapUiSettings(zoomControlsEnabled = true)
            ) {
                userLocations.forEach { user ->
                    Marker(
                        state = MarkerState(position = LatLng(user.latitude, user.longitude)),
                        title = user.name,
                        snippet = "Laatst gezien: ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(user.lastUpdated))}"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (userLocations.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Locaties worden geladen...", color = TextColor.copy(alpha = 0.5f))
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(userLocations) { userLocation ->
                    LocationCard(userLocation)
                }
            }
        }
    }
}

@Composable
fun LocationCard(userLocation: UserLocation) {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    val timeStr = sdf.format(Date(userLocation.lastUpdated))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.background(CardBrush)) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = ButtonBlue,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = userLocation.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextColor
                    )
                    Text(
                        text = "Laatst gezien: $timeStr",
                        fontSize = 14.sp,
                        color = TextColor.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
