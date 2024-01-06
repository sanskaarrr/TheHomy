package com.example.thehomyapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.thehomyapp.utils.LocationUtils
import com.example.thehomyapp.utils.LocationViewModel
import com.example.thehomyapp.R


class DasboardActivity : AppCompatActivity() {
    private lateinit var viewModel: LocationViewModel
    private lateinit var locationTextView: TextView
    private lateinit var getLocationButton: ImageView
    private lateinit var locationUtils: LocationUtils
    private lateinit var profile: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dasboard)

        viewModel = ViewModelProvider(this).get(LocationViewModel::class.java)
        locationUtils = LocationUtils(this)

        locationTextView = findViewById(R.id.show_address)
        getLocationButton = findViewById(R.id.location)
        profile=findViewById((R.id.profile_user))

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
            
        }






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

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}
