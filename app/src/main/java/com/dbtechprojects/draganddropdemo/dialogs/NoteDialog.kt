package com.dbtechprojects.draganddropdemo.dialogs

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.dbtechprojects.draganddropdemo.R
import com.dbtechprojects.draganddropdemo.database.DatabaseHandler
import com.dbtechprojects.draganddropdemo.models.NoteItem
import java.time.Instant
import java.time.format.DateTimeFormatter


class NoteDialog (): DialogFragment() {
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

        button.setOnClickListener {
            //close helpDialog
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
            val note = NoteItem(
                title = notetitle.text.toString(),
                note = notetext.text.toString(),
                TimeStamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString()
            )
            DatabaseHandler(requireContext()).addNote(note)

        }



        return rootview

    }



    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val activity: Activity? = activity
        if (activity is DialogInterface.OnDismissListener) {
            (activity as DialogInterface.OnDismissListener).onDismiss(dialog)
        }
    }


}