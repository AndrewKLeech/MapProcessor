package com.example.andrew.mapprocessor

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.os.StrictMode
import java.text.SimpleDateFormat
import java.io.*
import java.util.*
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.util.Log
import android.view.View

class MainActivity : AppCompatActivity() {
    var mCurrentPhotoPath: String? = null
    var photoFile: File? = null
    var mCamera:Camera? = null
    var mPreview:Preview? = null

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    // Create an empty .JPEG file that will be used to store the picture taken
    // The name if the file will be JPEG_yyyyMMdd_HHmmSS_.jpg
    private fun createImageFile(): File {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
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
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    // Create copy of the image so that orignal image can be saved
    private fun createImageCopy(srcImg:File): String? {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
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
        mCurrentPhotoPath = image.absolutePath

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
        var dir = mCurrentPhotoPath
        return dir
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
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

        // On click listener for capture button (capture_btn)
        capture_btn.setOnClickListener {
            // Try create new blank .jpg file
            try {
                photoFile = createImageFile()
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
                    convert_btn.isEnabled = true
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            })
        }
        // On click listener for convert button (convert_btn)
        convert_btn.setOnClickListener {
            Toast.makeText(this, "Converting image", Toast.LENGTH_SHORT).show()
            // Intent for convert activity
            val convertIntent = Intent(this, ConvertActivity::class.java)
            // Create copy of image taken and get the path
            val cpy_img_path = createImageCopy(photoFile!!)
            // Send image path as extra in intent
            convertIntent.putExtra("img", cpy_img_path)
            // Start convert intent
            this.startActivity(convertIntent)
        }

        tweak_filter_btn.setOnClickListener{
            value_textbox.visibility = View.VISIBLE
            valSeekBar.visibility = View.VISIBLE
            saturation_txt_box.visibility = View.VISIBLE
            satSeekBar.visibility = View.VISIBLE
        }
    }

    //menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_edit_range -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
