package com.enveramil.savelocation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.*
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import androidx.viewpager2.widget.ViewPager2
import com.enveramil.savelocation.databinding.ActivityMainBinding
import com.enveramil.savelocation.databinding.ActivityMapsBinding
import com.enveramil.savelocation.model.Location
import com.enveramil.savelocation.roomdb.LocationDatabase
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val compositeDisposable = CompositeDisposable()
    private lateinit var list : ArrayList<CompositeDisposable>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val db = Room.databaseBuilder(applicationContext, LocationDatabase::class.java,"Locations").build()
        val dao = db.locationDao()
        compositeDisposable.add(
            dao.getAllData().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::handleResponse)
        )

        //binding.navBottomBar.showBadge(R.id.home,100)
        //binding.navBottomBar.showBadge(R.id.location,10)
        binding.navBottomBar.setOnItemSelectedListener {
            binding.navBottomBar.setItemSelected(R.id.home,true)
            if (R.id.location == it) {
                val intent = Intent(this, MapsActivity::class.java)
                intent.putExtra("info","new")
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }



    }

    fun handleResponse(list : List<Location>){
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = SaveLocationAdapter(list)
        binding.recyclerView.adapter = adapter

    }

    /**
     * Used to do "menu" binding created under "res"
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.location_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Used to do "menu" binding created under "res"
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_location){
            val intent = Intent(this,MapsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("info","new")

            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    fun goToAddNewPlace(item: MenuItem){
        if (binding.navBottomBar.id == R.id.location){
            val intent = Intent(this,MapsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    fun View.hide() {
        //this.visibility = GONE
        binding.recyclerView.visibility = INVISIBLE
    }

    fun View.show() {
        //this.visibility = VISIBLE
        binding.recyclerView.visibility = VISIBLE
    }

}