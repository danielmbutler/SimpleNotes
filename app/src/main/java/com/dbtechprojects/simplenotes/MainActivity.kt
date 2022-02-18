package com.dbtechprojects.simplenotes

import android.content.ClipDescription
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dbtechprojects.simplenotes.adapter.NoteClickListener
import com.dbtechprojects.simplenotes.adapter.NotesAdapter
import com.dbtechprojects.simplenotes.database.DatabaseHandler
import com.dbtechprojects.simplenotes.dialogs.AddNoteListener
import com.dbtechprojects.simplenotes.dialogs.NoteDialog
import com.dbtechprojects.simplenotes.models.NoteItem
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity(), AddNoteListener, NoteClickListener {

    private lateinit var dbHandler : DatabaseHandler
    private lateinit var notesBin : ImageView
    private var isDrawableSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notesview)
        dbHandler = DatabaseHandler(this)
        getNotesFromLocalDB()


        val floatingbutton = findViewById<FloatingActionButton>(R.id.floatingActionButton)

        floatingbutton.setOnClickListener {
            val dialog =
                NoteDialog(this, null)
            dialog.show(supportFragmentManager, "Add Note")
        }
        notesBin = findViewById<ImageView>(R.id.notesviewbin)

        notesBin.setOnDragListener(dragListener)

    }

    private fun getNotesFromLocalDB() {

        val getNoteList = dbHandler.getNotes()

        if (getNoteList.size > 0) {
            setupRecyclerView(getNoteList)
        } else {
            val emptylist = ArrayList<NoteItem>()
            setupRecyclerView(emptylist)

        }
    }


    private fun setupRecyclerView(
        notelist: ArrayList<NoteItem>
    ) {

        val rv = findViewById<RecyclerView>(R.id.notesrecyclerview)
        val horizontalLayoutManagaer =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        rv.layoutManager = horizontalLayoutManagaer


        val adapter = NotesAdapter(notelist, this)
        rv.adapter = adapter
    }

    private val dragListener = View.OnDragListener { view, event ->
        when(event.action){
            DragEvent.ACTION_DRAG_STARTED -> {
                event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                view.invalidate()
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> true
            DragEvent.ACTION_DRAG_EXITED -> {
                view.invalidate()
                true
            }
            DragEvent.ACTION_DROP ->{
                val item = event.clipData.getItemAt(0)
                val noteId = item.text.toString()
                showDeleteDialog(noteId)

                true
            }
            DragEvent.ACTION_DRAG_ENDED ->{

                true
            }
            else -> false
        }
    }

    private fun showDeleteDialog(noteId: String){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete this note ?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                    val noteIdString = noteId
                    val result = dbHandler.deleteNote(noteIdString.toInt())

                    if (result > 0){
                        getNotesFromLocalDB()
                        Toast.makeText(this, "Note has been deleted", Toast.LENGTH_SHORT).show()
                        setBinDrawable()
                    } else{
                        Toast.makeText(this, "Error deleting", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }

            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()
    }

    private fun setBinDrawable(){
        if (!isDrawableSet){
            isDrawableSet = true
            notesBin.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bin))
        }
    }

    override fun addNote(note: NoteItem) {
        dbHandler.addNote(note)
        getNotesFromLocalDB()
    }

    override fun editNote(note: String, title: String, id: Int) {
        dbHandler.editNote(note,title, id)
        getNotesFromLocalDB()
    }

    override fun onClick(noteItem: NoteItem) {
        val dialog =
            NoteDialog(this, noteItem)
        dialog.show(supportFragmentManager, "Edit note")
    }

}