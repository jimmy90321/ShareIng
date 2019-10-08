package jimmyliao.com.shareing.Activity

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import jimmyliao.com.shareing.Adapter.FilterAdapter
import jimmyliao.com.shareing.Constant.sellingList
import jimmyliao.com.shareing.R
import jimmyliao.com.shareing.Util.Dp2Px
import kotlinx.android.synthetic.main.activity_filter.*

class FilterActivity : AppCompatActivity() {
    private val TAG = "FilterActivity"
    private lateinit var adapter: FilterAdapter

    companion object {
        const val KEY_INGREDIENTS = "ingredients"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        setToolBar()
        setupRecyclerView()
        initEvent()
    }

    private fun setToolBar() {
        setSupportActionBar((filter_toolbar as Toolbar))

        supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

    private fun setupRecyclerView() {
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        val titles = mutableListOf<String>()
        sellingList.forEach {
            if (titles.indexOf(it.soldingTitle!!.toLowerCase()) == -1) {
                titles.add(it.soldingTitle.toLowerCase())
            }
        }

        adapter = FilterAdapter(this, titles)
        adapter.setOnItemClickListener { _, position, id ->
            adapter.clickIng(position, id)
        }
        recycler.adapter = adapter
    }

    private fun initEvent(){
        btn_filter_clear_all.setOnClickListener {
            adapter.clearAll()
        }

        btn_filter_select_all.setOnClickListener {
            adapter.selectAll()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = MenuItemCompat.getActionView(searchItem) as SearchView
        searchView.maxWidth = Dp2Px(280f, applicationContext)
        searchView.setIconifiedByDefault(false)
        searchView.queryHint = "Search..."
        searchView.inputType = InputType.TYPE_CLASS_TEXT
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean = false

            override fun onQueryTextChange(p0: String?): Boolean {
                adapter.filter.filter(p0)
                showButton(p0.isNullOrEmpty())
                return false
            }
        })
        return true
    }

    fun showButton(b: Boolean) {
        btn_filter_clear_all.visibility = if (b) View.VISIBLE else View.GONE
        btn_filter_select_all.visibility = if (b) View.VISIBLE else View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_filter -> {
                val finalFilter = adapter.tempData as ArrayList<String>
                val intent = Intent()
                intent.putStringArrayListExtra(KEY_INGREDIENTS, finalFilter)
                setResult(Activity.RESULT_OK, intent)
                finish()
                true
            }
            android.R.id.home -> {
                super.onBackPressed()
                true
            }
            else -> false
        }
    }
}
