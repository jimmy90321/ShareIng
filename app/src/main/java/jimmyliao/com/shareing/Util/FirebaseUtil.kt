package jimmyliao.com.shareing.Util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import jimmyliao.com.shareing.R
import java.lang.Exception

class FirebaseUtil {
    private var db: FirebaseFirestore? = null

    init {
        if (db == null) {
            db = FirebaseFirestore.getInstance()
        }
    }

    fun getCollectionData(collection: String, result: (QuerySnapshot) -> Unit) {
        db!!.collection(collection).get().addOnSuccessListener {
            result(it)
        }
    }

    fun addData(
        collection: String,
        documentId: String?,
        data: Any,
        onResult: (Boolean, Exception?) -> Unit
    ) {

        val documentRef = if (documentId != null) {
            db!!.collection(collection).document(documentId)
        } else {
            db!!.collection(collection).document()
        }

        documentRef.set(data)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener {
                onResult(false, it)
            }
    }
}