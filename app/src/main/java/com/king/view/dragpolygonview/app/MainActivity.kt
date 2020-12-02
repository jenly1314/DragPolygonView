package com.king.view.dragpolygonview.app

import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import com.king.view.dragpolygonview.DragPolygonView
import com.king.view.dragpolygonview.DragPolygonView.Polygon
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //三角形
        dpv.addPolygon(PointF(514f, 80f), PointF(814f, 340f), PointF(214f, 340f))
        //四边形
        val quadrilateral = Polygon(314f, 377f, 714f, 775f)
        quadrilateral.text = "四边形"
        dpv.addPolygon(quadrilateral)

        //五边形
        dpv.addPolygon(PointF(731f, 939f), PointF(652f, 1154f), PointF(403f, 1152f), PointF(322f, 931f), PointF(522f, 805f))

        //点击监听
        dpv.setOnPolygonClickListener {
            showToast("Click:$it")
        }

        //长按监听
        dpv.setOnPolygonLongClickListener {
            showToast("LongClick:$it")
        }

        //改变监听
        dpv.setOnChangeListener(object : DragPolygonView.OnChangeListener{
            override fun onStartTrackingTouch(position: Int) {

            }

            override fun onChanged(position: Int, fromUser: Boolean) {

            }

            override fun onStopTrackingTouch(position: Int) {

            }

        })
    }

    private fun showToast(text: CharSequence){
        toast?.let {
            it.setText(text)
        } ?: kotlin.run {
            toast = Toast.makeText(this,text,Toast.LENGTH_SHORT)
        }
        toast?.show()
    }
}