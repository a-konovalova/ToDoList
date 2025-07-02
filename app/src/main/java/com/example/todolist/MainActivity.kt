package com.example.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.todolist.model.AppDatabase
import com.example.todolist.model.Task
import com.example.todolist.model.TaskRepository
import com.example.todolist.viewModel.TaskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "tasks.db"
        ).build()

        val repository = TaskRepository(db.taskDao())

        setContent {
            val viewModel: TaskViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return TaskViewModel(repository) as T
                    }
                }
            )

            ToDoApp(viewModel)
        }
    }
}

@Composable
fun ToDoApp(viewModel: TaskViewModel) {
    var newTaskTitle by remember { mutableStateOf("") }
    val tasks by viewModel.tasks.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Поле ввода новой задачи
        Row(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = newTaskTitle,
                onValueChange = { newTaskTitle = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Добавить задачу") }
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                if (newTaskTitle.isNotBlank()) {
                    viewModel.addTask(newTaskTitle)
                    newTaskTitle = ""
                }
            }) {
                Text("+")
            }
        }

        // Список задач
        LazyColumn(Modifier.fillMaxWidth()) {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    onCheckedChange = { viewModel.updateTask(task) },
                    onDelete = { viewModel.deleteTask(task) }
                )
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onCheckedChange: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { onCheckedChange() }
        )
        Text(
            text = task.title,
            modifier = Modifier.weight(1f),
            style = if (task.isCompleted) {
                MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            } else {
                MaterialTheme.typography.bodyMedium
            }
        )
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Удалить")
        }
    }
}