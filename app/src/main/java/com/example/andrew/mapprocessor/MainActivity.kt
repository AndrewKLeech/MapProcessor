package com.example.andrew.mapprocessor

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
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
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.preview.*
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth





class MainActivity : AppCompatActivity() {
    var mCurrentPhotoPath: String? = null
    var photoFile: File? = null
    var mCamera:Camera? = null
    var mPreview:Preview? = null
    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)


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
            done_img.visibility = View.INVISIBLE
            clear_img.visibility = View.INVISIBLE
        }
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
