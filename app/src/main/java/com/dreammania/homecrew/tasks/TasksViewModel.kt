package com.dreammania.homecrew.tasks

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.dreammania.homecrew.ui.theme.ButtonBlue
import com.dreammania.homecrew.ui.theme.ButtonGreen
import com.dreammania.homecrew.ui.theme.ButtonRed
import com.dreammania.homecrew.ui.theme.ButtonYellow
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Task(val id: String, val text: String, val assignedTo: User, var isCompleted: Boolean = false)
data class User(val name: String, val color: Color)

class TasksViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance("https://homecrew-d8e21-default-rtdb.europe-west1.firebasedatabase.app/")
        .getReference("tasks")

    private val _users = MutableStateFlow(listOf(
        User("Jonas", ButtonBlue),
        User("Partner", ButtonGreen),
        User("Kid 1", ButtonRed),
        User("Kid 2", ButtonYellow)
    ))
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    init {
        listenToFirebase()
    }

    private fun listenToFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedTasks = mutableListOf<Task>()
                for (taskSnapshot in snapshot.children) {
                    val id = taskSnapshot.key ?: continue
                    val text = taskSnapshot.child("text").getValue(String::class.java) ?: ""
                    val assignedToName = taskSnapshot.child("assignedTo").getValue(String::class.java) ?: ""
                    val isCompleted = taskSnapshot.child("isCompleted").getValue(Boolean::class.java) ?: false
                    
                    val assignedTo = _users.value.find { it.name == assignedToName } ?: _users.value.first()
                    updatedTasks.add(Task(id, text, assignedTo, isCompleted))
                }
                _tasks.value = updatedTasks
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TasksVM", "Firebase error: ${error.message}")
            }
        })
    }

    fun addTask(text: String, assignedTo: User) {
        val taskRef = database.push()
        val taskData = mapOf(
            "text" to text,
            "assignedTo" to assignedTo.name,
            "isCompleted" to false
        )
        taskRef.setValue(taskData)
    }

    fun toggleTaskCompletion(task: Task) {
        database.child(task.id).child("isCompleted").setValue(!task.isCompleted)
    }

    fun removeCompletedTasks() {
        _tasks.value.forEach { task ->
            if (task.isCompleted) {
                database.child(task.id).removeValue()
            }
        }
    }
}
