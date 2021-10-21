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
import io.github.takusan23.mobilestatuswidget.tool.MobileDataUsageTool

/**
 * モバイルデータの使用量を表示するウイジェット
 */
class MobileDataUsageWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateAppWidget(context)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    /**
     * 更新ボタンを受け取る
     * */
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (context != null) {
            updateAppWidget(context)
        }
    }

    companion object {

        /**
         * ウイジェットを更新する関数
         * */
        fun updateAppWidget(context: Context) {
            val componentName = ComponentName(context, MobileDataUsageWidget::class.java)
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(componentName)
            ids.forEach { id ->
                // RemoteView
                val views = RemoteViews(context.packageName, R.layout.widget_mobile_data_usage)
                // 更新。このクラスにブロードキャストを送信する
                val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getBroadcast(context, 25, Intent(context, MobileDataUsageWidget::class.java), PendingIntent.FLAG_IMMUTABLE)
                } else {
                    PendingIntent.getBroadcast(context, 25, Intent(context, MobileDataUsageWidget::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
                }
                views.setOnClickPendingIntent(R.id.widget_mobile_data_usage_image_view, pendingIntent)
                // 権限があるときのみ
                if (MobileDataUsageTool.isGrantedUsageStatusPermission(context)) {
                    // 通信量を入れる
                    val usageMB = String.format("%.2f", MobileDataUsageTool.getMobileDataUsageFromCurrentMonth(context) / 1024f / 1024f)
                    val usageGB = String.format("%.2f", MobileDataUsageTool.getMobileDataUsageFromCurrentMonth(context) / 1024f / 1024f / 1024f)
                    views.setTextViewText(R.id.widget_mobile_data_usage_text_view, "$usageGB GB ${context.getString(R.string.widget_mobile_data_used)}")
                    views.setTextViewText(R.id.widget_mobile_data_usage_sub_text_view, "($usageMB MB)")
                    manager.updateAppWidget(id, views)
                } else {
                    views.setTextViewText(R.id.widget_mobile_data_usage_text_view, context.getString(R.string.permission_not_granted))
                    manager.updateAppWidget(id, views)
                }
            }
        }
    }

}