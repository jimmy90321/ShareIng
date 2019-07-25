package jimmyliao.com.shareing.Activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import jimmyliao.com.shareing.Constant.*
import jimmyliao.com.shareing.Model.Solding
import jimmyliao.com.shareing.R
import jimmyliao.com.shareing.Util.FirebaseUtil
import jimmyliao.com.shareing.Util.MapUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback{

    private lateinit var map: GoogleMap
    private val mapUtil = MapUtil()
    private var dataReady = false
    private var mapReady = false

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
        const val REQUEST_LOCATION_CHECK_SETTINGS = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setToolBar()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        initData()
        initEvent()
    }

    private fun setToolBar() {
        setSupportActionBar((toolbar as Toolbar))
        supportActionBar!!.title = resources.getString(R.string.app_name)

        val actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawer, (toolbar as Toolbar), R.string.text_open, R.string.text_close)
        drawer.addDrawerListener(actionBarDrawerToggle)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu)

        val drawerHeader = sidebar.getHeaderView(0)
        val btnCloseDrawer = drawerHeader.findViewById<ImageView>(R.id.btn_close_drawer)
        btnCloseDrawer.setOnClickListener {
            drawer.closeDrawer(Gravity.LEFT, true)
        }

        sidebar.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_filter -> {
                    Toast.makeText(this, "filter click", Toast.LENGTH_SHORT).show()
                }
                R.id.menu_favor -> {
                    Toast.makeText(this, "favor click", Toast.LENGTH_SHORT).show()
                }
                R.id.menu_login -> {
                    Toast.makeText(this, "login click", Toast.LENGTH_SHORT).show()
                }
            }

            return@setNavigationItemSelectedListener true
        }
    }

    private fun initData() {
        FirebaseUtil().getCollectionData(solding_collectionName) { result ->
            result.forEach { document ->
                val solding = Solding(
                    document.reference, document.get(solding_amount), document.getGeoPoint(
                        solding_location
                    ), document.get(solding_price), document.getString(solding_title), document.getString(
                        solding_unit
                    ), document.getDocumentReference(solding_provider)
                )
                soldingList.add(solding)
            }
            filteredList = soldingList
            dataReady = true
            if (mapReady) {
                mapUtil.setupClusterManager(map, filteredList)
            }
        }
    }

    private fun initEvent() {
        btn_add_solding.setOnClickListener {
            val intent = Intent(this, AddSellingActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        var firstUpdated = false
        map = googleMap!!

        map.uiSettings.isZoomControlsEnabled = false
        map.uiSettings.isMapToolbarEnabled = false

        mapUtil.setupMap(this@MainActivity, map,
            setupReady = {
                mapReady = it
                if (mapReady && dataReady) {
                    mapUtil.setupClusterManager(map, filteredList)
                }
            },
            onLocationUpdate = {
                if (!firstUpdated) {
                    map.clear()
                    mapUtil.setupClusterManager(map, filteredList)
                    map.animateCamera(CameraUpdateFactory.newLatLng(LatLng(it.latitude, it.longitude)))
                    firstUpdated = true
                }
            })
    }
}
