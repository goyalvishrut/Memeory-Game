package com.example.my_memory

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.example.my_memory.models.BoardSize
import kotlin.math.min

class ImagePickerAdapter(
    private val context: Context,
    private val chosenImageUris: List<Uri>,
    private val boardSize: BoardSize
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.card_image,parent,false)
        val cardWidth = parent.width / boardSize.getWidth()
        val cardHeight = parent.height/ boardSize.getHeight()
        val cardSideLength = min(cardHeight,cardWidth)
        val layoutParams = view.findViewById<ImageView>(R.id.ivCustomImage).layoutParams
        layoutParams.width = cardSideLength
        layoutParams.height = cardSideLength
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount() = boardSize.getNumPairs()

}
