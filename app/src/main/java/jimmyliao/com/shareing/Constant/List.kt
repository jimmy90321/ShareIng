package jimmyliao.com.shareing.Constant

import com.google.firebase.firestore.DocumentReference
import jimmyliao.com.shareing.Model.*


val IngredientList = mutableListOf("Apple", "Beef", "Pork")

val gramList = mutableListOf("mg", "g", "kg")

val soldingList = mutableListOf<Solding>()

var filteredList = mutableListOf<Solding>()

val providerMap = mutableMapOf<DocumentReference, Provider>()