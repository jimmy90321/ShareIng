package jimmyliao.com.shareing.Activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import jimmyliao.com.shareing.Adapter.ListItemAdapter
import jimmyliao.com.shareing.Constant.sellingList
import jimmyliao.com.shareing.Model.Selling
import jimmyliao.com.shareing.R
import kotlinx.android.synthetic.main.activity_cluster_list.*

class ClusterListActivity : AppCompatActivity() {
    private var clusterList = listOf<Selling>()

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

        clusterList = sellingList.filter {
            idList.indexOf(it.ref?.id) > -1  //exist in clusterList
        }
    }

    private fun initRecycler() {
        recycler_cluster_list.layoutManager = GridLayoutManager(this, 2)
        val adapter = ListItemAdapter(this, clusterList,R.layout.item_cluster_list,
            onItemClickListener = {id->
                val bundle = Bundle()
                bundle.putString(SoldingDetailActiviy.ID, id)
                val intent = Intent(this@ClusterListActivity, SoldingDetailActiviy::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            })

        recycler_cluster_list.adapter = adapter
    }
}
