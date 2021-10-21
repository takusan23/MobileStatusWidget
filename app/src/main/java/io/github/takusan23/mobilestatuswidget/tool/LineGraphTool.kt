package io.github.takusan23.mobilestatuswidget.tool

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.view.View
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import io.github.takusan23.mobilestatuswidget.R

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
        val dataSet = LineDataSet(mobileDataUsageList, "モバイルデータ使用量").also { lineDataSet ->
            lineDataSet.color = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                context.getColor(android.R.color.system_accent1_500)
            } else {
                context.getColor(R.color.dark)
            }
            lineDataSet.setDrawCircles(false)
            lineDataSet.setDrawValues(false)
            lineDataSet.lineWidth = 3f
            lineDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER // なめらかな線
        }
        // Bitmapを返す
        val barChart = LineChart(context).apply {
            // 折れ線グラフを作る
            data = LineData(dataSet)
            measure(
                View.MeasureSpec.makeMeasureSpec(500, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(500, View.MeasureSpec.EXACTLY)
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
        }
        // Bitmap作成。Chart側でもBitmap生成関数が用意されてるんだけど、透明が扱えないため
        val bitmap = Bitmap.createBitmap(barChart.width, barChart.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        barChart.draw(canvas)
        return bitmap
    }

}