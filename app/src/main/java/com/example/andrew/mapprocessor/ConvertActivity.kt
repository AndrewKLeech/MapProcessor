package com.example.andrew.mapprocessor

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException

/**
 * Created by Andrew on 28/01/2018.
 */

class ConvertActivity : AppCompatActivity() {
    var mCurrentPhotoPath: String? = null
    var CAM_INTENT = 1;
    var photoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.convert_screen)
        setSupportActionBar(toolbar)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }
}
