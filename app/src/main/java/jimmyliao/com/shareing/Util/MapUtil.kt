package jimmyliao.com.shareing.Util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.view.View
import com.google.android.gms.common.api.ResolvableApiException
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
import jimmyliao.com.shareing.Activity.MainActivity
import jimmyliao.com.shareing.Activity.SoldingDetailActiviy
import jimmyliao.com.shareing.Model.Solding
import jimmyliao.com.shareing.R
import kotlinx.android.synthetic.main.marker_solding.view.*
import java.lang.Exception

class MapUtil {
    private lateinit var context: Context
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        var lastLocation = LatLng(1.299964, 103.843337)
    }

    fun setupMap(
        context: Context,
        map: GoogleMap,
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
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 14.0f))

            val locationRequest = LocationRequest.create()?.apply {
                interval = 1 * 1000
                fastestInterval = 5 * 100
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!)
            val client = LocationServices.getSettingsClient(context)
            val task = client.checkLocationSettings(builder.build())

            task.addOnFailureListener {
                if (it is ResolvableApiException) {
                    try {
                        it.startResolutionForResult(context as Activity, MainActivity.REQUEST_LOCATION_CHECK_SETTINGS)
                    } catch (sendEx: IntentSender.SendIntentException) {
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    if (locationResult != null) {
                        onLocationUpdate(locationResult.lastLocation)
                        lastLocation =
                                LatLng(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude)
                    }
                }
            }, Looper.myLooper())

        }


        map.isMyLocationEnabled = true
        setupReady(true)
    }

    fun setupClusterManager(map: GoogleMap, list: MutableList<Solding>) {
        val clusterManager = ClusterManager<Solding>(context, map)
        clusterManager.renderer = MyClusterRender(map, clusterManager)
        clusterManager.addItems(list)
        clusterManager.cluster()
        map.setOnCameraIdleListener(clusterManager)
        map.setOnMarkerClickListener(clusterManager)

        clusterManager.setOnClusterClickListener {
            val builder = LatLngBounds.builder()
            for (item in it.items) {
                builder.include(item.position)
            }
            val bounds = builder.build()

            try {
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10))
            } catch (e: Exception) {
                e.printStackTrace()
            }

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

    private inner class MyClusterRender(map: GoogleMap, clusterManager: ClusterManager<Solding>) :
        DefaultClusterRenderer<Solding>(context, map, clusterManager) {

        val iconFactory = IconGenerator(context)
        val contentView: View = (context as Activity).layoutInflater.inflate(R.layout.marker_solding, null)

        init {
            iconFactory.setContentView(contentView)
        }

        override fun onBeforeClusterItemRendered(item: Solding?, markerOptions: MarkerOptions?) {
            contentView.tv_solding_title.text = item!!.soldingTitle
            contentView.tv_solding_amount.text = item.amount.toString()
            contentView.tv_solding_unit.text = item.unit
            contentView.tv_solding_price.text = item.price.toString()

            val soldingLocation = item.position
            val distance = SphericalUtil.computeDistanceBetween(lastLocation, soldingLocation)

            when {
                distance < 500 -> iconFactory.setStyle(IconGenerator.STYLE_BLUE)
                distance > 1000 -> iconFactory.setStyle(IconGenerator.STYLE_RED)
                else -> iconFactory.setStyle(IconGenerator.STYLE_ORANGE)
            }

            markerOptions!!.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon()))
        }

        override fun shouldRenderAsCluster(cluster: Cluster<Solding>?): Boolean {
            return cluster!!.size > 1
        }
    }
}