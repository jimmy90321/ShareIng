package jimmyliao.com.shareing.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jimmyliao.com.shareing.Model.Solding
import jimmyliao.com.shareing.R
import kotlinx.android.synthetic.main.item_solding_list.view.*

class ListItemAdapter(val context: Context, val data: List<Solding>) :
    RecyclerView.Adapter<ListItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_solding_list, parent, false))
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(data[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setData(item:Solding){
            itemView.tv_item_list_title.text = item.soldingTitle
            val amount = "${item.amount} ${item.unit}"
            itemView.tv_item_list_amount.text = amount
            val price = "S$ ${item.price}"
            itemView.tv_item_list_price.text = price
        }
    }

}