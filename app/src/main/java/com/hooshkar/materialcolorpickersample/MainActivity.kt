package com.hooshkar.materialcolorpickersample

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hooshkar.materialcolorpicker.views.MaterialColorPickerView
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
        val colorPickerView = MaterialColorPickerView(context)

        colorPickerView.setNewColor(0xFFFF0000.toInt())

        MaterialAlertDialogBuilder(this, R.style.MaterialColoPickerAlertDialog)
            .setView(colorPickerView)
            .setPositiveButton("Done", null)
            .setNegativeButton("Cancel", null)
            .show()




//        val dialog = MaterialColorPickerDialog(this) {
//            Log.d("TAG_1234", "color set: $it")
//        }
//
//        dialog.onColorChangedListener = OnColorChangedListener {
//            Log.d("TAG_1234", "color changed: $it")
//        }
//
//        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Done") { _, _ ->
//
//        }
//
//        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { _, _ ->
//
//        }
//
//        dialog.show()
    }
}