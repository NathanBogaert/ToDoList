package com.example.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolist.ui.theme.ToDoListTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoListTheme {
                val showCreateDialog = remember { mutableStateOf(false) }
                val showSortingDialog = remember { mutableStateOf(false) }
                var comparator by remember { mutableStateOf(nameComparator) }
                val comparatorName = remember { mutableStateOf("Name") }

                val taskList = remember { mutableStateListOf(
                    TaskInfo("Task 1"),
                    TaskInfo("ALO", "Small description"),
                    TaskInfo("Task 3", "Long description. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed tincidunt blandit nunc ac ultricies. Nunc congue orci vitae tincidunt maximus. Morbi pellentesque, dui non dapibus consequat, lectus lacus hendrerit eros, id dictum sapien nisl nec risus. Sed vel posuere ipsum, at bibendum tortor. Nullam commodo feugiat enim eget scelerisque. Etiam maximus est at erat vulputate facilisis. Nunc id vehicula purus. Phasellus ut diam sapien. Vestibulum sollicitudin risus arcu. Donec orci lacus, tempus vestibulum aliquet a, tempus vel purus. Maecenas dapibus sodales turpis, at vulputate purus malesuada ac. ")
                ) }

                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary
                            ),
                            title = { Text(text = "My TODO List") },
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { showCreateDialog.value = true },
                            modifier = Modifier.background(MaterialTheme.colorScheme.background)
                        ) {
                            Icon(
                                painter = rememberVectorPainter(image = Icons.Default.Add),
                                contentDescription = null,
                                modifier = Modifier.clickable { showCreateDialog.value = true }
                            )
                        }
                    }
                ) {
                    if (showCreateDialog.value) {
                        Card {
                            AlertCreateTask(taskList, showCreateDialog) {}
                        }
                    }
                    Column(
                        modifier = Modifier.padding(it)
                    ) {
                        Card(
                            onClick = { showSortingDialog.value = !showSortingDialog.value },
                            modifier = Modifier.padding(horizontal = 15.dp, vertical = 8.dp)
                                .width(IntrinsicSize.Max)
                        ) {
                            Text(text = "Sort by: ${comparatorName.value}", modifier = Modifier.padding(10.dp))
                            if (showSortingDialog.value) {
                                SortingDropDownMenu(
                                    expanded = showSortingDialog,
                                    comparatorName = comparatorName
                                ) { selectedComparator ->
                                    comparator = selectedComparator
                                }
                            }
                        }
                        LazyColumn(
                            state = rememberLazyListState()
                        ) {
                            val sortedList = mutableStateOf(taskList.sortedWith(comparator))
                            items(sortedList.value) { task ->
                                Task(task, onDeleteTask = { taskList.remove(task) })
                            }
                        }
                    }
                }
            }
        }
    }
}

private val nameComparator = Comparator<TaskInfo> { left, right ->
    left.name.compareTo(right.name)
}
private val doneComparator = Comparator<TaskInfo> { left, right ->
    left.isDone.value.compareTo(right.isDone.value)
}

data class TaskInfo(
    var name: String,
    var description: String? = null,
    var isDone: MutableState<Boolean> = mutableStateOf(false)
)

@Composable
fun Task(taskInfo: TaskInfo, onDeleteTask: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 8.dp)
            .fillMaxWidth()
            .border(1.dp, Color.Gray, RoundedCornerShape(15.dp))
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = taskInfo.name,
                    style = TextStyle(
                        textDecoration = if (taskInfo.isDone.value) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f)
                )
                Checkbox(checked = taskInfo.isDone.value, onCheckedChange = { taskInfo.isDone.value = it })
                IconButton(onClick = { onDeleteTask() }) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Default.Delete),
                        contentDescription = null,
                    )
                }
            }
            if (!taskInfo.description.isNullOrBlank()) {
                if (!taskInfo.isDone.value) {
                    Text(
                        text = taskInfo.description!!,
                        modifier = Modifier.padding(vertical = 5.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AlertCreateTask(
    taskList: MutableList<TaskInfo>,
    showAlertDialog: MutableState<Boolean>,
    onDismiss: () -> Unit)
{
    val inputName = remember { mutableStateOf("") }
    val inputNameMaxLength = 40
    val inputDescriptionMaxLength = 254
    val inputDescription = remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    AlertDialog(
        title = { Text("Create Task") },
        onDismissRequest = { onDismiss() },
        text = {
            Column {
                TextField(
                    value = inputName.value,
                    onValueChange = { if (it.length <= inputNameMaxLength) inputName.value = it },
                    modifier = Modifier.focusRequester(focusRequester),
                    label = { Text("Name") },
                    supportingText = {
                        Text(
                            text = "${inputName.value.length} / $inputNameMaxLength",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    }
                )
                Spacer(Modifier.height(25.dp))
                TextField(
                    value = inputDescription.value,
                    onValueChange = { if (it.length <= inputDescriptionMaxLength) inputDescription.value = it },
                    label = { Text("Description") },
                    supportingText = {
                        Text(
                            text = "${inputDescription.value.length} / $inputDescriptionMaxLength",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                taskList.add(TaskInfo(inputName.value, if (inputDescription.value == "") null else inputDescription.value))
                showAlertDialog.value = false
            }) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = { showAlertDialog.value = false }) {
                Text("Cancel")
            }
        }
    )

    // Focus the TextField when the dialog is shown
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun SortingDropDownMenu(
    expanded: MutableState<Boolean>,
    comparatorName: MutableState<String>,
    onComparatorSelected: (Comparator<TaskInfo>) -> Unit
) {
    if (expanded.value) {
        Text(
            text = "Name",
            modifier = Modifier.clickable {
                onComparatorSelected(nameComparator)
                comparatorName.value = "Name"
                expanded.value = !expanded.value
            }.padding(horizontal = 10.dp, vertical = 5.dp)
                .fillMaxWidth()
        )
        Text(
            text = "Done",
            modifier = Modifier.clickable {
                onComparatorSelected(doneComparator)
                comparatorName.value = "Done"
                expanded.value = !expanded.value
            }.padding(horizontal = 10.dp, vertical = 5.dp)
                .fillMaxWidth()
        )
    }
}