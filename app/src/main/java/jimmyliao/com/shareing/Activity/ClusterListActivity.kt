package jimmyliao.com.shareing.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import jimmyliao.com.shareing.Adapter.ListItemAdapter
import jimmyliao.com.shareing.Constant.soldingList
import jimmyliao.com.shareing.Model.Solding
import jimmyliao.com.shareing.R
import kotlinx.android.synthetic.main.activity_cluster_list.*

class ClusterListActivity : AppCompatActivity() {
    private var clusterList = listOf<Solding>()

    companion object {
        const val LIST = "cluster list"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cluster_list)

        initData()
        initRecycler()
    }

    private fun initData() {
        val idList = intent.extras.getStringArrayList(LIST)

        clusterList = soldingList.filter {
            idList.indexOf(it.ref?.id) > -1
        }

        Log.d("ClusterList", "$clusterList")
    }

    private fun initRecycler() {
        recycler_cluster_list.layoutManager = GridLayoutManager(this, 2)
        val adapter = ListItemAdapter(this, clusterList)

        recycler_cluster_list.adapter = adapter
    }
}
