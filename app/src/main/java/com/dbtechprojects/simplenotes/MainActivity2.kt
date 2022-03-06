package com.dbtechprojects.simplenotes

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.dbtechprojects.simplenotes.database.NotesDao
import com.dbtechprojects.simplenotes.database.NotesDatabase
import com.dbtechprojects.simplenotes.models.NoteItem
import com.dbtechprojects.simplenotes.ui.theme.DragandDropDemoTheme
import com.dbtechprojects.simplenotes.ui.theme.NotesBackground
import com.dbtechprojects.simplenotes.ui.theme.NotesViewBackground
import com.dbtechprojects.simplenotes.ui.theme.Purple700

class MainActivity2 : ComponentActivity() {

    private lateinit var dbHandler: NotesDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHandler = NotesDatabase.provideDB(applicationContext).noteDao()
        setContent {
            DragandDropDemoTheme {
                Surface(color = MaterialTheme.colors.background) {
                    AppScaffold(dbHandler)
                }
            }
        }
    }
}

@Composable
fun AppScaffold(dbHandler: NotesDao) {
    val notes = dbHandler.getAllNotes().observeAsState()
    val shouldShowNoteDialog = remember { mutableStateOf(false) }
    val currentNoteTitle = remember { mutableStateOf("") }
    val currentNoteBody = remember { mutableStateOf("") }
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
                    shouldShowNoteDialog
                )
                BinImage()
                NotesDialog(
                    showDialog = shouldShowNoteDialog , noteTitle = currentNoteTitle,
                    noteBody = currentNoteBody )

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
fun BinImage() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.png_bin),
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
    notesList: State<List<NoteItem>?>,
    currentNoteTitle: MutableState<String>,
    currentNoteBody: MutableState<String>,
    showDialog: MutableState<Boolean>
) {
    LazyRow(
        modifier = Modifier
            .padding(6.dp)
    ) {
        items(items = notesList.value ?: listOf(), itemContent = { item ->
            Column(
                modifier = Modifier
                    .background(color = NotesBackground)
                    .height(120.dp)
                    .width(120.dp)
                    .padding(12.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                currentNoteTitle.value = item.title ?: ""
                                currentNoteBody.value = item.note ?: ""
                                showDialog.value = true
                                Log.d("note", "note : $currentNoteTitle , $currentNoteBody")
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
fun NotesDialog(
    showDialog: MutableState<Boolean>,
    noteTitle: MutableState<String>? = null,
    noteBody: MutableState<String>? = null
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
                    val titleState = remember { mutableStateOf(TextFieldValue(noteTitle?.value ?: "")) }
                    val noteState = remember { mutableStateOf(TextFieldValue(noteBody?.value ?: "")) }

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
                        onClick = { shouldShow.value = false },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        Text(text = "Save")
                    }
                }
            }
        }
    }
}

