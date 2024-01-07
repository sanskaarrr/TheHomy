package com.example.thehomyapp.activities


import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.thehomyapp.R
import com.example.thehomyapp.firebase.FirestoreClass
import com.example.thehomyapp.model.User
import com.example.thehomyapp.utils.LocationUtils
import com.example.thehomyapp.utils.LocationViewModel

import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseAuth


class DasboardActivity : BaseActivity() , OnNavigationItemSelectedListener{
    private lateinit var viewModel: LocationViewModel
    private lateinit var locationTextView: TextView
    private lateinit var getLocationButton: ImageView
    private lateinit var locationUtils: LocationUtils
    private lateinit var profile: ImageView
    private lateinit var drawer_layout_dashboard:DrawerLayout
    private lateinit var navi_view:NavigationView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dasboard)

        viewModel = ViewModelProvider(this).get(LocationViewModel::class.java)
        locationUtils = LocationUtils(this)

        locationTextView = findViewById(R.id.show_address)
        getLocationButton = findViewById(R.id.location)
        profile=findViewById((R.id.profile_user))
        drawer_layout_dashboard=findViewById(R.id.drawer_layout)
        navi_view=findViewById(R.id.nav_view)

        getLocationButton.setOnClickListener {
            if (locationUtils.hasLocationPermission()) {
                locationUtils.requestLocationUpdates(viewModel)
            } else {
                requestLocationPermission()
            }
        }
        viewModel.location.observe(this) { location ->
            location?.let {
                val address = locationUtils.reverseGeoCodeLocation(location)
                locationTextView.text = address
                locationTextView.visibility = TextView.VISIBLE
            } ?: run {
                locationTextView.text = "Location not available"
                locationTextView.visibility = TextView.VISIBLE
            }
        }

        profile.setOnClickListener{
            toggleDrawer()

        }
        navi_view.setNavigationItemSelectedListener(this)

        FirestoreClass().loadUserData(this)





    }
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                locationUtils.requestLocationUpdates(viewModel)
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun toggleDrawer(){
        if(drawer_layout_dashboard.isDrawerOpen(GravityCompat.START)){
            drawer_layout_dashboard.closeDrawer(GravityCompat.START)
        }else{
            drawer_layout_dashboard.openDrawer(GravityCompat.START)
        }

    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if(drawer_layout_dashboard.isDrawerOpen(GravityCompat.START)){
            drawer_layout_dashboard.closeDrawer(GravityCompat.START)
        }else{
            doubleBackToExit()
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.signout->{
                FirebaseAuth.getInstance().signOut()
                val intent= Intent(this,LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            R.id.nav_my_profile->{
                startActivity(Intent(this,ProfileActivity::class.java))
            }
            R.id.help_center->{
                openWhatsAppChat("+918310519564")
            }
        }
        drawer_layout_dashboard.closeDrawer(GravityCompat.START)
        return true
    }
    fun updateNavigationUserDetails(user: User) {
        // The instance of the header view of the navigation view.
        val headerView = navi_view.getHeaderView(0)

        // The instance of the user image of the navigation view.
        val navUserImage = headerView.findViewById<ImageView>(R.id.drawer_image)

        // Load the user image in the ImageView.

        Glide
            .with(this)
            .load(user.image) // URL of the image
            .centerCrop() // Scale type of the image.
            .placeholder(R.drawable.profile) // A default place holder
            .into(navUserImage) // the view in which the image will be loaded.
        val username=findViewById<TextView>(R.id.tv_username_drawer)
        username.text=user.name

    }
    fun openWhatsAppChat(phoneNumber: String) {
        try {
            // Format the phone number by removing non-numeric characters
            val formattedPhoneNumber = phoneNumber.replace("[^0-9]".toRegex(), "")

            // Create a URI for the WhatsApp chat link
            val uri = Uri.parse("https://wa.me/$formattedPhoneNumber")

            // Create an Intent with the ACTION_VIEW action and the specified URI
            val intent = Intent(Intent.ACTION_VIEW, uri)

            // Start the Intent (assuming this function is called within an activity)
            startActivity(intent)

        } catch (e: Exception) {
            // Handle exceptions if WhatsApp is not installed or other issues
            e.printStackTrace()
            // You might want to notify the user or log the error
        }
    }

}
