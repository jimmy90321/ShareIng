package jimmyliao.com.shareing.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import com.google.maps.android.SphericalUtil
import jimmyliao.com.shareing.Adapter.ListItemAdapter
import jimmyliao.com.shareing.Constant.sellingList
import jimmyliao.com.shareing.Model.Selling
import jimmyliao.com.shareing.R
import jimmyliao.com.shareing.Util.MapUtil.Companion.lastLocation
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity() {
    private lateinit var latestAdapter: ListItemAdapter
    private lateinit var nearbyAdapter: ListItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        setupRecyclerView()
        initEvent()
    }

    private fun setupRecyclerView() {
        recycler_latest_coming.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        recycler_nearby.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)

        val latest = sellingList.sortedByDescending {
            it.postTime?.seconds
        }
        latestAdapter = ListItemAdapter(this, latest.subList(0, endIndex(latest)),R.layout.item_solding_list)
        recycler_latest_coming.adapter = latestAdapter

        val nearby = sellingList.sortedBy {
            SphericalUtil.computeDistanceBetween(lastLocation, it.position)
        }
        nearbyAdapter = ListItemAdapter(this, nearby.subList(0,endIndex(nearby)),R.layout.item_solding_list)
        recycler_nearby.adapter = nearbyAdapter
    }

    private fun endIndex(list: List<Selling>):Int {
        return if (list.size > 5) 5 else list.size
    }

    private fun initEvent() {

    }
}
