package com.example.todolist.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.model.Task
import com.example.todolist.model.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    val tasks = repository.allTasks

    fun addTask(title: String) = viewModelScope.launch {
        repository.addTask(Task(title = title))
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        repository.updateTask(task.copy(isCompleted = !task.isCompleted))
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.deleteTask(task)
    }
}