package com.dreammania.homecrew.agenda

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.DtEnd
import net.fortuna.ical4j.model.property.DtStart
import net.fortuna.ical4j.model.property.Summary
import java.net.URL
import java.util.Date

data class CalendarEvent(
    val summary: String = "",
    val startDate: Date = Date(),
    val endDate: Date = Date(),
    val id: String = ""
)

class AgendaViewModel : ViewModel() {

    private val _events = MutableStateFlow<List<CalendarEvent>>(emptyList())
    val events: StateFlow<List<CalendarEvent>> = _events.asStateFlow()

    private val _firebaseEvents = MutableStateFlow<List<CalendarEvent>>(emptyList())
    private val database = FirebaseDatabase.getInstance().getReference("agenda")

    init {
        listenToFirebaseEvents()
    }

    private fun listenToFirebaseEvents() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<CalendarEvent>()
                for (child in snapshot.children) {
                    val summary = child.child("summary").getValue(String::class.java) ?: ""
                    val startTimestamp = child.child("startDate").getValue(Long::class.java) ?: 0L
                    val endTimestamp = child.child("endDate").getValue(Long::class.java) ?: 0L
                    val id = child.key ?: ""
                    list.add(CalendarEvent(summary, Date(startTimestamp), Date(endTimestamp), id))
                }
                _firebaseEvents.value = list
                combineEvents()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AgendaViewModel", "Firebase error: ${error.message}")
            }
        })
    }

    private var icalEvents: List<CalendarEvent> = emptyList()

    fun fetchCalendar(url: String) {
        viewModelScope.launch {
            try {
                val calendar = CalendarBuilder().build(URL(url).openStream()) as Calendar
                icalEvents = calendar.getComponents<VEvent>("VEVENT").mapNotNull { event ->
                    val summary = event.getProperty<Summary>("SUMMARY")?.value
                    val startDate = event.getProperty<DtStart>("DTSTART")?.date
                    val endDate = event.getProperty<DtEnd>("DTEND")?.date

                    if (summary != null && startDate != null && endDate != null) {
                        CalendarEvent(summary, startDate, endDate, "ical_${startDate.time}")
                    } else {
                        null
                    }
                }
                combineEvents()
            } catch (e: Exception) {
                Log.e("AgendaViewModel", "Error fetching or parsing calendar", e)
            }
        }
    }

    private fun combineEvents() {
        _events.value = (icalEvents + _firebaseEvents.value).sortedBy { it.startDate }
    }

    fun addEvent(summary: String, startDate: Date, endDate: Date) {
        val key = database.push().key ?: return
        val eventMap = mapOf(
            "summary" to summary,
            "startDate" to startDate.time,
            "endDate" to endDate.time
        )
        database.child(key).setValue(eventMap)
    }
}
