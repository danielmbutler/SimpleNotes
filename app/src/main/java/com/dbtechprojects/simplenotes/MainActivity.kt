package com.dbtechprojects.simplenotes

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.dbtechprojects.simplenotes.database.NotesDao
import com.dbtechprojects.simplenotes.database.NotesDatabase
import com.dbtechprojects.simplenotes.models.NoteItem
import com.dbtechprojects.simplenotes.ui.theme.DragandDropDemoTheme
import com.dbtechprojects.simplenotes.ui.theme.NotesBackground
import com.dbtechprojects.simplenotes.ui.theme.NotesViewBackground
import com.dbtechprojects.simplenotes.ui.theme.Purple700
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.reflect.KFunction3

class MainActivity : ComponentActivity() {

    private lateinit var dbHandler: NotesDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHandler = NotesDatabase.provideDB(applicationContext).noteDao()
        setContent {
            DragandDropDemoTheme {
                Surface(color = MaterialTheme.colors.background) {
                    AppScaffold(dbHandler, this::addOrSaveNote, this::deleteNote)
                }
            }
        }
    }

    private fun addOrSaveNote(noteTitle: String, noteBody: String, noteId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (noteId == 0) {
                // add new note
                dbHandler.insertNote(NoteItem(title = noteTitle, note = noteBody))
            } else {
                // update note
                dbHandler.updateNote(NoteItem(id = noteId, title = noteTitle, noteBody))
            }
        }
    }

    private fun deleteNote(noteId: Int){
        lifecycleScope.launch {
            dbHandler.delete(noteId)
        }
    }
}

@Composable
fun AppScaffold(
    dbHandler: NotesDao,
    addOrSave: KFunction3<String, String, Int, Unit>,
    deleteNote: (Int) -> Unit
) {
    val notes = dbHandler.getAllNotes()
    val shouldShowNoteDialog = remember { mutableStateOf(false) }
    val shouldShowDeleteDialog = remember { mutableStateOf(false) }
    val hasBinDrawableChanged = remember { mutableStateOf(false) }
    val currentNoteTitle = remember { mutableStateOf("") }
    val currentNoteBody = remember { mutableStateOf("") }
    val currentNoteId = remember { mutableStateOf(0) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                backgroundColor = Purple700
            )
        },
        content = {
            Column(
                Modifier
                    .background(NotesViewBackground)
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                NotesList(
                    notesList = notes,
                    currentNoteTitle,
                    currentNoteBody,
                    currentNoteId,
                    shouldShowNoteDialog,
                    shouldShowDeleteDialog
                )
                BinImage(hasBinDrawableChanged)
                NotesDialog(
                    showDialog = shouldShowNoteDialog, noteTitle = currentNoteTitle,
                    noteBody = currentNoteBody, noteId = currentNoteId, addOrSave
                )
                DeleteDialog(
                    showDialog = shouldShowDeleteDialog,
                    noteId = currentNoteId, deleteNote = deleteNote,
                    hasBinDrawableChanged = hasBinDrawableChanged
                )

            }

        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                shouldShowNoteDialog.value = !shouldShowNoteDialog.value
                currentNoteTitle.value = ""
                currentNoteBody.value = ""
            }, backgroundColor = Purple700) {
                Text("+")
            }
        }
    )
}

@Composable
fun BinImage(hasBinDrawableChanged : MutableState<Boolean>) {
    val hasChanged = remember{hasBinDrawableChanged}
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = if (!hasChanged.value) R.drawable.png_bin else R.drawable.bin),
            contentDescription = "Notes Bin",
            modifier = Modifier
                .height(90.dp)
                .width(90.dp)
                .padding(bottom = 12.dp)
                .align(Alignment.BottomStart)
        )
    }
}

@Composable
fun NotesList(
    notesList: LiveData<List<NoteItem>>,
    currentNoteTitle: MutableState<String>,
    currentNoteBody: MutableState<String>,
    currentNoteId: MutableState<Int>,
    showDialog: MutableState<Boolean>,
    showDeleteDialog : MutableState<Boolean>
) {

    val notes = notesList.observeAsState()

    LazyRow(
        modifier = Modifier
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        itemsIndexed(items = notes.value ?: listOf(), itemContent = { index, item ->
            Column(
                modifier = Modifier
                    .background(color = NotesBackground)
                    .height(120.dp)
                    .width(120.dp)
                    .padding(12.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                currentNoteTitle.value = notes.value?.get(index)?.title ?: ""
                                currentNoteBody.value = notes.value?.get(index)?.note ?: ""
                                currentNoteId.value = notes.value?.get(index)?.id ?: 0
                                showDialog.value = true
                                Log.d("note", "note : ${item.title} , $currentNoteBody")
                            },
                            onLongPress = {
                                currentNoteId.value = notes.value?.get(index)?.id ?: 0
                                showDeleteDialog.value = true
                            }
                        )
                    }

            ) {
                Text(text = item.title.toString(), fontWeight = FontWeight.Bold)
                Text(text = item.note.toString(), modifier = Modifier.padding(bottom = 12.dp))
            }
        })
    }
}

@Composable
fun DeleteDialog(
    showDialog: MutableState<Boolean>,
    noteId: MutableState<Int>,
    deleteNote: (Int) -> Unit,
    hasBinDrawableChanged: MutableState<Boolean>
) {
    MaterialTheme {
        Column {
            val openDialog = remember { showDialog  }

            Button(onClick = {
                openDialog.value = true
            }) {
                Text("Click me")
            }

            if (openDialog.value) {

                AlertDialog(
                    onDismissRequest = {
                        openDialog.value = false
                    },
                    title = {
                        Text(text = "Delete Note")
                    },
                    text = {
                        Text("Are you sure you want to delete this note ? ")
                    },
                    confirmButton = {
                        Button(

                            onClick = {
                                openDialog.value = false
                                deleteNote.invoke(noteId.value)
                                if (!hasBinDrawableChanged.value){
                                    hasBinDrawableChanged.value = true
                                }
                            }) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        Button(

                            onClick = {
                                openDialog.value = false
                            }) {
                            Text("No")
                        }
                    }
                )
            }
        }

    }
}


@Composable
fun NotesDialog(
    showDialog: MutableState<Boolean>,
    noteTitle: MutableState<String>? = null,
    noteBody: MutableState<String>? = null,
    noteId: MutableState<Int>? = null,
    onClick: (String, String, Int) -> Unit
) {

    val shouldShow = remember { showDialog }
    if (shouldShow.value) {
        Dialog(onDismissRequest = {}) {
            Surface(
                modifier = Modifier
                    .width(350.dp)
                    .height(350.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.LightGray
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    val titleState =
                        remember { mutableStateOf(TextFieldValue(noteTitle?.value ?: "")) }
                    val noteState =
                        remember { mutableStateOf(TextFieldValue(noteBody?.value ?: "")) }
                    val context = LocalContext.current

                    Column(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                    ) {
                        Text("Note Title")
                        TextField(
                            value = titleState.value,
                            onValueChange = { titleState.value = it },
                            maxLines = 1,
                            singleLine = true
                        )
                        Text("Note Body", modifier = Modifier.padding(top = 12.dp))
                        TextField(
                            value = noteState.value,
                            onValueChange = { noteState.value = it },
                            modifier = Modifier
                                .height(180.dp)
                                .padding(bottom = 12.dp),
                            maxLines = 20
                        )
                    }
                    Button(
                        onClick = {
                            if (titleState.value.text.isEmpty() || noteState.value.text.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Please add a note title and a note body",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }
                            onClick.invoke(
                                titleState.value.text,
                                noteState.value.text,
                                noteId?.value ?: 0
                            )
                            noteTitle?.value = ""
                            noteBody?.value = ""
                            noteId?.value = 0
                            shouldShow.value = false
                        },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        Text(text = "Save")
                    }
                }
            }
        }
    }
}

