package com.example.thehomyapp.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import com.example.thehomyapp.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class LoginActivity : BaseActivity() {
    private lateinit var imageView: ImageView
    private lateinit var showdetails: LinearLayout
    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId: String
    private lateinit var phoneNumber:String
    private lateinit var verficationbtn:Button
    private lateinit var progress_bar:ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        imageView = findViewById(R.id.logo_login)
        showdetails = findViewById(R.id.show_details)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        auth = FirebaseAuth.getInstance()


        verficationbtn =findViewById(R.id.get_otp)
        progress_bar=findViewById(R.id.progress_sendotp)
        val Phonenum: EditText =findViewById(R.id.phoneNumber)

        verficationbtn.setOnClickListener {
            progress_bar.visibility= View.VISIBLE
            verficationbtn.visibility=View.INVISIBLE
            Phonenum.isEnabled = false
            phoneNumber = Phonenum.text.toString()
            if(phoneNumber.length==10) {
                sendVerificationCode(phoneNumber)
            }
            else{
                showErrorSnackBar("Please Enter a valid number")
                progress_bar.visibility= View.INVISIBLE
                verficationbtn.visibility=View.VISIBLE
                Phonenum.isEnabled = true

            }

        }




        Handler().postDelayed({
            moveImageViewUp()
        }, 1000)
    }
    private fun moveImageViewUp() {
        val animation = TranslateAnimation(0f, 0f, 0f, -300f) // Adjust the distance to move up
        animation.duration = 500 // Adjust the duration
        animation.fillAfter = true

        animation.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
            override fun onAnimationStart(animation: android.view.animation.Animation?) {}

            override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                // Show the text after the animation completes
                showdetails.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
        })

        imageView.startAnimation(animation)
    }


    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            progress_bar.visibility= View.GONE
            verficationbtn.visibility= View.VISIBLE

            val intent = Intent(this@LoginActivity, VerificationActivity::class.java)
            intent.putExtra("phoneNumber",phoneNumber)
            intent.putExtra("phoneAuthCredential", credential)
            startActivity(intent)
            finish()

        }

        override fun onVerificationFailed(e: FirebaseException) {
            progress_bar.visibility= View.GONE
            verficationbtn.visibility= View.VISIBLE
            Toast.makeText(this@LoginActivity,e.message, Toast.LENGTH_LONG).show()
            Log.e("VerificationFailed", e.message, e)
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            progress_bar.visibility= View.GONE
            verficationbtn.visibility= View.VISIBLE
            storedVerificationId = verificationId
            val intent = Intent(this@LoginActivity, VerificationActivity::class.java)
            intent.putExtra("phoneNumber",phoneNumber)
            intent.putExtra("phoneAuthCredential", storedVerificationId)
            startActivity(intent)

        }
    }
}

