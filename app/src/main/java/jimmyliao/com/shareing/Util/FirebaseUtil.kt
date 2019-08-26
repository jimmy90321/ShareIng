package jimmyliao.com.shareing.Util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import jimmyliao.com.shareing.Constant.GOOGLE_SIGN_IN
import jimmyliao.com.shareing.Constant.providerMap
import jimmyliao.com.shareing.Constant.provider_collectionName
import jimmyliao.com.shareing.Model.Provider
import jimmyliao.com.shareing.R
import kotlinx.android.synthetic.main.dialog_login.*
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

fun login(context: Context) {
    val dialog = Dialog(context)
    dialog.setCancelable(true)
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setContentView(R.layout.dialog_login)
    dialog.show()

    dialog.btn_google_sign_in.setOnClickListener {
        Toast.makeText(context,"Sign in clicked",Toast.LENGTH_SHORT).show()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInIntent = GoogleSignIn.getClient(context,gso).signInIntent
        (context as Activity).startActivityForResult(googleSignInIntent, GOOGLE_SIGN_IN)
        dialog.dismiss()
    }

}