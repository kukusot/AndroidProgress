package com.kukusot.progess

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        typingDynamic.apply {
            circleRadius = 25f
            circleTravel = 150f
            circleSpacing = 20f
            animationDuration = 1500
            numDots = 4
            circleColor = Color.CYAN
        }
    }
}
