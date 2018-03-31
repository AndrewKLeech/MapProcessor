package com.example.andrew.maps

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.os.StrictMode
import java.io.*
import android.hardware.Camera
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract.EXTRA_INITIAL_URI
import android.util.Log
import android.view.View
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v4.app.FragmentActivity
import java.net.Inet4Address
import java.net.URI


class MainActivity : AppCompatActivity() {
    var mCurrentPhotoPath: String? = null
    var photoFile: File? = null
    var mCamera:Camera? = null
    var mPreview:Preview? = null
    private val READ_REQUEST_CODE = 42
    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        // Try load camera
        try{
            mCamera = Camera.open()
        } catch (e:Exception){
            Log.d("ERROR", "Failed to get camera: " + e.message)
        }
        if(mCamera != null){
            mPreview = Preview(this, mCamera!!)//create a surfaceview
            camera_view.addView(mPreview)
        }


        // On click listener for capture image (capture_img)
        capture_img.setOnClickListener {
            // Try create new blank .jpg file
            try {
                photoFile = ImageHandler().createImageFile(this)
                mCurrentPhotoPath = photoFile!!.absolutePath
            } catch (e: IOException) {
                Log.d("ERROR", "Could not create file " + e.message)
            }

            // Get current frame on camera and set the blank .jpg file as the frame
            mCamera!!.takePicture(null, null, Camera.PictureCallback { data, mCamera ->
                // Try set file (photoFile) as frame (data)
                try {
                    val fos = FileOutputStream(photoFile)
                    fos.write(data)
                    fos.close()
                    capture_img.visibility = View.INVISIBLE
                    gal_img_btn.visibility = View.INVISIBLE
                    done_img.visibility = View.VISIBLE
                    clear_img.visibility = View.VISIBLE
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            })
        }
        // On click listener for convert button (convert_btn)
        done_img.setOnClickListener {
            Toast.makeText(this, "Converting image", Toast.LENGTH_SHORT).show()
            // Reset image buttons so if user goes back they are set right
            capture_img.visibility = View.VISIBLE
            gal_img_btn.visibility = View.VISIBLE
            done_img.visibility = View.INVISIBLE
            clear_img.visibility = View.INVISIBLE
            // Intent for convert activity
            val convertIntent = Intent(this, ConvertActivity::class.java)
            // Create copy of image taken and get the path
            val cpy_img_path = ImageHandler().createImageCopy(photoFile!!, this)
            // Send original image path as extra in intent
            convertIntent.putExtra("src", mCurrentPhotoPath)
            // Send copy image path as extra in intent
            convertIntent.putExtra("img", cpy_img_path)
            // Start convert intent
            this.startActivity(convertIntent)
        }
        clear_img.setOnClickListener {
            // Restart the preview
            mCamera!!.startPreview()
            capture_img.visibility = View.VISIBLE
            gal_img_btn.visibility = View.VISIBLE
            done_img.visibility = View.INVISIBLE
            clear_img.visibility = View.INVISIBLE
        }
        gal_img_btn.setOnClickListener({
            performFileSearch()
        })

    }

    override fun onResume() {
        super.onResume()
        // Try load camera
        try{
            mCamera = Camera.open()
        } catch (e:Exception){
            Log.d("ERROR", "Failed to get camera: " + e.message)
        }
        if(mCamera != null){
            mPreview = Preview(this, mCamera!!)//create a surfaceview
            camera_view.addView(mPreview)
        }
    }

    /**
     * Adapted from https://developer.android.com/guide/topics/providers/document-provider.html
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    fun performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.type = "image/*"

        startActivityForResult(intent, READ_REQUEST_CODE)
    }

}
