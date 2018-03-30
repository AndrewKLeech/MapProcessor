package com.example.andrew.maps

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class ImageProcessor {

    fun PrepImage(src: Bitmap, hSv_lower:Double, hsV_lower:Double, hsV_upper:Double): Bitmap {

        //http://bagawerexecinux.cf/1305539/6566619/137c6080a-android-colorrgb-to-hsv-83344
        var mat = Mat()
        var resized = Mat()
        var cpyMat = Mat()
        var equCpy = Mat()
        var findBlack = Mat()
        var invMat = Mat()

        val bmp32 = src.copy(Bitmap.Config.ARGB_8888, true)
        Utils.bitmapToMat(bmp32, mat)
        var size = Size(1200.0, 900.0)
        Imgproc.resize(mat, resized, size)
        Imgproc.cvtColor(resized, cpyMat, Imgproc.COLOR_BGR2HSV, 3) //3 is HSV Channel

        cpyMat.copyTo(findBlack)
        cpyMat.copyTo(equCpy)
        cpyMat.copyTo(invMat)

        // Try get features that we will remove from the image
        Core.inRange(equCpy, Scalar(0.0, hSv_lower, hsV_lower), Scalar(70.0, 255.0, hsV_upper), findBlack)


        // Invert segmentation
        //Core.bitwise_not(findBlack,invMat)

        var newBitmap: Bitmap = Bitmap.createBitmap(findBlack.width(),findBlack.height(), src.config)
        // Turn mat into bitmap to display in app
        Utils.matToBitmap(findBlack, newBitmap)
        return newBitmap!!
    }


    //Addpted from http://felix.abecassis.me/2011/09/opencv-morphological-skeleton/
    fun thin(bitmap: Bitmap): Bitmap{
        var mat = Mat()
        // Convert bitmap to mat
        Utils.bitmapToMat(bitmap, mat)

        // Split channels of mat as to only use one
        var channels = List<Mat>(mat.channels(), {Mat()})
        Core.split(mat, channels)
        var ch1 = channels[0]

        // Change values from 0 and 255 to 0 and 1
        Imgproc.threshold(ch1,ch1, 127.0, 255.0, Imgproc.THRESH_BINARY)

        // New mat that will hold the output skeleton
        var skel = Mat.zeros(ch1.size(), ch1.type())
        var temp = Mat(ch1.size(), ch1.type())


        // structured element (kernal)
        var element = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, Size(3.0,3.0))

        var done = false

        // Do thinning
        do {
            Imgproc.morphologyEx(ch1, temp, Imgproc.MORPH_OPEN, element)
            Core.bitwise_not(temp, temp)
            Core.bitwise_and(ch1, temp, temp)
            Core.bitwise_or(skel, temp, skel)
            Imgproc.erode(ch1, ch1, element)

            var max: Double? = null

            // get max value of mat being thinned
            max = Core.minMaxLoc(ch1).maxVal

            // if all values are 0 (max will be 0) exit loop
            done = (max == 0.0)
        }while (!done)

        // Create new bitmap to hold values of skeleton mat
        var newBitmap = bitmap.copy(bitmap.config,true)

        // Turn skeletion mat to bitmap
        Utils.matToBitmap(skel, newBitmap)

        // return thinned bitmap
        return newBitmap
    }
}