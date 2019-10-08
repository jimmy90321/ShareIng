package jimmyliao.com.shareing.Model

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.clustering.ClusterItem
import java.util.*

data class Selling(
    val ref: DocumentReference? = null,
    val amount: Any? = null,
    val location: GeoPoint? = null,
    val price: Any? = null,
    val soldingTitle: String? = null,
    val unit: String? = null,
    val providerRef: DocumentReference? = null,
    val postTime: Timestamp? = null
) : ClusterItem {
    override fun getSnippet(): String? {
        return null
    }

    override fun getTitle(): String? {
        return null
    }

    override fun getPosition(): LatLng {
        return LatLng(location!!.latitude, location.longitude)
    }

}