package jimmyliao.com.shareing.Util

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.View
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.SphericalUtil
import com.google.maps.android.ui.IconGenerator
import jimmyliao.com.shareing.Activity.MainActivity
import jimmyliao.com.shareing.Model.Solding
import jimmyliao.com.shareing.R
import kotlinx.android.synthetic.main.marker_solding.view.*

class MapUtil {
    private lateinit var context: Context
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: LatLng

    fun setupMap(context: Context, map: GoogleMap, setupReady: (Boolean) -> Unit) {
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
            askLocationService()
            fusedLocationClient.lastLocation.addOnSuccessListener {
                lastLocation =
                        if (it != null) {
                            LatLng(it.latitude, it.longitude)
                        } else {
                            LatLng(1.299964, 103.843337)
                        }
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 16.0f))
            }
        }


        map.isMyLocationEnabled = true
        setupReady(true)
    }

    private fun askLocationService() {

        val locationRequest = LocationRequest.create()?.apply {
            interval = 10 * 1000
            fastestInterval = 5 * 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!)
        val client = LocationServices.getSettingsClient(context)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
        }.addOnFailureListener {
            if (it is ResolvableApiException) {
                try {
                    it.startResolutionForResult(context as Activity, MainActivity.REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {

                }
            }
        }
    }

    fun addMarkerOnMap(map: GoogleMap, solding: Solding) {
        val iconFactory = IconGenerator(context)
        val contentView: View = (context as Activity).layoutInflater.inflate(R.layout.marker_solding, null)

        contentView.tv_solding_title.text = solding.title
        contentView.tv_solding_amount.text = solding.amount.toString()
        contentView.tv_solding_unit.text = solding.unit
        contentView.tv_solding_price.text = solding.price.toString()
        iconFactory.setContentView(contentView)
        val soldingLocation = LatLng(solding.location!!.latitude, solding.location.longitude)
        val distance = SphericalUtil.computeDistanceBetween(lastLocation, soldingLocation)

        when {
            distance < 500 -> iconFactory.setStyle(IconGenerator.STYLE_BLUE)
            distance > 1000 -> iconFactory.setStyle(IconGenerator.STYLE_RED)
            else -> iconFactory.setStyle(IconGenerator.STYLE_ORANGE)
        }

        val markerOptions = MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon()))
            .position(soldingLocation)
            .anchor(iconFactory.anchorU, iconFactory.anchorV)
        map.addMarker(markerOptions)
    }
}