package io.github.takusan23.mobilestatuswidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import io.github.takusan23.mobilestatuswidget.R
import io.github.takusan23.mobilestatuswidget.tool.LineGraphTool
import io.github.takusan23.mobilestatuswidget.tool.MobileDataUsageTool

/**
 * モバイルデータ利用量をグラフにして表示するウイジェット
 */
class MobileDataUsageGraphWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateAppWidget(context)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (context != null) {
            updateAppWidget(context)
        }
    }

    companion object {

        /**
         * ウイジェット更新関数
         * */
        fun updateAppWidget(context: Context) {
            val componentName = ComponentName(context, MobileDataUsageGraphWidget::class.java)
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(componentName)
            ids.forEach { id ->
                // RemoteView
                val views = RemoteViews(context.packageName, R.layout.widget_mobile_data_usage_graph)
                // 更新。このクラスにブロードキャストを送信する
                val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getBroadcast(context, 128, Intent(context, MobileDataUsageGraphWidget::class.java), PendingIntent.FLAG_MUTABLE)
                } else {
                    PendingIntent.getBroadcast(context, 128, Intent(context, MobileDataUsageGraphWidget::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
                }
                views.setOnClickPendingIntent(R.id.widget_mobile_data_usage_graph_update_button, pendingIntent)
                // 権限があるときのみ
                if (MobileDataUsageTool.isGrantedUsageStatusPermission(context)) {
                    // グラフ作成
                    val bitmap = LineGraphTool.createGraphImage(context)
                    views.setImageViewBitmap(R.id.widget_mobile_data_usage_graph_image_view, bitmap)
                    // 使用量も出す
                    val usageGB = String.format("%.2f", MobileDataUsageTool.getMobileDataUsageFromCurrentMonth(context) / 1024f / 1024f / 1024f)
                    views.setTextViewText(R.id.widget_mobile_data_usage_graph_update_button, "$usageGB GB")
                    manager.updateAppWidget(id, views)
                } else {
                    views.setTextViewText(R.id.widget_mobile_data_usage_graph_update_button, context.getString(R.string.permission_not_granted))
                    manager.updateAppWidget(id, views)
                }
            }
        }
    }

}
