package com.example.andrew.mapprocessor

import org.opencv.core.Mat
import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.android.synthetic.main.convert_screen.*
import android.widget.SeekBar
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Scalar

import org.opencv.imgproc.Imgproc

/**
 * Created by Andrew on 28/01/2018.
 */

class ConvertActivity : AppCompatActivity() {

    private var mSrcPhotoPath: String? = null
    var mCurrentPhotoPath: String? = null
    var photoFile: File? = null
    var hsV_lower = 89.0
    var hsV_upper = 106.0
    var hSv_lower = 0.0
    var hSv_upper = 0

    private fun setPic(path: String) {
        // Get the dimensions of the View
        val targetW = convert_map_img_view.width
        val targetH = convert_map_img_view.height

        // Get the dimensions of the bitmap
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, bmOptions)
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight
        // Determine how much to scale down the image
        val scaleFactor = Math.min(photoW / targetW, photoH / targetH)

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor
        bmOptions.inPurgeable = true

        val bitmap = BitmapFactory.decodeFile(path, bmOptions)
        val bitmap_cpy = bitmap.copy(bitmap.config, true)
        val bitmapconv =  PrepImage(bitmap_cpy)

        convert_map_img_view.setImageBitmap(bitmapconv)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
        val intent = intent
        mSrcPhotoPath = intent.getStringExtra("src")
        mCurrentPhotoPath = intent.getStringExtra("img")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.convert_screen)
        setSupportActionBar(toolbar)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        // Set starting progress of Value min bar to hsV_lower
        valLowerSeekBar.progress = hsV_lower.toInt()
        // Set starting progress of Value max bar to hsV_upper
        valUpperSeekBar.progress = hsV_upper.toInt()

        valLowerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
                mCurrentPhotoPath = mSrcPhotoPath
                hsV_lower = valLowerSeekBar.progress.toDouble()
                setPic(mCurrentPhotoPath!!)
                Log.d("I", "Value is: " + hsV_lower.toString())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // TODO Auto-generated method stub
            }
        })
        valUpperSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
                mCurrentPhotoPath = mSrcPhotoPath
                hsV_upper = valUpperSeekBar.progress.toDouble()
                setPic(mCurrentPhotoPath!!)
                Log.d("I", "Value is: " + hsV_upper.toString())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // TODO Auto-generated method stub
            }
        })
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setPic(mCurrentPhotoPath!!)
        }

    }

    fun PrepImage(src: Bitmap): Bitmap {

        var cpy = src.copy(src.config, true)
        var pixels = IntArray(cpy.height *cpy.width)
        cpy.getPixels(pixels, 0, cpy.width, 0, 0, cpy.width, cpy.height)

        //http://bagawerexecinux.cf/1305539/6566619/137c6080a-android-colorrgb-to-hsv-83344
        var mat = Mat()
        var cpyMat = Mat()
        var equCpy = Mat()
        var findBlack = Mat()
        var invMat = Mat()

        val bmp32 = src.copy(Bitmap.Config.ARGB_8888, true)
        Utils.bitmapToMat(bmp32, mat)
        Imgproc.cvtColor(mat, cpyMat, Imgproc.COLOR_BGR2HSV, 3) //3 is HSV Channel

        cpyMat.copyTo(findBlack)
        cpyMat.copyTo(equCpy)
        cpyMat.copyTo(invMat)

        // Try get features that we will remove from the image
        Core.inRange(equCpy, Scalar(0.0, hSv_lower, hsV_lower), Scalar(180.0, 255.0, hsV_upper), findBlack)

        // Invert segmentation
        Core.bitwise_not(findBlack,invMat)

        // Turn mat into bitmap to display in app
        Utils.matToBitmap(invMat, cpy)
        return cpy
    }
}
