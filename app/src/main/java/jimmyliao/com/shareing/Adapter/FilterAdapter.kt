package jimmyliao.com.shareing.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import jimmyliao.com.shareing.R
import kotlinx.android.synthetic.main.item_multi_choice.view.*
import java.util.*

class FilterAdapter(val context: Context, val data: MutableList<String>) :
    RecyclerView.Adapter<FilterAdapter.FilterViewHolder>(), Filterable {

    private var sortedData: List<String> = data.sorted()
    private lateinit var filterData: MutableList<String>
    private lateinit var onItemClickListener: ((view: View, position: Int, id: Int) -> Unit)
    private lateinit var myFilter: MyFilter


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        return FilterViewHolder(LayoutInflater.from(context).inflate(R.layout.item_multi_choice, parent, false))
    }

    override fun getItemCount() = filterData.count()

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            onItemClickListener(it, position, holder.id)
        }
        holder.itemView.checkView.text = filterData[position]
    }

    inner class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var id: Int = 0
    }

    override fun getFilter(): Filter {
        return myFilter
    }

    inner class MyFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            if (p0 != null && p0.toString().trim { it <= ' ' }.isNotEmpty()) {
                val tempData = ArrayList<String>()
                for (i in sortedData.indices) {
                    val content = sortedData[i]
                    if (content.toLowerCase().contains(p0)) {
                        tempData.add(content)
                    }
                }
                filterData = tempData
            } else {
                filterData.addAll(sortedData)
            }
            val filterResults = Filter.FilterResults()
            filterResults.count = filterData.size
            filterResults.values = filterData
            return filterResults
        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            filterData = p1?.values as MutableList<String>
            notifyDataSetChanged()
        }

    }

}