package jimmyliao.com.shareing.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import jimmyliao.com.shareing.Adapter.ListItemAdapter
import jimmyliao.com.shareing.Constant.soldingList
import jimmyliao.com.shareing.R
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

        val latest = soldingList
        latestAdapter = ListItemAdapter(this,latest)
        recycler_latest_coming.adapter = latestAdapter

        nearbyAdapter = ListItemAdapter(this,latest)
        recycler_nearby.adapter = nearbyAdapter
    }

    private fun initEvent() {

    }
}
