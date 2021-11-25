package com.example.my_memory

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.my_memory.models.BoardSize
import com.example.my_memory.utils.EXTRA_BOARD_SIZE

class CreateActivity : AppCompatActivity() {

    private lateinit var rvimagepicker: RecyclerView
    private lateinit var btn_save: Button
    private lateinit var edt_gamename: EditText

    private lateinit var boardSize: BoardSize
    private var numimagesrequired = -1
    private val chosenImageUris = mutableListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        rvimagepicker = findViewById(R.id.rvimagepicker)
        btn_save = findViewById(R.id.btn_save)
        edt_gamename = findViewById(R.id.edt_gamename)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        boardSize = intent.getSerializableExtra(EXTRA_BOARD_SIZE) as BoardSize
        numimagesrequired = boardSize.getNumPairs()
        supportActionBar?.title = "Choose pics(0/ $numimagesrequired)"

        rvimagepicker.adapter = ImagePickerAdapter(this,chosenImageUris,boardSize)
        rvimagepicker.setHasFixedSize(true)
        rvimagepicker.layoutManager = GridLayoutManager(this,boardSize.getWidth())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}