package com.dbtechprojects.draganddropdemo

import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dragview = findViewById<View>(R.id.dragview)
        val lltop = findViewById<LinearLayout>(R.id.llTop)
        val llBottom = findViewById<LinearLayout>(R.id.llBottom)
        val Button = findViewById<Button>(R.id.intentbutton)
        val notesviewbtn = findViewById<Button>(R.id.notesviewbutton)


        dragview.setOnLongClickListener {
            val cliptext = "this is our ClipDataText"
            val item = ClipData.Item(cliptext)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val data = ClipData(cliptext,mimeTypes, item)

            val dragShadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(data,dragShadowBuilder,it,0)

            it.visibility = View.INVISIBLE
            true
        }

        notesviewbtn.setOnClickListener {
            val intent = Intent(this, notesview::class.java)
            startActivity(intent)
        }


        lltop.setOnDragListener(dragListener)
        llBottom.setOnDragListener(dragListener)

        Button.setOnClickListener {
            val intent = Intent(this, NotesActivity::class.java)
            startActivity(intent)
        }


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
                Toast.makeText(this, dragdata, Toast.LENGTH_SHORT).show()
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