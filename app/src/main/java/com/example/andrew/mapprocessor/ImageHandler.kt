package com.example.andrew.mapprocessor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import org.opencv.core.CvType.CV_8UC1
import org.opencv.core.Mat
import org.opencv.core.Point
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import org.opencv.core.Core
import org.opencv.core.Core.absdiff
import org.opencv.core.Core.countNonZero
import java.awt.image.DataBufferByte
import java.awt.image.BufferedImage



/**
* Created by Andrew on 12/03/2018.
*/
class ImageHandler {
    // Create copy of the image so that orignal image can be saved
    fun createImageCopy(srcImg: File, ctx: Context): String? {
        val storageDir = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        // Get name of source image file
        val srcImageFileName = srcImg.name
        // Create new string of "COPY_" plus the orignal file name
        val cpyImageFileName = "COPY_$srcImageFileName"
        // Create new blank .jpg file
        val image = File.createTempFile(
                cpyImageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )
        // Save a file: path for use with ACTION_VIEW intents
        val dir = image.absolutePath

        // Copy orignal image to new image file
        val fin = FileInputStream(srcImg)
        try {
            val out = FileOutputStream(image)
            try {
                // Transfer bytes from in to out
                val buf = ByteArray(1024)

                while (true) {
                    var len = fin.read(buf)
                    if(len <= 0){
                        break
                    }
                    out.write(buf, 0, len)
                }
            } finally {
                out.close()
            }
        } finally {
            fin.close()
        }
        return dir
    }

    @SuppressLint("SimpleDateFormat")
// Create an empty .JPEG file that will be used to store the picture taken
    // The name if the file will be JPEG_yyyyMMdd_HHmmSS_.jpg
    fun createImageFile(ctx: Context): File {
        val storageDir = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        // Create empty file
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )
        // Save a file: path for use with ACTION_VIEW intents
        return image
    }
    // Addapted from https://web.archive.org/web/20160322113207/http://opencv-code.com/quick-tips/implementation-of-thinning-algorithm-in-opencv/
    fun thinningIteration(im: Mat, iter: Int): Mat
    {
        var marker: Mat = Mat.zeros(im.size(), CV_8UC1)
        val length = (im.total() * im.elemSize()).toInt()
        val buffer = ByteArray(length)
        im.get(0, 0, buffer)

        for (i in 0 until im.rows())
        {
            for (j in 0 until im.cols())
            {
                var p2: Byte = buffer[(j+im.cols()*i)]
                var p3: Byte = buffer[(j+1)+im.cols()*(i-1)]
                var p4 = buffer[(j+1)+im.cols()*(i)]
                var p5 = buffer[(j+1)+im.cols()*(i+1)]
                var p6 = buffer[(j)+im.cols()*(i+1)]
                var p7 = buffer[(j-1)+im.cols()*(i+1)]
                var p8 = buffer[(j-1)+im.cols()*(i)]
                var p9 = buffer[(j-1)+im.cols()*(i-1)]
                var A = 0
                if(p2 == 0.toByte() && p3 == 1.toByte()) {
                    A++
                }
                if((p3 == 0.toByte()  && p4 == 1.toByte() )) {
                    A++
                }
                if(p4 == 0.toByte()  && p5 == 1.toByte() ) {
                }
                if(p5 == 0.toByte()  && p6 == 1.toByte() ){}
                if(p6 == 0.toByte()  && p7 == 1.toByte() ) {
                }
                if(p7 == 0.toByte()  && p8 == 1.toByte() ) {
                }
                if(p8 == 0.toByte()  && p9 == 1.toByte() ) {
                }
                if(p9 == 0.toByte()  && p2 == 1.toByte() ){

                }
                var B  = p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9
                var m1 = if(iter == 0){
                    (p2 * p4 * p6)
                }
                else{
                    (p2 * p4 * p8)
                }
                var m2 = if(iter == 0) {
                    (p4 * p6 * p8)
                }
                else{
                    (p2 * p6 * p8)
                }

                if (A == 1 && (B >= 2 && B <= 6) && m1 == 0 && m2 == 0){
                    buffer[im.cols()*i + j] = 1
                }

            }
        }

        return marker
    }

    /**
     * Function for thinning the given binary image
     *
     * @param  im  Binary image with range = 0-255
     */
    fun thinning(im: Mat)
    {
        im /= 255

        var prev: Mat  = Mat.zeros(im.size(), CV_8UC1);
        var diff: Mat? = null

        do {
            thinningIteration(im, 0)
            thinningIteration(im, 1)
            absdiff(im, prev, diff)
            im.copyTo(prev)
        }
        while (countNonZero(diff) > 0)

        im *= 255
    }
}