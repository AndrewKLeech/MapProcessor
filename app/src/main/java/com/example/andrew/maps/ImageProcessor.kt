package com.example.andrew.maps

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Imgproc.*

class ImageProcessor {

    // Blur types
    val GAUSSIAN = 0
    val MEDIAN = 1
    val BLUR = 2

    /*
    * segment() segments a bitmap by color using arguments passed to set the color range
    * in which to segment the bitmap.
    * segment() returns a black and white Bitmap.
    */
    fun segment(src: Bitmap, hsV_lower:Double, hsV_upper:Double, Hsv_lower: Double, Hsv_upper: Double, hSv_lower: Double, hSv_upper: Double): Bitmap {

        // Initialize Mats
        var mat = Mat()
        var resized = Mat()
        var hsvMat = Mat()
        var findBlack = Mat()

        // Turn bitmap to Mat
        val bmp32 = src.copy(Bitmap.Config.ARGB_8888, true)
        Utils.bitmapToMat(bmp32, mat)

        // Resize image smaller for thinning accuracy later
        var size = Size(1200.0, 900.0)
        Imgproc.resize(mat, resized, size)

        // Create new bitmap to return
        var blured: Mat

        blured = blur(resized, GAUSSIAN)

        //Change color space to has
        Imgproc.cvtColor(blured, hsvMat, Imgproc.COLOR_BGR2HSV, 3) //3 is HSV Channel

        // Copy hsvMat to findBlack so they are the same size
        hsvMat.copyTo(findBlack)

        // Try get features that we will remove from the image
        Core.inRange(hsvMat, Scalar(Hsv_lower, hSv_lower, hsV_lower), Scalar(Hsv_upper, hSv_upper, hsV_upper), findBlack)

        // Create new bitmap to return
        var newBitmap: Bitmap = Bitmap.createBitmap(blured.width(),blured.height(), src.config)

        // Turn mat back to bitmap
        Utils.matToBitmap(findBlack, newBitmap)
        return newBitmap
    }

    /*
        thin() takes a Bitmap as an argument and performs iterations of a morphological
        thinning operation to return a skeleton of the original Bitmap.
        Adapted from http://felix.abecassis.me/2011/09/opencv-morphological-skeleton/
    */
    fun thin(bitmap: Bitmap): Bitmap{

        // Convert bitmap to mat
        var mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        // Split channels of mat as to only use one
        var channels = List(mat.channels(), {Mat()})
        Core.split(mat, channels)
        var ch1 = channels[0]

        // Change values from 0 and 255 to 0 and 1
        Imgproc.threshold(ch1,ch1, 127.0, 255.0, Imgproc.THRESH_BINARY)

        // New mat that will hold the output skeleton
        var skel = Mat.zeros(ch1.size(), ch1.type())
        var temp = Mat(ch1.size(), ch1.type())
        var erode = Mat(ch1.size(), ch1.type())

        /* structured element (kernel)
                  ___ ___ ___
                 |   | 1 |   |
                 |___|___|___|
                 | 1 | 1 | 1 |
                 |___|___|___|
                 |   | 1 |   |
                 |___|___|___|
         */
        val element = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, Size(3.0,3.0))

        // Start as not done (false)
        var done: Boolean

        // Do thinning
        do {
            // MORPH_OPEN = erode -> dilate
            /*Imgproc.morphologyEx(ch1, temp, Imgproc.MORPH_OPEN, element)
            Core.bitwise_not(temp, temp)
            Core.bitwise_and(ch1, temp, temp)
            Core.bitwise_or(skel, temp, skel)
            Imgproc.erode(ch1, ch1, element)*/

            Imgproc.erode(ch1, erode, element)
            Imgproc.dilate(erode, temp, element)
            Core.subtract(ch1, temp, temp)
            Core.bitwise_or(skel, temp, skel)
            erode.copyTo(ch1)

            // get max value of mat being thinned
            var max: Double?
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

    /* unused function for a different approach to thinning*/
    fun thin2(bitmap: Bitmap): Bitmap{

        var hierarchy = Mat()
        var mat = Mat()


        Utils.bitmapToMat(bitmap, mat)

        // Split channels of mat as to only use one
        var channels = List(mat.channels(), {Mat()})
        Core.split(mat, channels)
        System.out.println("No Of Channels: " + channels.size)
        var ch1 = channels[0]

        // Detect edges
        Canny(ch1,ch1,127.0, 255.0)
        System.out.println("Canny complete")

        var contours = arrayListOf(MatOfPoint())
        // RETR_TREE finds all contours
        findContours(ch1, contours, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE)
        System.out.println("findContours complete")

        var drawing = Mat(mat.size(), mat.type(), Scalar(0.0,0.0,0.0, 255.0))

        for (i in 1 until contours.size){
            drawContours(drawing,contours,i, Scalar(255.0,255.0,255.0, 255.0), 0, 8, hierarchy, 0, Point())
        }


        // Create new bitmap to hold values of skeleton mat
        var newBitmap = bitmap.copy(bitmap.config,true)


        var outMat = Mat(mat.size(), mat.type(), Scalar(0.0,0.0,0.0, 255.0))
        //Core.bitwise_or(outMat,drawing,outMat)
        // Turn skeletion mat to bitmap
        Utils.matToBitmap(drawing, newBitmap)

        // return thinned bitmap
        return newBitmap
    }


    fun blur(mat: Mat, blurType: Int): Mat{

        var blur = Mat(mat.size(), mat.type())

        // Gaussian Blur
        if(blurType == 0) {
            for (i in 1 until 10 step 2) {
                GaussianBlur(mat, blur, Size(i.toDouble(), i.toDouble()), 0.0, 0.0)
            }
        }

        // Median Blur
        if(blurType == 1) {
            for (i in 1 until 10 step 2) {
                medianBlur(mat, blur, i)
            }
        }

        // Blur
        if(blurType == 2) {
            for (i in 1 until 10 step 2) {
                blur(mat, blur, Size(i.toDouble(), i.toDouble()))
            }
        }

        return blur
    }
}