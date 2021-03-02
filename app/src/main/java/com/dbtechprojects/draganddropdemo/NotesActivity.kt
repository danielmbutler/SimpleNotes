package com.dbtechprojects.draganddropdemo

import android.content.ClipData
import android.content.ClipDescription
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.lang.Exception

class NotesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        val llbin = findViewById<LinearLayout>(R.id.llBin)
        val llTop = findViewById<LinearLayout>(R.id.llTopnotes)
        val dragview = findViewById<View>(R.id.dragviewnotes)
        val TextView = findViewById<TextView>(R.id.dragviewText)

        dragview.setOnLongClickListener {
            val cliptext = it.id.toString()

            val item = ClipData.Item(cliptext)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val data = ClipData(cliptext,mimeTypes, item) // equivalent to bundle

            val dragShadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(data,dragShadowBuilder,it,0)

            it.visibility = View.INVISIBLE
            true
        }


        TextView.setOnLongClickListener {
            val cliptext = "1"
            val item = ClipData.Item(cliptext)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val data = ClipData(cliptext,mimeTypes, item)

            val dragShadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(data,dragShadowBuilder,it,0)

            it.visibility = View.INVISIBLE
            true
        }
        llbin.setOnDragListener(dragListener)
        llTop.setOnDragListener(dragListener)


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
                val dragdata = item.text
                if (view.tag != null){
                    Toast.makeText(this, view.tag.toString(), Toast.LENGTH_SHORT).show()
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Are you sure want to delete ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes") { dialog, id ->
                            try{
                                val id = item.text
                                Toast.makeText(this, id, Toast.LENGTH_SHORT).show()
                            }catch (e: Exception){
                                e.printStackTrace()
                            }


                        }
                        .setNegativeButton("No") { dialog, id ->
                            // Dismiss the dialog
                            dialog.dismiss()
                        }
                    val alert = builder.create()
                    alert.show()
                }

                view.invalidate()

                val v = event.localState as View
                val owner = v.parent as ViewGroup
                owner.removeView(v)

                val destination = view as LinearLayout
                destination.addView(v)
                v.visibility = View.VISIBLE
                true
            }
            DragEvent.ACTION_DRAG_ENDED ->{
                view.invalidate()
                true
            }
            else -> false
        }
    }
}