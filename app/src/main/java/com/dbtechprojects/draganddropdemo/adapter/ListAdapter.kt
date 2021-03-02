package com.dbtechprojects.draganddropdemo.adapter

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.icu.text.CaseMap
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dbtechprojects.draganddropdemo.R
import com.dbtechprojects.draganddropdemo.models.NoteItem


class ListAdapter(
    private var list: MutableList<NoteItem>,
    private val context: Context
    )
    : RecyclerView.Adapter<ListViewHolder>() {
    private var onLongClickListener: OnLongClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder{
        val inflater = LayoutInflater.from(parent.context)
        return ListViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val model = list[position]
        val itemView = holder.itemView
        val TitleText = itemView.findViewById<TextView>(R.id.Rv_Item_Title)
        val MessageText = itemView.findViewById<TextView>(R.id.Rv_Item_Note)

        TitleText.setText(model.title)
        MessageText.setText(model.note)

        itemView.setOnClickListener {
            Log.d("notes", "Id: ${model.id}")
        }

        //onlongclick
        itemView.setOnLongClickListener {
            val cliptext = model.id.toString()
            val item = ClipData.Item(cliptext)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val data = ClipData(cliptext,mimeTypes, item)

            val dragShadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(data,dragShadowBuilder,it,0)

            true
        }



    }

    override fun getItemCount(): Int = list.size
    /**
     * Function called to swap dragged items
     */


}
class ListViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.note_item, parent, false)) {

    fun bind(Note: NoteItem) {}

}