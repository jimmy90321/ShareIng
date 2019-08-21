package jimmyliao.com.shareing.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Filter
import android.widget.Filterable
import jimmyliao.com.shareing.R
import kotlinx.android.synthetic.main.item_multi_choice.view.*
import java.util.*

class FilterAdapter(val context: Context, val data: List<String>) :
    RecyclerView.Adapter<FilterAdapter.FilterViewHolder>(), Filterable {

    private val TAG = "FilterAdapter"

    private var sortedData: List<String> = data.sorted()
    private var filterData = mutableListOf<String>()
    private lateinit var onItemClickListener: ((view: View, position: Int, id: Int) -> Unit)
    private var myFilter: MyFilter
    private var checkStates: SparseBooleanArray
    val tempData = mutableListOf<String>()

    init {
        filterData.addAll(sortedData)
        checkStates = SparseBooleanArray(0)
        myFilter = MyFilter()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        return FilterViewHolder(LayoutInflater.from(context).inflate(R.layout.item_multi_choice, parent, false))
    }

    override fun getItemCount() = filterData.count()

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.itemView.setOnClickListener { view ->
            onItemClickListener.invoke(view, position, holder.id)
        }
        holder.itemView.checkView.text = filterData[position]
        val item = holder.itemView.checkView.text.toString()
        holder.id = getItemId(item)
        holder.itemView.checkView.isChecked = checkStates.get(holder.id)
    }

    private fun getItemId(str: String): Int {
        return sortedData.indexOf(str)
    }

    inner class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var id: Int = 0
    }

    override fun getFilter(): Filter {
        return myFilter
    }

    fun setOnItemClickListener(listener: ((view: View, position: Int, id: Int) -> Unit)) {
        onItemClickListener = listener
    }

    fun clickIng(position: Int,holderId:Int) {
        if (checkStates.get(holderId)) {
            checkStates.delete(holderId)
            tempData.remove(filterData[position])
        } else {
            checkStates.put(holderId, true)
            tempData.add(filterData[position])
        }
        notifyDataSetChanged()
    }

    fun clearAll(){
        tempData.clear()
        checkStates.clear()
        notifyDataSetChanged()
    }

    fun selectAll(){
        sortedData.forEach {
            if(!tempData.contains(it)){
                tempData.add(it)
            }
        }
        for(i in 0 until sortedData.size){
            if(!checkStates[i]){
                checkStates.put(i,true)
            }
        }
        notifyDataSetChanged()
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
                filterData.clear()
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