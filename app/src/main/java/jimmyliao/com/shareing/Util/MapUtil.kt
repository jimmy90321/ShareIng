package jimmyliao.com.shareing.Util

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import jimmyliao.com.shareing.Activity.MainActivity
import jimmyliao.com.shareing.Model.Solding
import jimmyliao.com.shareing.R.id.map

class MapUtil {
    private lateinit var context: Context
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    fun setupMap(context: Context, map: GoogleMap) {
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
                val moveTo =
                    if (it != null) {
                        LatLng(it.latitude, it.longitude)
                    } else {
                        LatLng(1.299964, 103.843337)
                    }
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(moveTo,16.0f))
            }
        }


        map.isMyLocationEnabled = true
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
            Log.d("askLocation", "task success")
        }.addOnFailureListener {
            Log.d("askLocation", "task failed")
            if (it is ResolvableApiException) {
                Log.d("askLocation", "fail resolvable")
                try {
                    it.startResolutionForResult(context as Activity, MainActivity.REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {

                }
            }
        }
    }

    fun addMarkerOnMap(map:GoogleMap,solding: Solding) {
        val location = solding.location
        val markerOptions =
            MarkerOptions().position(LatLng(location!!.latitude, location.longitude)).title(solding.title)
        map.addMarker(markerOptions)
    }
}