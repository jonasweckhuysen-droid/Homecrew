package com.dreammania.homecrew.agenda

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreammania.homecrew.ui.theme.EventColors
import com.dreammania.homecrew.ui.theme.HomecrewTheme
import com.dreammania.homecrew.ui.theme.TextColor
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AgendaScreen(agendaViewModel: AgendaViewModel = viewModel()) {
    val events by agendaViewModel.events.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    
    LaunchedEffect(Unit) {
        agendaViewModel.fetchCalendar("https://calendar.google.com/calendar/ical/family16389375851516316441%40group.calendar.google.com/public/basic.ics")
    }

    val calendar = Calendar.getInstance()
    val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    val dayOfMonthFormat = SimpleDateFormat("d MMMM", Locale.getDefault())

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            items(14) { day ->
                val date = calendar.clone() as Calendar
                val dayEvents = events.filter { event ->
                    val eventCalendar = Calendar.getInstance()
                    eventCalendar.time = event.startDate
                    eventCalendar.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR) &&
                    eventCalendar.get(Calendar.YEAR) == date.get(Calendar.YEAR)
                }
                
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    // Clickable Date Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedDate = date.time
                                showDialog = true
                            }
                            .padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (day == 0) Color(0xFF0288D1) else Color.LightGray)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (day == 0) "Vandaag" else dayOfWeekFormat.format(date.time).replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (day == 0) Color(0xFF0288D1) else TextColor,
                                fontSize = 20.sp
                            )
                            Text(
                                text = dayOfMonthFormat.format(date.time),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextColor.copy(alpha = 0.6f)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_input_add),
                            contentDescription = "Toevoegen",
                            tint = Color.LightGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    if (dayEvents.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(start = 22.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.5f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "Tik om een afspraak toe te voegen",
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = TextColor.copy(alpha = 0.4f)
                            )
                        }
                    } else {
                        dayEvents.forEachIndexed { index, event ->
                            val color = EventColors[index % EventColors.size]
                            FancyEventCard(event = event, color = color)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        if (showDialog && selectedDate != null) {
            AddEventDialog(
                onDismiss = { showDialog = false },
                onSave = { summary, start, end ->
                    agendaViewModel.addEvent(summary, start, end)
                    showDialog = false
                },
                date = selectedDate!!
            )
        }
    }
}

@Composable
fun AddEventDialog(onDismiss: () -> Unit, onSave: (String, Date, Date) -> Unit, date: Date) {
    var summary by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("12:00") }
    var endTime by remember { mutableStateOf("13:00") }
    val displayDate = SimpleDateFormat("EEEE d MMMM", Locale.getDefault()).format(date)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nieuwe afspraak", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(displayDate, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Onderwerp") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Van (HH:mm)") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("Tot (HH:mm)") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (summary.isNotBlank()) {
                    val cal = Calendar.getInstance()
                    cal.time = date
                    
                    try {
                        val startParts = startTime.split(":")
                        val startCal = cal.clone() as Calendar
                        startCal.set(Calendar.HOUR_OF_DAY, startParts[0].toInt())
                        startCal.set(Calendar.MINUTE, startParts[1].toInt())

                        val endParts = endTime.split(":")
                        val endCal = cal.clone() as Calendar
                        endCal.set(Calendar.HOUR_OF_DAY, endParts[0].toInt())
                        endCal.set(Calendar.MINUTE, endParts[1].toInt())

                        onSave(summary, startCal.time, endCal.time)
                    } catch (e: Exception) {
                        // Ongeldige tijd invoer, niets doen of melding tonen
                    }
                }
            }) { Text("Opslaan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuleren") }
        }
    )
}

@Composable
fun FancyEventCard(event: CalendarEvent, color: Color) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth().padding(start = 22.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .background(Brush.horizontalGradient(listOf(color.copy(alpha = 0.8f), color.copy(alpha = 0.4f))))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp)).padding(8.dp)
            ) {
                Text(timeFormat.format(event.startDate), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                Box(modifier = Modifier.height(1.dp).width(20.dp).background(Color.White))
                Text(timeFormat.format(event.endDate), fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(event.summary, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                Text("Duur: ${getDuration(event.startDate, event.endDate)}", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
            }
        }
    }
}

fun getDuration(start: Date, end: Date): String {
    val diff = end.time - start.time
    val hours = diff / (1000 * 60 * 60)
    val minutes = (diff / (1000 * 60)) % 60
    return if (hours > 0) "${hours}u ${minutes}m" else "${minutes}m"
}
