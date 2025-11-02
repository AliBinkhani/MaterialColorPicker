package com.hooshkar.materialcolorpickersample

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hooshkar.materialcolorpicker.MaterialColorPickerDialogBuilder
import com.hooshkar.materialcolorpickersample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @OptIn(ExperimentalStdlibApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val context: Context = this
        val dialog = MaterialColorPickerDialogBuilder(context)
            .setOpacityBarEnabled(true)
            .setRecentColor(0x88FF0000.toInt())
            .setOnColorChangeListener { color ->
                Log.d("TAG_1234", "color changed: $color")
            }.setPositiveButton("OK") { _, _, color ->
                Log.d("TAG_1234", "color selected: $color")
            }.setNegativeButton("Cancel", null)
            .show()

        binding.button.setOnClickListener {
            dialog.show()
        }
    }
}