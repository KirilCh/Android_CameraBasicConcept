package com.first.emojisnap.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.first.emojisnap.R
import com.first.emojisnap.SmileyType
import kotlinx.android.synthetic.main.design_options_card.view.*

class SmileyAdapter(
        private val mItemList : List<SmileyItem>,
        private val mListener : OnItemClickListener,
        private val mChooseSmileyType : SmileyType
    ) : RecyclerView.Adapter<SmileyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.design_options_card,parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = mItemList[position]

        holder.imageView.setImageResource(currentItem.imageResource)
    }

    override fun getItemCount() = mItemList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener{
        val imageView : ImageView = itemView.imageViewCard

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                mListener.onItemClick(position, mChooseSmileyType)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, chooseSmiley : SmileyType)
    }

}