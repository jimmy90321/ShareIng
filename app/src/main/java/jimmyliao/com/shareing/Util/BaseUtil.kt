package jimmyliao.com.shareing.Util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import jimmyliao.com.shareing.R

class BaseUtil {
    companion object {
        fun loadingDialog(context: Context): Dialog {
            val dialog = Dialog(context)
            dialog.setCancelable(false)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.loading_dialog)
            return dialog
        }
    }
}