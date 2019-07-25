package jimmyliao.com.shareing.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import jimmyliao.com.shareing.Constant.provider_collectionName
import jimmyliao.com.shareing.Constant.soldingList
import jimmyliao.com.shareing.Model.Provider
import jimmyliao.com.shareing.R
import jimmyliao.com.shareing.Util.FirebaseUtil
import kotlinx.android.synthetic.main.activity_solding_detail.*

class SoldingDetailActiviy : AppCompatActivity() {

    companion object {
        const val ID = "soldingID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solding_detail)

        setToolBar()
        initData()
    }

    private fun setToolBar() {
        setSupportActionBar((detail_toolbar as Toolbar))
        supportActionBar!!.title = resources.getString(R.string.text_title_detail)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun initData() {
        val soldingId = intent.extras.getString(ID)
        val solding = soldingList.find { it.ref!!.id == soldingId }

        FirebaseUtil().getData(provider_collectionName, solding!!.providerRef!!) {
            val provider = it as Provider
            tv_detail_provider.text = provider.name
            tv_detail_phone.text = provider.phone
        }

        tv_detail_ingredient.text = solding.soldingTitle
        val amountWithUnit = "${solding.amount} ${solding.unit}"
        tv_detail_amount.text = amountWithUnit
        tv_detail_price.text = solding.price.toString()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
