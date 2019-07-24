package jimmyliao.com.shareing.Activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.firestore.GeoPoint
import jimmyliao.com.shareing.Constant.*
import jimmyliao.com.shareing.R
import jimmyliao.com.shareing.Util.BaseUtil
import jimmyliao.com.shareing.Util.FirebaseUtil
import jimmyliao.com.shareing.Util.MapUtil
import kotlinx.android.synthetic.main.activity_add_selling.*

class AddSellingActivity : AppCompatActivity() {

    companion object {
        val TAG = "AddSellingActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_selling)

        setToolBar()
        setData()
        initEvent()
    }

    private fun setToolBar() {
        setSupportActionBar((toolbar as Toolbar))
        supportActionBar!!.title = resources.getString(R.string.text_title_add)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun setData() {
        val ingredientListAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, IngredientList)
        ingredientListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinner_ingredient.adapter = ingredientListAdapter
        spinner_ingredient.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val unitList = when (parent?.getItemAtPosition(position).toString()) {
                    "Beef", "Pork" -> gramList
                    else -> mutableListOf()
                }
                val unitListAdapter =
                    ArrayAdapter(this@AddSellingActivity, android.R.layout.simple_spinner_item, unitList)
                unitListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                spinner_unit.adapter = unitListAdapter
            }

        }
    }

    private fun initEvent() {
        btn_add_to_db.setOnClickListener {
            val loading = BaseUtil.loadingDialog(this)
            loading.show()
            val lastlocation = MapUtil.lastLocation

            val data = hashMapOf(
                "amount" to et_measure.text.toString().toDouble(),
                "location" to GeoPoint(lastlocation.latitude, lastlocation.longitude),
                "price" to et_price.text.toString().toDouble(),
                "soldingTitle" to spinner_ingredient.selectedItem.toString(),
                "unit" to (spinner_unit.selectedItem?.toString() ?: "")
            )

            FirebaseUtil().addData("Solding", null, data) { success, e ->
                loading.dismiss()
                if (success) {
                    Toast.makeText(this, "create success", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Log.e(TAG, e.toString())
                    e!!.printStackTrace()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
