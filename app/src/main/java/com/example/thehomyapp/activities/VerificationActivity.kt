package com.example.thehomyapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.thehomyapp.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class VerificationActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId: String
    private lateinit var otpInputs: List<EditText>
    private lateinit var phoneNum: String

    private lateinit var resendCodeBtn: TextView
    private lateinit var countdownTimer: CountDownTimer
    private lateinit var countdownText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        auth = FirebaseAuth.getInstance()
        initializeViews()

        storedVerificationId = intent.getStringExtra("phoneAuthCredential") ?: ""

        val verifyBtn: Button = findViewById(R.id.verify_otp)
        verifyBtn.setOnClickListener {
            val otp = otpInputs.joinToString("") { it.text.toString().trim() }
            val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp)
            signInWithPhoneAuthCredential(credential)
        }

        resendCodeBtn = findViewById(R.id.resend_otp)
        countdownText = findViewById(R.id.countdownText)

        resendCodeBtn.setOnClickListener {
            resendVerificationCode(phoneNum)
        }

        startCountdownTimer()
    }

    @SuppressLint("SetTextI18n")
    private fun initializeViews() {
        otpInputs = listOf(
            findViewById(R.id.inputOTP1),
            findViewById(R.id.inputOTP2),
            findViewById(R.id.inputOTP3),
            findViewById(R.id.inputOTP4),
            findViewById(R.id.inputOTP5),
            findViewById(R.id.inputOTP6)
        )

        val phoneNumber: TextView = findViewById(R.id.phoneNumber_display)
        phoneNumber.text = "+91${intent.getStringExtra("phoneNumber") ?: ""}"
        phoneNum = intent.getStringExtra("phoneNumber") ?: ""

        setupOtpTextWatchers()
    }

    private fun setupOtpTextWatchers() {
        otpInputs.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(cs: CharSequence, start: Int, before: Int, count: Int) {}

                override fun onTextChanged(cs: CharSequence, start: Int, before: Int, count: Int) {
                    if (cs.length == 1 && index < otpInputs.size - 1) {
                        otpInputs[index + 1].requestFocus()
                    } else if (cs.isEmpty() && start == 0 && index > 0) {
                        otpInputs[index - 1].requestFocus()
                    }
                }

                override fun afterTextChanged(editable: Editable) {}
            })
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("VerificationActivity", "signInWithCredential: success")
                    val user = task.result?.user
                    CurrentUserID(user)
                    redirectToHomepage()
                } else {
                    Log.w("VerificationActivity", "signInWithCredential: failure", task.exception)
                }
            }
    }

    private fun resendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(getPhoneAuthCallbacks())
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

        // Disable the button and start the countdown timer
        resendCodeBtn.visibility=View.INVISIBLE
        countdownText.visibility=View.VISIBLE
        startCountdownTimer()
    }

    private fun startCountdownTimer() {
        countdownTimer = object : CountDownTimer(60000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                countdownText.text ="0:$secondsRemaining"
            }

            override fun onFinish() {
                countdownText.visibility=View.INVISIBLE
                resendCodeBtn.visibility= View.VISIBLE
            }
        }.start()
    }

    private fun redirectToHomepage() {
        val intent = Intent(this@VerificationActivity, DasboardActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun getPhoneAuthCallbacks(): PhoneAuthProvider.OnVerificationStateChangedCallbacks {
        return object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Handle the case where verification is automatically completed
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w("VerificationActivity", "onVerificationFailed", e)
                // Handle the case where verification fails
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                storedVerificationId = verificationId
                // Handle the case where the verification code is sent to the user
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countdownTimer.cancel() // Cancel the countdown timer to avoid leaks
    }
}
