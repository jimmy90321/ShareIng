package jimmyliao.com.shareing.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.Marker
import com.google.firebase.firestore.FirebaseFirestore
import jimmyliao.com.shareing.Model.Solding
import jimmyliao.com.shareing.R
import jimmyliao.com.shareing.Util.MapUtil

class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var db: FirebaseFirestore
    private val mapUtil = MapUtil()
    private val soldingList = mutableListOf<Solding>()
    private var dataReady = false
    private var mapReady = false

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
        const val REQUEST_CHECK_SETTINGS = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        initData()
    }

    private fun initData() {
        db = FirebaseFirestore.getInstance()
        db.collection("Solding").get().addOnSuccessListener { result ->
            result.forEach { document ->
                val solding = document.toObject(Solding::class.java)
                soldingList.add(solding)
            }
            dataReady = true
            if (mapReady) {
                mapUtil.setupClusterManager(map,soldingList)
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap!!

        map.uiSettings.isZoomControlsEnabled = true
//        map.setInfoWindowAdapter(MyInfoWindowAdapter(this@MainActivity))
        map.setOnMarkerClickListener(this)


        mapUtil.setupMap(this@MainActivity, map) {
            mapReady = it
            if (mapReady && dataReady) {
                mapUtil.setupClusterManager(map,soldingList)
            }
        }
    }

    override fun onMarkerClick(marker: Marker?) = false
}
