package com.enveramil.savelocation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.enveramil.savelocation.databinding.ActivityMapsBinding
import com.google.android.gms.common.wrappers.Wrappers
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.material.snackbar.Snackbar

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener : LocationListener
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private var tracker : Boolean = false
    private var selectedLatitude : Double? = null
    private var selectedLongitude : Double? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        registerLauncher()
        selectedLatitude = 0.0
        selectedLongitude = 0.0
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {

/*
        // latitude : 41.1892751 , longitude : 29.0429287
        val getLocationWithLatLng = LatLng(41.1892751,29.0429287)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getLocationWithLatLng,15f))
        mMap.addMarker(MarkerOptions().position(getLocationWithLatLng).title("Acarlar Sitesi"))

 */

        mMap = googleMap

        mMap.setOnMapLongClickListener(this) // LongClickListener kullanacağımızı belirtmemiz lazım.

        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val latitude = location.latitude
                val longitude = location.longitude
                val userLocation = LatLng(latitude,longitude)

                if (tracker == false){
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15f))
                    //mMap.addMarker(MarkerOptions().position(userLocation).title("User Location"))
                }
                tracker = true


            }
        }

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // permission denied
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                Snackbar.make(binding.root,"Permission needed for location",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                    // request permission
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()
            }else{
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }else{
            // permission granted
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastKnownLocation != null){
                val lastLocation = LatLng(lastKnownLocation.latitude,lastKnownLocation.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation,15f))
                //mMap.addMarker(MarkerOptions().position(lastLocation).title("User Location"))
            }
            mMap.isMyLocationEnabled = true
        }
    }
    fun registerLauncher(){
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            result ->
            if (result){
                // permission granted
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
                    val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (lastKnownLocation != null){
                        val lastLocation = LatLng(lastKnownLocation.latitude,lastKnownLocation.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation,15f))
                        //mMap.addMarker(MarkerOptions().position(lastLocation).title("User Location"))
                    }
                    mMap.isMyLocationEnabled = true
                }
            }else{
                // permission denied
                Toast.makeText(this,"Lokasyon için izin yok",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onMapLongClick(longClick: LatLng) {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(longClick).icon(getBitmapDescriptorFromVector(this,R.drawable.ic_round_add_location_24)))
        selectedLatitude = longClick.latitude
        selectedLongitude = longClick.longitude
    }
    fun getBitmapDescriptorFromVector(context: Context, @DrawableRes vectorDrawableResourceId: Int): BitmapDescriptor? {
        var height : Int = 250
        var width : Int = 250
        val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable!!.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable!!.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}