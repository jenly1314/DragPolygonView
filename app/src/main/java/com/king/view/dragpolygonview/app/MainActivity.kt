package com.king.view.dragpolygonview.app

import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.king.view.dragpolygonview.DragPolygonView.Polygon
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //三角形
        dpv.addPolygon(PointF(514f, 80f), PointF(814f, 340f), PointF(214f, 340f))
        //四边形
        dpv.addPolygon(Polygon(314f, 377f, 714f, 775f))
        //五边形
        dpv.addPolygon(PointF(731f, 939f), PointF(652f, 1154f), PointF(403f, 1152f), PointF(322f, 931f), PointF(522f, 805f))

    }
}