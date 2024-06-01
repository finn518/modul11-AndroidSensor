package com.example.pam_gps

import android.Manifest
import android.content.pm.PackageManager
import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var latitude: TextView
    private lateinit var longitude: TextView
    private lateinit var altitude: TextView
    private lateinit var akurasi: TextView
    private lateinit var alamat: TextView
    private lateinit var btnFind: Button
    private lateinit var locationProviderClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        latitude = findViewById(R.id.latitude)
        longitude = findViewById(R.id.longitude)
        altitude = findViewById(R.id.altitude)
        alamat = findViewById(R.id.alamat)
        akurasi = findViewById(R.id.akurasi)
        btnFind = findViewById(R.id.btn_find)
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())

        btnFind.setOnClickListener {
            getLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                Toast.makeText(this, "Izin lokasi tidak diaktifkan!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), 10)
        } else {
            locationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    latitude.text = location.latitude.toString()
                    longitude.text = location.longitude.toString()
                    altitude.text = location.altitude.toString()
                    akurasi.text = "${location.accuracy}%"

                    try {
                        val addresses: List<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (!addresses.isNullOrEmpty()) {
                            alamat.text = addresses[0].getAddressLine(0)
                        } else {
                            alamat.text = "Alamat tidak ditemukan"
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        alamat.text = "Tidak dapat mengambil alamat"
                    }
                } else {
                    Toast.makeText(this, "Lokasi tidak aktif!", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e: Exception ->
                Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
