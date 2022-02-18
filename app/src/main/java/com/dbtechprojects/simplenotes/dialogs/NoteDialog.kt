package com.dbtechprojects.simplenotes.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.dbtechprojects.simplenotes.R
import com.dbtechprojects.simplenotes.models.NoteItem
import java.time.Instant
import java.time.format.DateTimeFormatter


class NoteDialog (private val noteListener: AddNoteListener , private val note: NoteItem?): DialogFragment() {

    private var editMode: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragement_container = container
        val rootview = layoutInflater.inflate(R.layout.dialog, fragement_container, false)


         val button = rootview.findViewById<Button>(R.id.submitbutton)
         val notetext = rootview.findViewById<EditText>(R.id.editTextNote)
         val notetitle = rootview.findViewById<EditText>(R.id.editTextTitle)

        note?.let {
            editMode = true
            notetitle.setText(it.title)
            notetext.setText(it.note)
        }

        if (editMode){
            button.text = getString(R.string.save)
        }

        button.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()

            if (notetitle.text.toString().isEmpty() || notetext.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "Please provide a note title and note body", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (editMode && note != null){
                noteListener.editNote(notetext.text.toString(), notetitle.text.toString(), note.id!!)
                return@setOnClickListener
            }

            val note = NoteItem(
                title = notetitle.text.toString(),
                note = notetext.text.toString(),
                timeStamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString()
            )
           noteListener.addNote(note)

        }



        return rootview

    }


}

interface AddNoteListener {
    fun addNote(note: NoteItem)
    fun editNote(note: String, title: String, id: Int)
}