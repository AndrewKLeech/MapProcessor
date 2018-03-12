package com.example.andrew.mapprocessor
import org.opencv.core.Mat
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.convert_screen.*
import android.opengl.ETC1.getWidth
import android.util.Log
import android.widget.SeekBar
import org.opencv.android.OpenCVLoader
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Scalar

import org.opencv.imgproc.Imgproc
import org.opencv.core.CvType




/**
 * Created by Andrew on 28/01/2018.
 */

class ConvertActivity : AppCompatActivity() {

    var mCurrentPhotoPath: String? = null
    var photoFile: File? = null
    var hsV_lower = 80.0
    var hsV_upper = 255
    var hSv_lower = 0
    var hSv_upper = 0

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

        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
        val intent = intent
        mCurrentPhotoPath = intent.getStringExtra("img")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.convert_screen)
        setSupportActionBar(toolbar)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        valSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // TODO Auto-generated method stub
                hsV_lower = valSeekBar.verticalScrollbarPosition.toDouble()
            }
        })
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setPic()
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

        val bmp32 = src.copy(Bitmap.Config.ARGB_8888, true)
        Utils.bitmapToMat(bmp32, mat)
        Imgproc.cvtColor(mat, cpyMat, Imgproc.COLOR_BGR2HSV, 3) //3 is HSV Channel
        var findBlack = Mat()
        var findWhite = Mat()
        var whiteInv = Mat()
        var outMat = Mat()
        cpyMat.copyTo(findBlack)
        cpyMat.copyTo(findWhite)
        cpyMat.copyTo(equCpy)
        //Imgproc.equalizeHist(cpyMat,equCpy)
        //V<0.25
        Core.inRange(equCpy, Scalar(0.0, 0.0, hsV_lower), Scalar(180.0, 255.0, 255.0), findBlack)
        //S<0.20 AND V>0.60
        Core.inRange(equCpy, Scalar(0.0, 0.0, 153.0), Scalar(180.0, 51.0, 255.0), findWhite)
        System.out.println("TEMP")
        //System.out.println(findWhite.dump())

        //whiteInv = findWhite.inv()
        Core.add(findBlack, findWhite, outMat)
        Utils.matToBitmap(findWhite, cpy)
        return cpy
    }
}
