package io.github.takusan23.mobilestatuswidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import io.github.takusan23.mobilestatuswidget.R
import io.github.takusan23.mobilestatuswidget.tool.MobileDataUsageTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Implementation of App Widget functionality.
 */
class BandWidget : AppWidgetProvider() {
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
         * ウイジェットを更新する関数
         * */
        fun updateAppWidget(context: Context) {
            val componentName = ComponentName(context, BandWidget::class.java)
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(componentName)
            ids.forEach { id ->
                // RemoteView
                val views = RemoteViews(context.packageName, R.layout.widget_band)
                // 更新。このクラスにブロードキャストを送信する
                val pendingIntent = PendingIntent.getBroadcast(context, 50, Intent(context, BandWidget::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
                views.setOnClickPendingIntent(R.id.widget_band_image_view, pendingIntent)
                // 権限があるときのみ
                if (MobileDataUsageTool.isGrantedReadPhoneAndFineLocation(context)) {
                    GlobalScope.launch {
                        val bandPair = MobileDataUsageTool.getEarfcnOrNrarfcn(context)
                        views.setTextViewText(R.id.widget_band_text_view, "バンド：${bandPair?.first}")
                        views.setTextViewText(R.id.widget_band_sub_text_view, bandPair?.second.toString())
                        manager.updateAppWidget(id, views)
                    }
                } else {
                    views.setTextViewText(R.id.widget_band_text_view, "権限がありません")
                    manager.updateAppWidget(id, views)
                }
            }
        }
    }

}
