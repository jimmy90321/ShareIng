package jimmyliao.com.shareing

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner

class convert {
    internal var spinner: Spinner? = null
    fun create() {
        spinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }

        }
    }
}
