package io.github.takusan23.mobilestatuswidget.tool

import android.app.AppOpsManager
import android.content.Context
import android.content.Context.APP_OPS_SERVICE
import android.os.Process

/**
 * 権限が付与されているかを確認する関数がある
 * */
object PermissionCheckTool {

    /**
     * PACKAGE_USAGE_STATSの権限が付与されているか確認する
     * @return 権限があればtrue
     * */
    fun isGrantedUsageStatusPermission(context: Context): Boolean {
        val appOpsManager = context.getSystemService(APP_OPS_SERVICE) as AppOpsManager
        val mode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            // Android 10 以降
            appOpsManager.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
        } else {
            // Android 9 以前
            appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

}