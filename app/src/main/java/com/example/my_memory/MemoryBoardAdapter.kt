package com.example.my_memory

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.my_memory.models.BoardSize
import com.example.my_memory.models.Memorycard
import kotlin.math.min

class MemoryBoardAdapter(
    private val context: Context,
    private val boardSize: BoardSize,
    private val cards: List<Memorycard>,
    private val cardclickListner: CardclickListner
) :
    RecyclerView.Adapter<MemoryBoardAdapter.ViewHolder>() {

    companion object{
        private const val margin_size = 10
        private const val TAG = "MemoryBoardAdapter"
    }

    interface CardclickListner{
        fun onCardClicked(postion:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardWidth = parent.width / boardSize.getWidth() - (2* margin_size)
        val cardHeight = parent.height / boardSize.getHeight() - (2* margin_size)
        val cardSideLength = min(cardWidth,cardHeight)
        val view:View = LayoutInflater.from(context).inflate(R.layout.memory_card, parent, false)
        val layoutParams = view.findViewById<CardView>(R.id.cardview).layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.height=cardSideLength
        layoutParams.width=cardSideLength
        layoutParams.setMargins(margin_size, margin_size, margin_size, margin_size)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = boardSize.numCards

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val imageButton = itemView.findViewById<ImageButton>(R.id.imageButton)

        fun bind(position: Int) {
            val memorycard = cards[position]
            imageButton.setImageResource(if (memorycard.isFaceup) memorycard.identifier else R.drawable.ic_launcher_background)

            imageButton.alpha = if (memorycard.isMatched) .4f else 1.0f
            val colorStateList = if (memorycard.isMatched) ContextCompat.getColorStateList(context,R.color.colour_gray) else null
            ViewCompat.setBackgroundTintList(imageButton,colorStateList)

            imageButton.setOnClickListener{
                Log.i(TAG,"clicked on the postion $position")
                cardclickListner.onCardClicked(position)
            }
        }
    }
}
