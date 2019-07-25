package jimmyliao.com.shareing.Util

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import jimmyliao.com.shareing.Constant.providerMap
import jimmyliao.com.shareing.Constant.provider_collectionName
import jimmyliao.com.shareing.Model.Provider
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

    fun addData(collection: String, documentId: String?, data: Any, onResult: (Boolean, Exception?) -> Unit) {

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

    fun getData(collection: String, documentIndex: Any, result: (Any) -> Unit) {
        when (collection) {
            provider_collectionName -> getProvider((documentIndex as DocumentReference), result)
        }
    }

    private fun getProvider(documentRef: DocumentReference, result: (Any) -> Unit) {
        if (providerMap[documentRef] != null) {
            result(providerMap[documentRef]!!)
        } else {
            documentRef.get()
                .addOnSuccessListener {
                    val provider = it.toObject(Provider::class.java)
                    providerMap[documentRef] = provider!!
                    result(provider)
                }
        }
    }
}