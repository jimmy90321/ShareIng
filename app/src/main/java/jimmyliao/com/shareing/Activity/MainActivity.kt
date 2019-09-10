package jimmyliao.com.shareing.Activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import jimmyliao.com.shareing.Constant.*
import jimmyliao.com.shareing.Model.Solding
import jimmyliao.com.shareing.R
import jimmyliao.com.shareing.Util.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val mapUtil = MapUtil()
    private var dataReady = false
    private var mapReady = false
    private lateinit var auth: FirebaseAuth

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
            }
            drawer.closeDrawer(Gravity.START, true)

            return@setNavigationItemSelectedListener true
        }
    }

    private fun initData() {
        auth = FirebaseAuth.getInstance()

        currentUser = auth.currentUser
        updateUI(currentUser)

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

    private fun updateData(){
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
            startActivityForResult(intent,REQUEST_ADD_SELLING)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_FILTER -> {
                    val ingredients = data?.getStringArrayListExtra(FilterActivity.KEY_INGREDIENTS)
                    filteredList = soldingList.filter {
                        ingredients!!.contains(it.soldingTitle?.toLowerCase())
                    }
                    mapUtil.updateCluster(map, filteredList)
                }
                REQUEST_ADD_SELLING->{
                    updateData()
                }
                GOOGLE_SIGN_IN -> {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        val account = task.getResult(ApiException::class.java)
                        firebaseAuthWithGoogle(this, auth, account!!) {
                            updateUI(currentUser)
                        }
                    } catch (e: ApiException) {
                        // Google Sign In failed, update UI appropriately
                        Log.w(TAG, "Google sign in failed", e)
                    }
                }
            }
        }
    }

//    private fun firebaseAuthWithGoogle(acc: GoogleSignInAccount) {
//        val dialog = customDialog(this)
//        dialog.show()
//        val credential = GoogleAuthProvider.getCredential(acc.idToken, null)
//
//        auth.signInWithCredential(credential)
//            .addOnSuccessListener {
//                dialog.dismiss()
//                Toast.makeText(this@MainActivity, "Login success", Toast.LENGTH_SHORT).show()
//
//                currentUser = auth.currentUser!!
//                updateUI(currentUser)
//            }
//            .addOnFailureListener {
//                dialog.dismiss()
//                Toast.makeText(this@MainActivity, "Login failed", Toast.LENGTH_SHORT).show()
//
//                Log.e(TAG, "firebase auth with google failed", it)
//            }
//    }

    private fun logout() {
        auth.signOut()
        currentUser = null
        Toast.makeText(this@MainActivity, "Logout success", Toast.LENGTH_SHORT).show()
        updateUI(null)
    }

    private fun updateUI(user: FirebaseUser?) {
        sidebar.menu.findItem(R.id.menu_login).isVisible = user == null
        sidebar.menu.findItem(R.id.menu_logout).isVisible = user != null
        sidebar.getHeaderView(0).findViewById<TextView>(R.id.header_drawer).text = user?.email
    }
}
