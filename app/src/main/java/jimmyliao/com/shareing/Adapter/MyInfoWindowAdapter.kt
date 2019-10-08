package jimmyliao.com.shareing.Adapter

import android.app.Activity
import android.content.Context
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import jimmyliao.com.shareing.Model.Selling
import jimmyliao.com.shareing.R
import kotlinx.android.synthetic.main.marker_solding.view.*

class MyInfoWindowAdapter(context:Context) : GoogleMap.InfoWindowAdapter {
    private var contentView: View = (context as Activity).layoutInflater.inflate(R.layout.marker_solding,null)

    override fun getInfoWindow(marker: Marker?): View {
        contentView.tv_solding_title.text = marker?.title

        val solding = marker?.tag as Selling
        contentView.tv_solding_amount.text = solding.amount.toString()
        contentView.tv_solding_unit.text = solding.unit
        contentView.tv_solding_price.text = solding.price.toString()

        return contentView
    }

    override fun getInfoContents(marker: Marker?): View {
        return contentView
    }

}