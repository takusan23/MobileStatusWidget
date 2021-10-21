package io.github.takusan23.mobilestatuswidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import io.github.takusan23.mobilestatuswidget.R
import io.github.takusan23.mobilestatuswidget.tool.MobileDataUsageTool
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * バンドを表示するウイジェット
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
                val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getBroadcast(context, 50, Intent(context, BandWidget::class.java), PendingIntent.FLAG_IMMUTABLE)
                } else {
                    PendingIntent.getBroadcast(context, 50, Intent(context, BandWidget::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
                }
                views.setOnClickPendingIntent(R.id.widget_band_update_button, pendingIntent)
                // 権限があるときのみ
                if (MobileDataUsageTool.isGrantedReadPhoneAndFineLocation(context)) {
                    GlobalScope.launch {
                        val bandTriple = MobileDataUsageTool.getBandDataFromEarfcnOrNrafcn(context)
                        // Triple#firstにnが付いてたら5Gのバンド
                        val isNRBand = bandTriple?.first?.contains("n") ?: false
                        val genIcon = if (isNRBand) R.drawable.ic_baseline_5g_24 else R.drawable.ic_baseline_4g_mobiledata_24
                        views.setTextViewText(R.id.widget_band_text_view, "Band : ${bandTriple?.first}\n(${bandTriple?.second})")
                        views.setTextViewText(R.id.widget_band_carrier_button, bandTriple?.third)
                        views.setTextViewCompoundDrawables(R.id.widget_band_carrier_button, 0, genIcon, 0, 0)
                        manager.updateAppWidget(id, views)
                    }
                } else {
                    views.setTextViewText(R.id.widget_band_update_button, context.getString(R.string.permission_not_granted))
                    manager.updateAppWidget(id, views)
                }
            }
        }
    }

}
