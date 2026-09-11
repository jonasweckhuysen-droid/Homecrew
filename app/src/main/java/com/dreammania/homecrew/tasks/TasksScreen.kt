package com.dreammania.homecrew.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreammania.homecrew.ui.theme.HomecrewTheme
import com.dreammania.homecrew.ui.theme.TextColor

@Composable
fun TasksScreen(tasksViewModel: TasksViewModel = viewModel()) {
    val tasks by tasksViewModel.tasks.collectAsState()
    val users by tasksViewModel.users.collectAsState()
    var taskText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf(users.first()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Taken", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = taskText,
                onValueChange = { taskText = it },
                label = { Text("Nieuwe taak") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box {
                Button(onClick = { expanded = true }) {
                    Text(text = selectedUser.name)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    users.forEach { user ->
                        DropdownMenuItem(text = { Text(user.name) } , onClick = { 
                            selectedUser = user
                            expanded = false
                        })
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { 
                if (taskText.isNotBlank()) {
                    tasksViewModel.addTask(taskText, selectedUser)
                    taskText = ""
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Taak toevoegen")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(tasks, key = { it.id }) { task ->
                TaskRow(task = task, onTaskCompleted = { tasksViewModel.toggleTaskCompletion(it) })
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { tasksViewModel.removeCompletedTasks() }, modifier = Modifier.fillMaxWidth()) {
            Text("Verwijder voltooide taken")
        }
    }
}

@Composable
fun TaskRow(task: Task, onTaskCompleted: (Task) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = task.isCompleted, onCheckedChange = { onTaskCompleted(task) })
        Text(text = task.text, modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(task.assignedTo.color)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TasksScreenPreview() {
    HomecrewTheme {
        TasksScreen()
    }
}
