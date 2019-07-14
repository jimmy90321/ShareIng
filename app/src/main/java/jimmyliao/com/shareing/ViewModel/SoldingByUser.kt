package jimmyliao.com.shareing.ViewModel

import com.google.firebase.firestore.GeoPoint

data class SoldingByUser (
    val amount: Any? = null,
    val location: GeoPoint? = null,
    val price: Any? = null,
    val title: String? = null,
    val unit: String? = null,
    val distance: Float? = null
)