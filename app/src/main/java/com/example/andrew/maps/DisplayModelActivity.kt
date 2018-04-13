package com.example.andrew.maps

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class DisplayModelActivity: AppCompatActivity() {

    private var mGLView: GLSurfaceView? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        mGLView = MyGLSurfaceView(this)
        setContentView(mGLView)
    }
}