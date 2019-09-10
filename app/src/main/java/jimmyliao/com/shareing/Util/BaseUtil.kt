package jimmyliao.com.shareing.Util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import jimmyliao.com.shareing.R

fun customDialog(context: Context,cancellable:Boolean,layoutId:Int): Dialog {
    val dialog = Dialog(context)
    dialog.setCancelable(cancellable)
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setContentView(layoutId)
    return dialog
}

fun Dp2Px(dp:Float,context: Context) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,context.resources.displayMetrics).toInt()