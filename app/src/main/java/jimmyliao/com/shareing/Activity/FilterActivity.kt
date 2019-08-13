package jimmyliao.com.shareing.Activity

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import jimmyliao.com.shareing.R
import kotlinx.android.synthetic.main.activity_filter.*

class FilterActivity : AppCompatActivity() {

    companion object {
        const val KEY_INGREDIENTS = "ingredients"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        initEvent()
    }

    private fun initEvent() {
        btn_test.setOnClickListener {
            val ingredients = arrayListOf("beef")
            val intent = Intent()
            intent.putStringArrayListExtra(KEY_INGREDIENTS, ingredients)
            setResult(Activity.RESULT_OK,intent)
            finish()
        }
    }
}
