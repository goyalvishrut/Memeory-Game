package com.example.my_memory

import android.animation.ArgbEvaluator
import android.content.Intent
import android.icu.text.CaseMap
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.my_memory.models.BoardSize
import com.example.my_memory.models.Memorycard
import com.example.my_memory.models.Memorygame
import com.example.my_memory.utils.EXTRA_BOARD_SIZE
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlin.math.max
import com.example.my_memory.utils.DEFAULT_ICONS as DEFAULT_ICONS

class MainActivity : AppCompatActivity() {

    companion object{
        private val TAG= "MainActivity"
        private const val CREATE_REQUEST_CODE = 497
    }

    private lateinit var clRoot:CoordinatorLayout
    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNumMoves: TextView
    private lateinit var tvNumPairs: TextView

    private lateinit var memorygame: Memorygame
    private lateinit var adapter: MemoryBoardAdapter
    private var boardSize: BoardSize = BoardSize.EASY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clRoot = findViewById(R.id.clRoot)
        rvBoard = findViewById(R.id.rvBoard)
        tvNumMoves= findViewById(R.id.tvNumMoves)
        tvNumPairs= findViewById(R.id.tvNumPairs)

        setupBoard()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.mi_refresh -> {
                // Restart the game
                if (memorygame.getnummoves() >0 && !memorygame.haveWonGame()){
                    showAlertdialog("Quit Your Current Game",null,View.OnClickListener {
                        setupBoard()
                    })
                }else{
                    setupBoard()
                }
                return true
            }
            R.id.mi_newsize ->{
                shownewsizedialog()
                return true
            }
            R.id.mi_custom ->{
                showCreationDialog()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun showCreationDialog() {
        val boardsizeview = LayoutInflater.from(this).inflate(R.layout.dialog_board_size,null)
        val radioGroupSize = boardsizeview.findViewById<RadioGroup>(R.id.radiogroup)
        showAlertdialog("Choose the Desigred Board Size",boardsizeview,View.OnClickListener {
            // Set new size value
            val desigredBoardSize = when(radioGroupSize.checkedRadioButtonId){
                R.id.radioeasy -> BoardSize.EASY
                R.id.radiomedium -> BoardSize.MEDIUM
                else -> BoardSize.HARD
            }
            // Navigate to the new screen
            val intent = Intent(this,CreateActivity::class.java)
            intent.putExtra(EXTRA_BOARD_SIZE,desigredBoardSize)
            startActivityForResult(intent, CREATE_REQUEST_CODE)
        })
    }

    private fun shownewsizedialog() {
        val boardsizeview = LayoutInflater.from(this).inflate(R.layout.dialog_board_size,null)
        val radioGroupSize = boardsizeview.findViewById<RadioGroup>(R.id.radiogroup)
        when (boardSize){
            BoardSize.EASY -> radioGroupSize.check(R.id.radioeasy)
            BoardSize.MEDIUM -> radioGroupSize.check(R.id.radiomedium)
            BoardSize.HARD -> radioGroupSize.check(R.id.radiohard)
        }
        showAlertdialog("Choose Size",boardsizeview,View.OnClickListener {
            // Set new size value
            boardSize = when(radioGroupSize.checkedRadioButtonId){
                R.id.radioeasy -> BoardSize.EASY
                R.id.radiomedium -> BoardSize.MEDIUM
                else -> BoardSize.HARD
            }
            setupBoard()
        })
    }


    private fun showAlertdialog(title: String, view: View?, positiveButtonClickListener: View.OnClickListener) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton("Cancel",null)
            .setPositiveButton("Ok"){_,_->
                positiveButtonClickListener.onClick(null)
            }.show()
    }

    private fun setupBoard() {
        when(boardSize){
            BoardSize.EASY -> {
                tvNumMoves.text = "EASY: 4 X 2"
                tvNumPairs.text = "0 / 4"
            }
            BoardSize.MEDIUM -> {
                tvNumMoves.text = "MEDIUM: 6 X 3"
                tvNumPairs.text = "0 / 9"
            }
            BoardSize.HARD -> {
                tvNumMoves.text = "HARD: 6 X 4"
                tvNumPairs.text = "0 / 12"
            }
        }
        tvNumPairs.setBackgroundColor(ContextCompat.getColor(this,R.color.color_progress_none_pairs))
//        tvNumMoves.setBackgroundColor(ContextCompat.getColor(this,R.color.color_progress_none_moves))
        memorygame=Memorygame(boardSize)

        adapter=MemoryBoardAdapter(this,boardSize,memorygame.cards,object :MemoryBoardAdapter.CardclickListner{
            override fun onCardClicked(postion: Int) {
                updateGamewithFLip(postion)
            }
        }
        )

        rvBoard.adapter= adapter
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager=GridLayoutManager(this,boardSize.getWidth())
    }

    private fun updateGamewithFLip(postion: Int) {
        if(memorygame.haveWonGame()){
            // alert user
            Snackbar.make(clRoot,"You already won the game",Snackbar.LENGTH_LONG).show()
            return
        }
        if(memorygame.isCardFacedUp(postion)){
            //Alert user
            Snackbar.make(clRoot,"Invalid Move",Snackbar.LENGTH_SHORT).show()
            return
        }

        // actual flip over the card
        if(memorygame.flipCard(postion)){
            Log.i(TAG,"Found a match Num Pairs Found :$(memorygame.numPairsFound)")
            val colornumPairs = ArgbEvaluator().evaluate(
                memorygame.numPairsFound.toFloat() / boardSize.getNumPairs(),
                ContextCompat.getColor(this,R.color.color_progress_none_pairs),
                ContextCompat.getColor(this,R.color.color_progress_full_pairs)
            ) as Int

            tvNumPairs.setBackgroundColor(colornumPairs)
            tvNumPairs.text = "Pairs: ${memorygame.numPairsFound} / ${boardSize.getNumPairs()}"
            if(memorygame.haveWonGame()){
                Snackbar.make(clRoot,"You won the game",Snackbar.LENGTH_LONG).show()
            }
        }

        tvNumMoves.text = "Moves: ${memorygame.getnummoves()}"
        adapter.notifyDataSetChanged()
    }
}

