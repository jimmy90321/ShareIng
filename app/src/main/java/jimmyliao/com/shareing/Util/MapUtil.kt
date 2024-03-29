package jimmyliao.com.shareing.Util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.view.View
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.SphericalUtil
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import jimmyliao.com.shareing.Activity.ClusterListActivity
import jimmyliao.com.shareing.Activity.MainActivity
import jimmyliao.com.shareing.Activity.SoldingDetailActiviy
import jimmyliao.com.shareing.Model.Selling
import jimmyliao.com.shareing.R
import kotlinx.android.synthetic.main.marker_solding.view.*
import java.lang.Exception

class MapUtil {
    private lateinit var context: Context
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var clusterManager: ClusterManager<Selling>
    private val tempLocation = LatLng(1.299964, 103.843337)

    companion object {
        var lastLocation: LatLng? = null
    }

    fun setupMap(
        context: Context,
        map: GoogleMap,
        locationAvailable: (Boolean) -> Unit,
        setupReady: (Boolean) -> Unit,
        onLocationUpdate: (Location) -> Unit
    ) {
        this.context = context
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                MainActivity.LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        } else {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(tempLocation, 14.0f))

            val locationRequest = LocationRequest.create()?.apply {
                interval = 1 * 1000
                fastestInterval = 5 * 100
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    if (locationResult != null) {
                        onLocationUpdate(locationResult.lastLocation)
                        lastLocation =
                                LatLng(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude)
                    }
                }

                override fun onLocationAvailability(p0: LocationAvailability?) {
                    super.onLocationAvailability(p0)
                    if (p0?.isLocationAvailable != true) {
                        locationAvailable.invoke(false)
                        lastLocation = null
                    } else {
                        locationAvailable.invoke(true)
                    }
                }
            }, Looper.myLooper())

        }


        map.isMyLocationEnabled = true
        setupReady(true)
    }

    fun updateCluster(map: GoogleMap, list: List<Selling>) {
        map.clear()
        clusterManager.clearItems()
        setupClusterManager(map, list)
    }

    fun setupClusterManager(map: GoogleMap, list: List<Selling>) {
        clusterManager = ClusterManager(context, map)
        clusterManager.renderer = MyClusterRender(map, clusterManager)
        clusterManager.addItems(list)
        clusterManager.cluster()
        map.setOnCameraIdleListener(clusterManager)
        map.setOnMarkerClickListener(clusterManager)

        clusterManager.setOnClusterClickListener {
            if (map.cameraPosition.zoom != map.maxZoomLevel) {
                val builder = LatLngBounds.builder()
                for (item in it.items) {
                    builder.include(item.position)
                }
                val bounds = builder.build()

                try {
                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return@setOnClusterClickListener true
            }

            val bundle = Bundle()
            val idList = ArrayList<String>()
            for (item in it.items) {
                idList.add(item.ref!!.id)
            }
            bundle.putStringArrayList(ClusterListActivity.LIST, idList)
            val intent = Intent(context, ClusterListActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)
            return@setOnClusterClickListener true
        }

        clusterManager.setOnClusterItemClickListener {
            val id = it.ref?.id
            val bundle = Bundle()
            bundle.putString(SoldingDetailActiviy.ID, id)
            val intent = Intent(context, SoldingDetailActiviy::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)
            return@setOnClusterItemClickListener true
        }
    }

    private inner class MyClusterRender(map: GoogleMap, clusterManager: ClusterManager<Selling>) :
        DefaultClusterRenderer<Selling>(context, map, clusterManager) {

        val iconFactory = IconGenerator(context)
        val contentView: View = (context as Activity).layoutInflater.inflate(R.layout.marker_solding, null)

        init {
            iconFactory.setContentView(contentView)
        }

        override fun onBeforeClusterItemRendered(item: Selling?, markerOptions: MarkerOptions?) {
            contentView.tv_solding_title.text = item!!.soldingTitle
            contentView.tv_solding_amount.text = item.amount.toString()
            contentView.tv_solding_unit.text = item.unit
            contentView.tv_solding_price.text = item.price.toString()

            val soldingLocation = item.position
            val distance = SphericalUtil.computeDistanceBetween(lastLocation ?: tempLocation, soldingLocation)

            when {
                distance < 1000 -> iconFactory.setStyle(IconGenerator.STYLE_BLUE)
                distance > 5000 -> iconFactory.setStyle(IconGenerator.STYLE_RED)
                else -> iconFactory.setStyle(IconGenerator.STYLE_ORANGE)
            }

            markerOptions!!.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon()))
        }

        override fun shouldRenderAsCluster(cluster: Cluster<Selling>?): Boolean {
            return cluster!!.size > 1
        }
    }
}