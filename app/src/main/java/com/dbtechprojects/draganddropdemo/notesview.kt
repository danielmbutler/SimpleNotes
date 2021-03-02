package com.dbtechprojects.draganddropdemo


import android.app.Dialog
import android.content.ClipDescription
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dbtechprojects.draganddropdemo.adapter.ListAdapter
import com.dbtechprojects.draganddropdemo.database.DatabaseHandler
import com.dbtechprojects.draganddropdemo.dialogs.NoteDialog
import com.dbtechprojects.draganddropdemo.models.NoteItem
import com.google.android.material.floatingactionbutton.FloatingActionButton


class notesview : AppCompatActivity(), DialogInterface.OnDismissListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notesview)
        getNotesFromLocalDB()


        val floatingbutton = findViewById<FloatingActionButton>(R.id.floatingActionButton)

        floatingbutton.setOnClickListener {
            val dialog =
                NoteDialog()
            dialog.show(supportFragmentManager, "Help")


        }
        val notesbin = findViewById<ImageView>(R.id.notesviewbin)

        notesbin.setOnDragListener(dragListener)




    }

    fun getNotesFromLocalDB() {

        val dbHandler = DatabaseHandler(this)

        val getNoteList = dbHandler.getHappyPlacesList()

        if (getNoteList.size > 0) {

            setupRecyclerView(getNoteList)
            Log.d("notesview", getNoteList.toString())
        } else {
            println("no notes")
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

        val itemDecorator = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider)!!)
        rv.addItemDecoration(itemDecorator)


        rv.setHasFixedSize(true)
        val adapter = ListAdapter(notelist, this)
        rv.adapter = adapter




    }

    override fun onDismiss(dialog: DialogInterface?) {
        println("dismissed")
        getNotesFromLocalDB()
    }

    val dragListener = View.OnDragListener { view, event ->
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
                val dragdata = item.text.toString()


                val builder = AlertDialog.Builder(this)
                builder.setMessage("Are you sure you want to delete this note ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->

                        val noteId = dragdata
                        if (!noteId.isEmpty()){
                            val noteIdString = noteId.toString()
                            val DBhandler = DatabaseHandler(this)
                            val result = DBhandler.deleteHappyPlace(noteIdString.toInt())

                            if (result > 0){
                                getNotesFromLocalDB()
                                view.invalidate()
                                Toast.makeText(this, "Item has been deleted", Toast.LENGTH_SHORT).show()
                            } else{
                                Log.e("test" , "null found")
                                dialog.dismiss()
                            }
                        } else {
                            Log.e("test" , "null found")
                            dialog.dismiss()
                        }


                    }

                    .setNegativeButton("No") { dialog, id ->
                        // Dismiss the dialog
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()





//                val v = event.localState as View
//                val owner = v.parent as ViewGroup
//                owner.removeView(v)
//
//                val destination = view as LinearLayout
//                destination.addView(v)
//                v.visibility = View.VISIBLE
                true
            }
            DragEvent.ACTION_DRAG_ENDED ->{

                true
            }
            else -> false
        }
    }

}