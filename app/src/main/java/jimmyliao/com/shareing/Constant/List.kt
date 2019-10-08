package jimmyliao.com.shareing.Constant

import com.google.firebase.firestore.DocumentReference
import jimmyliao.com.shareing.Model.*


val IngredientList = mutableListOf("Apple", "Beef", "Pork")

val gramList = mutableListOf("mg", "g", "kg")

val sellingList = mutableListOf<Selling>()

var filteredList = listOf<Selling>()

val providerMap = mutableMapOf<DocumentReference, Provider>()