package irastyazhkina.customview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import irastyazhkina.customview.ui.CustomView
import irastyazhkina.customview.ui.NotFilledCustomView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<CustomView>(R.id.customView).data = listOf(
            500F,
            500F,
            500F,
            500F,
        )

        findViewById<NotFilledCustomView>(R.id.notFilledCustomView).data = listOf(
            0.25F,
            0.25F,
            0.25F,
        )
    }
}