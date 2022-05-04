package com.enveramil.savelocation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.enveramil.savelocation.databinding.ActivityMapsBinding
import com.enveramil.savelocation.roomdb.LocationDao
import com.enveramil.savelocation.roomdb.LocationDatabase
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener : LocationListener
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private lateinit var sharedPreferences : SharedPreferences
    private var tracker : Boolean? = null
    private var selectedLatitude : Double? = null
    private var selectedLongitude : Double? = null
    private lateinit var db : LocationDatabase
    private lateinit var dao : LocationDao
    val compositeDisposable = CompositeDisposable()
    var locationFromMain : com.enveramil.savelocation.model.Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Call the "registerLauncher()" function
        registerLauncher()

        // Initialize sharedPreferences
        sharedPreferences = this.getSharedPreferences("com.enveramil.savelocation", MODE_PRIVATE)

        // Define the variable created for tracking
        tracker = false

        // Initialize latitude and longitude
        selectedLatitude = 0.0
        selectedLongitude = 0.0

        // Used to create a back button in "AppBar"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = Room.databaseBuilder(applicationContext,LocationDatabase::class.java,"Locations").build()
        dao = db.locationDao()

        binding.button.isEnabled = false

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Used to indicate that we will use a "LongClickListener".
        mMap.setOnMapLongClickListener(this)


        val intent = intent
        val info = intent.getStringExtra("info")
        if (info == "new"){
            binding.button.visibility = View.VISIBLE
            binding.button2.visibility = View.GONE

            locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager

            locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    // Is it already registered in "sharedPreferences"
                    // If it is not registered it will be taken as "false"
                    tracker = sharedPreferences.getBoolean("location",false)

                    if (!tracker!!){
                        val userLocation = LatLng(latitude,longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15f))
                        // A "true" value with the same key name is sent to "sharedPreferences".
                        sharedPreferences.edit().putBoolean("location",true).apply()
                    }
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
                    mMap.addMarker(MarkerOptions().position(lastLocation).title("User Location"))

                }

            }

        }else{
            mMap.clear()
            locationFromMain = intent.getSerializableExtra("selectedPlace") as com.enveramil.savelocation.model.Location
            locationFromMain?.let {
                val latlng = LatLng(it.latitude,it.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,15f))
                mMap.addMarker(MarkerOptions().position(latlng).title(it.locationName))
                binding.textView.setText(it.locationName)
                binding.button.visibility = View.GONE
                binding.button2.visibility = View.VISIBLE
            }

        }




    }

    private fun registerLauncher(){
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
        binding.button.isEnabled = true
    }

    private fun getBitmapDescriptorFromVector(context: Context, @DrawableRes vectorDrawableResourceId: Int): BitmapDescriptor? {
        var height : Int = 250
        var width : Int = 250
        val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable!!.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable!!.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun saveButton(view: View){

        if (selectedLatitude != null && selectedLongitude != null){
            val location = com.enveramil.savelocation.model.Location(binding.textView.text.toString(),selectedLatitude!!,selectedLongitude!!)
            compositeDisposable.add(
                dao.insertData(location)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse)
            )
            binding.textView.text.clear()
        }

    }
    fun handleResponse(){
        val intent = Intent(this,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    fun deleteButton(view: View){
        locationFromMain?.let {
            compositeDisposable.add(
                dao.deleteData(it).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse)
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}