package com.example.andrew.maps

import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AppCompatActivity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.convert_screen.*
import android.widget.SeekBar
import org.opencv.android.OpenCVLoader

/**
* Created by Andrew on 28/01/2018.
*/

class ConvertActivity : AppCompatActivity() {

    // Paths for photo files
    private var mSrcPhotoPath: String? = null
    var mCurrentPhotoPath: String? = null

    // Bitmap of segmented image
    var bitmapconv: Bitmap? = null
    var hsV_lower = 89.0
    var hsV_upper = 106.0
    var hSv_lower = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {

        // Try load OpenCV
        if (!OpenCVLoader.initDebug()) {
            Log.d("E", "OpenCV initialization error" + hsV_lower.toString())
        }

        // Get photo paths sent with intent
        val intent = intent
        mSrcPhotoPath = intent.getStringExtra("src")
        mCurrentPhotoPath = mSrcPhotoPath

        super.onCreate(savedInstanceState)
        setContentView(R.layout.convert_screen)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        // Set starting progress of Value min bar to hsV_lower
        valLowerSeekBar.progress = hsV_lower.toInt()

        // Set starting progress of Value max bar to hsV_upper
        valUpperSeekBar.progress = hsV_upper.toInt()

        // Listener for valLowerSeekBar
        valLowerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                mCurrentPhotoPath = mSrcPhotoPath
                hsV_lower = valLowerSeekBar.progress.toDouble()
                setPic(mCurrentPhotoPath!!)
                Log.d("I", "Value is: " + hsV_lower.toString())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do nothing
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Do nothing
            }
        })

        // Listener for valUpperSeekBar
        valUpperSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                mCurrentPhotoPath = mSrcPhotoPath
                hsV_upper = valUpperSeekBar.progress.toDouble()
                setPic(mCurrentPhotoPath!!)
                Log.d("I", "Value is: " + hsV_upper.toString())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do nothing
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                //Do nothing
            }
        })

        // Listener for done button
        done_seg_img_btn.setOnClickListener({

            // Set view as bitmap returned from thin function im ImageProcessor

            /* two approaches to thinning:
            thin: use a morphological thinning operation
            thin2: use canny edge detection then get contours
             */
            convert_map_img_view.setImageBitmap(ImageProcessor().thin(bitmapconv!!))
            //convert_map_img_view.setImageBitmap(ImageProcessor().thin2(ImageProcessor().thin(bitmapconv!!)))

            hsV_Max_txt.visibility = View.INVISIBLE
            hsV_Min_txt.visibility = View.INVISIBLE
            valUpperSeekBar.visibility = View.INVISIBLE
            valLowerSeekBar.visibility = View.INVISIBLE
            cancel_img_btn.visibility = View.VISIBLE
        })

        // Listener for cancel button
        cancel_img_btn.setOnClickListener {
            hsV_Max_txt.visibility = View.VISIBLE
            hsV_Min_txt.visibility = View.VISIBLE
            valUpperSeekBar.visibility = View.VISIBLE
            valLowerSeekBar.visibility = View.VISIBLE
            cancel_img_btn.visibility = View.INVISIBLE

        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setPic(mCurrentPhotoPath!!)
        }
    }

    private fun setPic(path: String) {
        // Get the dimensions of the View
        val targetW = convert_map_img_view.width
        val targetH = convert_map_img_view.height

        // Get the dimensions of the bitmap
        val bmOptions = BitmapFactory.Options()
        //bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, bmOptions)
        //val photoW = bmOptions.outWidth
        //val photoH = bmOptions.outHeight

        // Determine how much to scale down the image
        val scaleFactor = Math.min(1, 1)

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor
        bmOptions.inPurgeable = true

        // Create new mutable bitmap
        val bitmap = BitmapFactory.decodeFile(path, bmOptions)
        val bitmap_cpy = bitmap.copy(bitmap.config, true)

        // Segment image
        bitmapconv =  ImageProcessor().Segment(bitmap_cpy, hSv_lower, hsV_lower, hsV_upper)

        // Show bitmap
        convert_map_img_view.setImageBitmap(bitmapconv)
    }
}