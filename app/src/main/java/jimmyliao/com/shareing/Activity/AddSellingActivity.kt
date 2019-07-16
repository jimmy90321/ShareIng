package jimmyliao.com.shareing.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.ArrayAdapter
import jimmyliao.com.shareing.Constant.List
import jimmyliao.com.shareing.R
import kotlinx.android.synthetic.main.activity_add_selling.*

class AddSellingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_selling)

        setToolBar()
        setData()
    }

    private fun setToolBar() {
        setSupportActionBar((toolbar as Toolbar))
        supportActionBar!!.title = resources.getString(R.string.text_title_add)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun setData() {
        val listAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, List().IngredientList)
        listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinner_ingredient.adapter = listAdapter
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
