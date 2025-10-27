package com.hooshkar.materialcolorpickersample

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hooshkar.materialcolorpicker.views.MaterialColorPickerView.OnColorChangedListener
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

        binding.button.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
                .setTitle("Title")
                .setPositiveButton("Yes", null)
                .show()

        }

        binding.colorPicker.onColorChangedListener = OnColorChangedListener { newColor ->
            Log.d("TAG_1412", "new color: ${newColor.toHexString(HexFormat.Default)}")
        }
    }
}