package com.example.andrew.maps

import android.content.Intent
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

    // FOR DEBUGGING
    // Save segmented image to file
    val SAVE_SEGMENTED_IMAGE = false
    // Save thinned image to file
    val SAVE_THINNED_IMAGE = false

    // Paths for photo files
    private var mSrcPhotoPath: String? = null
    private var mCurrentPhotoPath: String? = null

    // Bitmap of segmented image
    var bitmapconv: Bitmap? = null

    // H
    var Hsv_lower = 76.0
    var Hsv_upper = 120.0

    // S
    var hSv_lower = 15.0
    var hSv_upper = 255.0

    // V
    var hsV_lower = 90.0
    var hsV_upper = 152.0

    override fun onCreate(savedInstanceState: Bundle?) {

        // Try load OpenCV
        if (!OpenCVLoader.initDebug()) {
            Log.d("E", "OpenCV initialization error" + hsV_lower.toString())
        }

        // used to check if user is done this step
        var doneConvert = false

        // Get photo paths sent with intent
        val intent = intent
        mSrcPhotoPath = intent.getStringExtra("src")
        mCurrentPhotoPath = mSrcPhotoPath

        super.onCreate(savedInstanceState)
        setContentView(R.layout.convert_screen)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())


        // Set starting progress of Value min bar to hsV_lower
        hue_min.progress = Hsv_lower.toInt()

        // Set starting progress of Value max bar to hsV_upper
        hue_max.progress = Hsv_upper.toInt()

        // Set starting progress of Value min bar to hsV_lower
        satmin.progress = hSv_lower.toInt()

        // Set starting progress of Value max bar to hsV_upper
        satmax.progress = hSv_upper.toInt()

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
                Log.d("I", "hsV_lower is: " + hsV_lower.toString())
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
                Log.d("I", "hsV_upper is: " + hsV_upper.toString())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do nothing
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                //Do nothing
            }
        })

        // Listener for hue min
        hue_min.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                mCurrentPhotoPath = mSrcPhotoPath
                Hsv_lower = hue_min.progress.toDouble()
                setPic(mCurrentPhotoPath!!)
                Log.d("I", "Hsv_lower is: " + Hsv_lower.toString())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do nothing
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                //Do nothing
            }
        })

        // Listener for hue min
        hue_max.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                mCurrentPhotoPath = mSrcPhotoPath
                Hsv_upper = hue_max.progress.toDouble()
                setPic(mCurrentPhotoPath!!)
                Log.d("I", "Hsv_upper is: " + Hsv_upper.toString())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do nothing
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                //Do nothing
            }
        })

        // Listener for sat min
        satmin.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                mCurrentPhotoPath = mSrcPhotoPath
                hSv_lower = satmin.progress.toDouble()
                setPic(mCurrentPhotoPath!!)
                Log.d("I", "hSv_lower is: " + hSv_lower.toString())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do nothing
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                //Do nothing
            }
        })

        // Listener for sat max
        satmax.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                mCurrentPhotoPath = mSrcPhotoPath
                hSv_upper = satmax.progress.toDouble()
                setPic(mCurrentPhotoPath!!)
                Log.d("I", "hSv_upper is: " + hSv_upper.toString())
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

            if(SAVE_SEGMENTED_IMAGE){
                FileHandler().saveImage(bitmapconv!!, "segmented", this)
            }
            var thinnedBitmap = ImageProcessor().thin(bitmapconv!!)
            convert_map_img_view.setImageBitmap(thinnedBitmap)


            // Save file of thinned image
            if(SAVE_THINNED_IMAGE){
                FileHandler().saveImage(thinnedBitmap, "thinned", this)
            }

            // Change visibility of UI options
            hsV_Max_txt.visibility = View.INVISIBLE
            hsV_Min_txt.visibility = View.INVISIBLE
            valUpperSeekBar.visibility = View.INVISIBLE
            valLowerSeekBar.visibility = View.INVISIBLE
            cancel_img_btn.visibility = View.VISIBLE

            if(doneConvert){
                // Intent for convert activity
                val displayModelIntent = Intent(this, DisplayModelActivity::class.java)


                // Start convert intent
                this.startActivity(displayModelIntent)
            }
            // Set doneConvert to true so when the done button is hit
            // a second time in a row the next intent is launched
            doneConvert = true
        })

        // Listener for cancel button
        cancel_img_btn.setOnClickListener {

            // Change visibility of UI options
            hsV_Max_txt.visibility = View.VISIBLE
            hsV_Min_txt.visibility = View.VISIBLE
            valUpperSeekBar.visibility = View.VISIBLE
            valLowerSeekBar.visibility = View.VISIBLE
            cancel_img_btn.visibility = View.INVISIBLE

            // Set doneConvert to false to make sure next intent is
            // Only launched when done button is hit twice in a row
            doneConvert = false

            // Redisplay original segmented image before thinning occurred
            setPic(mCurrentPhotoPath!!)
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

        // segment image
        bitmapconv =  ImageProcessor().segment(bitmap_cpy, hsV_lower, hsV_upper, Hsv_lower, Hsv_upper, hSv_lower, hSv_upper)

        // Show bitmap
        convert_map_img_view.setImageBitmap(bitmapconv)
    }
}