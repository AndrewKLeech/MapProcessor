package com.example.andrew.mapprocessor

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import android.content.Intent
import android.graphics.BitmapFactory
import android.widget.Toast
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.convert_screen.*
import android.opengl.ETC1.getWidth




/**
 * Created by Andrew on 28/01/2018.
 */

class ConvertActivity : AppCompatActivity() {
    var mCurrentPhotoPath: String? = null
    var CAM_INTENT = 1;
    var photoFile: File? = null

    private fun setPic() {
        // Get the dimensions of the View
        val targetW = convert_map_img_view.width
        val targetH = convert_map_img_view.height

        // Get the dimensions of the bitmap
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions)
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight
        // Determine how much to scale down the image
        val scaleFactor = Math.min(photoW / targetW, photoH / targetH)

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor
        bmOptions.inPurgeable = true

        val bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions)
        val bitmapconv =  PrepImage(bitmap)
        convert_map_img_view.setImageBitmap(bitmapconv)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        val intent = intent
        mCurrentPhotoPath = intent.getStringExtra("img")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.convert_screen)
        setSupportActionBar(toolbar)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setPic()
        }

    }
}
