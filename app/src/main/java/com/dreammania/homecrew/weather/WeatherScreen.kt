package com.dreammania.homecrew.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dreammania.homecrew.ui.theme.CardBrush
import com.dreammania.homecrew.ui.theme.TextColor
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeatherForecastScreen(weatherViewModel: WeatherViewModel) {
    val forecastItems = weatherViewModel.forecastItems
    val sunrise = weatherViewModel.sunrise
    val sunset = weatherViewModel.sunset

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Weersverwachting",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextColor
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Sunrise, Sunset and Day Length
        if (sunrise != 0L && sunset != 0L) {
            SunInfoCard(sunrise, sunset)
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (forecastItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(forecastItems) { item ->
                    ForecastCard(item)
                }
            }
        }
    }
}

@Composable
fun SunInfoCard(sunrise: Long, sunset: Long) {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    val sunriseStr = sdf.format(Date(sunrise * 1000))
    val sunsetStr = sdf.format(Date(sunset * 1000))
    
    val durationMillis = (sunset - sunrise) * 1000
    val hours = durationMillis / (1000 * 60 * 60)
    val minutes = (durationMillis / (1000 * 60)) % 60

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)) // Light yellow
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.WbSunny, contentDescription = null, tint = Color(0xFFFBC02D))
                Text("Opkomst", fontSize = 12.sp)
                Text(sunriseStr, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.LightMode, contentDescription = null, tint = Color(0xFFFBC02D))
                Text("Lichturen", fontSize = 12.sp)
                Text("${hours}u ${minutes}m", fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.WbTwilight, contentDescription = null, tint = Color(0xFFF57F17))
                Text("Ondergang", fontSize = 12.sp)
                Text(sunsetStr, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ForecastCard(item: ForecastItem) {
    val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(item.dtTxt)
    val dayName = SimpleDateFormat("EEEE d MMMM", Locale.getDefault()).format(date ?: Date())
    
    val clouds = item.clouds?.all ?: 0
    val sunStatus = when {
        clouds <= 20 -> "Zonnig"
        clouds <= 50 -> "Weinig zon"
        else -> "Geen zon"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.background(CardBrush)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = dayName.replaceFirstChar { it.uppercase() },
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextColor
                        )
                        Text(
                            text = item.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "",
                            fontSize = 14.sp,
                            color = TextColor.copy(alpha = 0.7f)
                        )
                    }
                    Text(
                        text = "${item.main.temp.toInt()}°C",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = Color(0xFF0288D1)
                    )
                }
                
                Divider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = Color.LightGray)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Air, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${item.wind?.speed ?: 0.0} m/s", fontSize = 12.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.WbSunny, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFFBC02D))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(sunStatus, fontSize = 12.sp)
                    }
                    Text("Bewolking: $clouds%", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}
