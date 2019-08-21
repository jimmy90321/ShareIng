package jimmyliao.com.shareing.Util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import jimmyliao.com.shareing.R

fun loadingDialog(context: Context): Dialog {
    val dialog = Dialog(context)
    dialog.setCancelable(false)
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setContentView(R.layout.loading_dialog)
    return dialog
}

fun Dp2Px(dp:Float,context: Context) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,context.resources.displayMetrics).toInt()