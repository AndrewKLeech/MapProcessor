package com.example.andrew.maps

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
* Created by Andrew on 12/03/2018.
 * Used to create new black image files and create copies of existing image files
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
    fun createImageFile(ctx: Context, fileHead: String): File {
        val storageDir = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = fileHead + "_" + timeStamp + "_"
        // Create empty file
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )
        // Save a file: path for use with ACTION_VIEW intents
        return image
    }

    fun saveImage(bitmap: Bitmap, fileHead: String, ctx: Context){

        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(createImageFile(ctx, fileHead))
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                if (out != null) {
                    out.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}