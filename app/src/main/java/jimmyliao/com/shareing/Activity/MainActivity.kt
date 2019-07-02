package jimmyliao.com.shareing.Activity

import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import jimmyliao.com.shareing.Model.Solding
import jimmyliao.com.shareing.R

class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var db:FirebaseFirestore

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        initData()
    }

    private fun initData(){
        db = FirebaseFirestore.getInstance()
        db.collection("Solding").get().addOnSuccessListener {result->
            for(document in result){
                val solding = document.toObject(Solding::class.java)
                addMarkerOnMap(solding)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap!!

//        val home = LatLng(1.274, 103.839)
//        map.addMarker(MarkerOptions().position(home).title("My place"))
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(home, 12.0f))
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)

        setupMap()
    }

    private fun setupMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        map.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) {
            val moveTo = if (it != null) {
                LatLng(it.latitude, it.longitude)
            } else {
                LatLng(1.351, 103.872)
            }
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(moveTo, 15.0f))
        }
    }

    private fun addMarkerOnMap(solding: Solding) {
        val location = solding.location
        val markerOptions = MarkerOptions().position(LatLng(location!!.latitude,location!!.longitude)).title(solding.title)
        map.addMarker(markerOptions)
    }

    override fun onMarkerClick(marker: Marker?) = false
}
