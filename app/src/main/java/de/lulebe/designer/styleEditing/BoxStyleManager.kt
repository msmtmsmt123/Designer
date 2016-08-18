package de.lulebe.designer.styleEditing

import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import de.lulebe.designer.R
import de.lulebe.designer.data.BoardState
import de.lulebe.designer.data.objects.BoardObject
import de.lulebe.designer.data.styles.BoxStyle


class BoxStyleManager(val mView: ViewGroup, val mBoardObject: BoardObject, val mBoardState: BoardState) {

    private val mListview: RecyclerView

    init {
        mListview = mView.findViewById(R.id.rv_boxstyles) as RecyclerView

        mListview.layoutManager = LinearLayoutManager(mListview.context)
        mListview.adapter = BoxStyleAdapter()

        mBoardObject.styles.addChangeListener {
            mListview.adapter.notifyDataSetChanged()
        }
    }


    private fun openEditDialog (bs: BoxStyle) {
        val view = LayoutInflater.from(mView.context).inflate(R.layout.dialog_edit_boxstyle, null)
        (view.findViewById(R.id.field_width) as EditText).setText(bs.width.toString())
        (view.findViewById(R.id.field_height) as EditText).setText(bs.height.toString())
        (view.findViewById(R.id.field_cornerradius) as EditText).setText(bs.cornerRadius.toString())
        AlertDialog.Builder(mView.context)
                .setView(view)
                .setTitle("Edit Box Style")
                .setNegativeButton(android.R.string.cancel, DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.cancel()
                })
                .setPositiveButton("save", DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    bs.width = (view.findViewById(R.id.field_width) as EditText).text.toString().toInt()
                    bs.height = (view.findViewById(R.id.field_height) as EditText).text.toString().toInt()
                    bs.cornerRadius = (view.findViewById(R.id.field_cornerradius) as EditText).text.toString().toInt()
                })
                .show()
    }


    inner class BoxStyleAdapter : RecyclerView.Adapter<BoxStyleAdapter.StyleViewHolder>() {
        override fun onBindViewHolder(holder: StyleViewHolder, position: Int) {
            val bs = mBoardObject.styles.boxStyles.values.toList().get(position)
            holder.name.text = bs.name
            holder.dimensions.text = bs.width.toString() + " * " + bs.height.toString() + ", radius: " + bs.cornerRadius.toString()
            holder.view.setOnClickListener {
                if (mBoardState.selected != null)
                    mBoardState.selected!!.boxStyle = bs
            }
            holder.view.setOnLongClickListener {
                openEditDialog(bs)
                true
            }
            holder.delete.setOnClickListener {
                if (!mBoardObject.styleIsUsed(bs))
                    mBoardObject.styles.removeBoxStyle(bs)
            }
        }

        override fun getItemCount(): Int {
            return mBoardObject.styles.boxStyles.count()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StyleViewHolder? {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.listitem_boxstyle, parent, false)
            return StyleViewHolder(itemView)
        }

        inner class StyleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val view: View
            val name: TextView
            val dimensions: TextView
            val delete: View
            init {
                view = itemView
                name = itemView.findViewById(R.id.name) as TextView
                dimensions = itemView.findViewById(R.id.dimensions) as TextView
                delete = itemView.findViewById(R.id.btn_delete)
            }
        }
    }
}