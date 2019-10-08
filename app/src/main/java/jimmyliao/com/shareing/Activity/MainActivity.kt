package jimmyliao.com.shareing.Activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import jimmyliao.com.shareing.Constant.*
import jimmyliao.com.shareing.Model.Selling
import jimmyliao.com.shareing.R
import jimmyliao.com.shareing.Util.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var mapUtil: MapUtil
    private var dataReady = false
    private var mapReady = false
    private lateinit var auth: FirebaseAuth
    private lateinit var locationDialog: Dialog

    companion object {
        const val TAG = "MainActivity"
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
        const val REQUEST_LOCATION_CHECK_SETTINGS = 1001
        const val REQUEST_FILTER = 2001
        const val REQUEST_ADD_SELLING = 2002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapUtil = MapUtil()

        locationDialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        locationDialog.setContentView(R.layout.dialog_location_service_cover)
        locationDialog.show()

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
            drawer.closeDrawer(Gravity.START, true)
        }

        sidebar.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_filter -> {
                    val intent = Intent(this, FilterActivity::class.java)
                    startActivityForResult(intent, REQUEST_FILTER)
                }
                R.id.menu_favor -> {
                    Toast.makeText(this, "favor click", Toast.LENGTH_SHORT).show()
                }
                R.id.menu_login -> {
                    login(this)
                }
                R.id.menu_logout -> {
                    logout()
                }
                R.id.menu_to_intro -> {
                    startActivity(Intent(this, IntroActivity::class.java))
                }
            }
            drawer.closeDrawer(Gravity.START, true)

            return@setNavigationItemSelectedListener true
        }
    }

    private fun initData() {
        auth = FirebaseAuth.getInstance()

        currentUser = auth.currentUser
        updateDrawer(currentUser)

        FirebaseUtil().getCollectionData(selling_collectionName) { result ->
            result.forEach { document ->
                val selling = Selling(
                    document.reference,
                    document.get(selling_amount),
                    document.getGeoPoint(selling_location),
                    document.get(selling_price),
                    document.getString(selling_title),
                    document.getString(selling_unit),
                    document.getDocumentReference(selling_provider),
                    document.getTimestamp(selling_postTime)
                )
                sellingList.add(selling)
            }
            filteredList = sellingList
            dataReady = true
            if (mapReady) {
                mapUtil.setupClusterManager(map, filteredList)
            }
        }
    }

    private fun updateData() {
        FirebaseUtil().getCollectionData(selling_collectionName) { result ->
            result.forEach { document ->
                val selling = Selling(
                    document.reference,
                    document.get(selling_amount),
                    document.getGeoPoint(selling_location),
                    document.get(selling_price),
                    document.getString(selling_title),
                    document.getString(selling_unit),
                    document.getDocumentReference(selling_provider)
                )
                if (sellingList.indexOf(selling) == -1) sellingList.add(selling)
            }
            filteredList = sellingList
            mapUtil.updateCluster(map, filteredList)
        }
    }

    private fun initEvent() {
        btn_add_solding.setOnClickListener {
            if (MapUtil.lastLocation == null || currentUser == null) {
                Toast.makeText(
                    this@MainActivity,
                    "Please turn on location service & login to share your ingredient",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val intent = Intent(this, AddSellingActivity::class.java)
            startActivityForResult(intent, REQUEST_ADD_SELLING)
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        var firstUpdated = false
        map = googleMap!!

        map.uiSettings.isZoomControlsEnabled = false
        map.uiSettings.isMapToolbarEnabled = false

        mapUtil.setupMap(this@MainActivity, map,
            locationAvailable = { available ->
                if (available) {
                    locationDialog.dismiss()
                } else {
                    locationDialog.show()
                }
            },
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_FILTER -> {
                    val ingredients = data?.getStringArrayListExtra(FilterActivity.KEY_INGREDIENTS)
                    filteredList = sellingList.filter {
                        ingredients!!.contains(it.soldingTitle?.toLowerCase())
                    }
                    mapUtil.updateCluster(map, filteredList)
                }
                REQUEST_ADD_SELLING -> {
                    updateData()
                }
                GOOGLE_SIGN_IN -> {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        val account = task.getResult(ApiException::class.java)
                        firebaseAuthWithGoogle(this, auth, account!!) {
                            updateDrawer(currentUser)
                        }
                    } catch (e: ApiException) {
                        // Google Sign In failed, update UI appropriately
                        Log.w(TAG, "Google sign in failed", e)
                    }
                }
            }
        }
    }

    private fun logout() {
        auth.signOut()
        currentUser = null
        Toast.makeText(this@MainActivity, "Logout success", Toast.LENGTH_SHORT).show()
        updateDrawer(null)
    }

    private fun updateDrawer(user: FirebaseUser?) {
        sidebar.menu.findItem(R.id.menu_login).isVisible = user == null
        sidebar.menu.findItem(R.id.menu_logout).isVisible = user != null
        sidebar.getHeaderView(0).findViewById<TextView>(R.id.header_drawer).text = user?.email
    }
}
