package jimmyliao.com.shareing.Model

import com.google.firebase.firestore.GeoPoint

data class Solding(
    val amount: Any? = null,
    val location: GeoPoint? = null,
    val price: Any? = null,
    val title: String? = null,
    val unit: String? = null
)