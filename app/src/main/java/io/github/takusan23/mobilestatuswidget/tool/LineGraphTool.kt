package io.github.takusan23.mobilestatuswidget.tool

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

/**
 * 折れ線グラフを作る
 * */
object LineGraphTool {

    /**
     * グラフを作ってBitmapにする関数
     *
     * 権限があることを確認してください
     * */
    fun createGraphImage(context: Context): Bitmap {
        // 通信量を取得
        val mobileDataUsageList = MobileDataUsageTool
            .getMobileDataUsageDayListFromCurrentMonth(context)
            .mapIndexed { index, usage -> BarEntry(index + 1f, (usage / 1024f / 1024f / 1024f)) } // 棒グラフのデータ
        // データを作る
        val dataSet = BarDataSet(mobileDataUsageList, "モバイルデータ使用量")
        // Bitmapを返す
        val barChart = BarChart(context).apply {
            // 折れ線グラフを作る
            data = BarData(dataSet)
            measure(
                View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY)
            )
            layout(0, 0, measuredWidth, measuredHeight)
            // 背景透明化
            setBackgroundColor(Color.TRANSPARENT)
            setDrawGridBackground(false)
            // いらないの消していく
            description.isEnabled = false
            legend.isEnabled = false
            xAxis.isEnabled = false
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            dataSet.setDrawValues(false)

        }
        // Bitmap作成。Chart側でもBitmap生成関数が用意されてるんだけど、透明が扱えないため
        val bitmap = Bitmap.createBitmap(barChart.width, barChart.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        barChart.draw(canvas)
        return bitmap
    }

}