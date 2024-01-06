package com.example.thehomyapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.thehomyapp.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

open class BaseActivity : AppCompatActivity() {
    private var doublebacktoexitpressedonce =false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }
    fun CurrentUserID(user: FirebaseUser?): String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
    fun doubleBackToExit(){
        if(doublebacktoexitpressedonce==true){
            super.onBackPressed()
            return
        }
        this.doublebacktoexitpressedonce=true
        Toast.makeText(this,resources.getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_LONG).show()
        Handler().postDelayed({doublebacktoexitpressedonce=false},2000)
    }
    open fun showErrorSnackBar(message: String){
        val snackBar= Snackbar.make(findViewById(android.R.id.content),message, Snackbar.LENGTH_LONG)
        val snackBar_view=snackBar.view
        snackBar_view.setBackgroundColor(ContextCompat.getColor(this, R.color.app_theme))
        snackBar.show()

    }
}