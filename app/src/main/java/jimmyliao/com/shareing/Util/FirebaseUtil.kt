package jimmyliao.com.shareing.Util

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import jimmyliao.com.shareing.Constant.GOOGLE_SIGN_IN
import jimmyliao.com.shareing.Constant.currentUser
import jimmyliao.com.shareing.Constant.providerMap
import jimmyliao.com.shareing.Constant.provider_collectionName
import jimmyliao.com.shareing.Model.Provider
import jimmyliao.com.shareing.R
import kotlinx.android.synthetic.main.dialog_login.*
import kotlinx.android.synthetic.main.dialog_signup.*
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

    fun getRemoteData(collection: String, documentId: String, onResult: (Any?) -> Unit) {
        db!!.collection(collection).document(documentId).get()
            .addOnSuccessListener { documentSnapshot ->
                onResult.invoke(documentSnapshot.data)
            }.addOnFailureListener { exception ->
                Log.e("FirebaseUtil", "Get data from firestore failed", exception)
                onResult.invoke(null)
            }
    }

    fun getLocalData(collection: String, documentIndex: String, result: (Any) -> Unit) {
        when (collection) {
            provider_collectionName -> getProvider(db!!.collection(collection).document(documentIndex), result)
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
    val loginOptionsDialog = customDialog(context, true, R.layout.dialog_login)
    loginOptionsDialog.show()

    loginOptionsDialog.btn_google_sign_in.setOnClickListener {
        loginOptionsDialog.dismiss()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInIntent = GoogleSignIn.getClient(context, gso).signInIntent
        (context as Activity).startActivityForResult(googleSignInIntent, GOOGLE_SIGN_IN)
    }

}

fun firebaseAuthWithGoogle(context: Context, auth: FirebaseAuth, account: GoogleSignInAccount, completed: () -> Unit) {
    val loadingDialog = customDialog(context, false, R.layout.dialog_loading)
    loadingDialog.show()
    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

    auth.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                checkSignup(context, auth.currentUser!!) {
                    loadingDialog.dismiss()
                    Toast.makeText(context, "Login success", Toast.LENGTH_SHORT).show()
                    currentUser = auth.currentUser
                    completed.invoke()
                }
            } else {
                loadingDialog.dismiss()
                Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                currentUser = null
                completed.invoke()
            }
        }
}

private fun checkSignup(context: Context, user: FirebaseUser, onCompleted: () -> Unit) {
    FirebaseUtil().getRemoteData("Provider", user.uid) {
        if (it == null) {
            val signUpDialog = customDialog(context, false, R.layout.dialog_signup)
            signUpDialog.show()

            signUpDialog.btn_sign_up.setOnClickListener {
                val name = signUpDialog.et_sign_up_name.text.toString().trim()
                val phone = signUpDialog.et_sign_up_phone.text.toString().trim()

                if (name != "" && phone != "") {
                    FirebaseUtil().addData(
                        "Provider",
                        user.uid,
                        hashMapOf(
                            "name" to name,
                            "phone" to phone,
                            "email" to user.email
                        )
                    ) { success, exception ->
                        if (success) {
                            signUpDialog.dismiss()
                            onCompleted.invoke()
                            return@addData
                        }

                        Log.e("FirebaseUtil", "create account record failed", exception)
                    }
                }
            }
        } else {
            onCompleted.invoke()
        }
    }
}