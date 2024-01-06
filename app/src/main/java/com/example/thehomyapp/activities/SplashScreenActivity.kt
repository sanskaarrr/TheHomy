package com.example.thehomyapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import com.example.thehomyapp.R


class SplashScreenActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        Handler().postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        },2500)

        val imageView = findViewById<ImageView>(R.id.logo_splash)
        bounceAnimation(imageView, 0)


    }
    private fun bounceAnimation(view: View, bounceCount: Int) {
        if (bounceCount < 5) { // Set the desired number of bounces (here, 2 bounces)
            val translationY = if (bounceCount % 2 == 0) 50f else -50f // Alternate direction

            // Bounce Animation
            view.animate()
                .translationYBy(translationY)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setDuration(500) // Adjust the duration
                .withEndAction {
                    // Recursively start the next bounce
                    bounceAnimation(view, bounceCount + 1)
                }
                .start()
        }
    }
}