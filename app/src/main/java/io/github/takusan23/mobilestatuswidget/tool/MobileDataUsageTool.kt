package io.github.takusan23.mobilestatuswidget.tool

import android.Manifest
import android.app.AppOpsManager
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Process
import android.telephony.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * モバイルデータ使用量を取得するなど
 * */
object MobileDataUsageTool {

    /**
     * PACKAGE_USAGE_STATSの権限が付与されているか確認する
     * @return 権限があればtrue
     * */
    fun isGrantedUsageStatusPermission(context: Context): Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            // Android 10 以降
            appOpsManager.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
        } else {
            // Android 9 以前
            appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    /**
     * ACCESS_FINE_LOCATION と READ_PHONE_STATE の権限を取得済みかどうか
     * */
    fun isGrantedReadPhoneAndFineLocation(context: Context): Boolean {
        val accessFine = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
        val readPhone = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE)
        return (accessFine == PackageManager.PERMISSION_GRANTED) && (readPhone == PackageManager.PERMISSION_GRANTED)
    }

    /**
     * 今月のモバイルデータ利用量を取得する。単位はバイト
     * @return バイト単位で返す
     * */
    fun getMobileDataUsageFromCurrentMonth(context: Context): Long {
        val networkStatsManager = context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
        // 集計開始の日付その月の最初の日
        val startTime = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.time.time
        // 集計終了は現在時刻
        val endTime = Calendar.getInstance().time.time
        // 問い合わせる
        val bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE, null, startTime, endTime)
        // 送信 + 受信
        return bucket.txBytes + bucket.rxBytes
    }

    /**
     * 一日ごとの通信量を配列で返す。開始は1日。
     * @return 配列。バイト単位で返す
     * */
    fun getMobileDataUsageDayListFromCurrentMonth(context: Context): List<Long> {
        val mobileDataUsageList = arrayListOf<Long>()
        val networkStatsManager = context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
        // 集計開始の日付その月の最初の日
        val calendar = Calendar.getInstance()
        val startCalendar = calendar.apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        // 今日の日付
        val currentDate = calendar.get(Calendar.DATE)
        repeat(currentDate) {
            // 収集開始
            val startUnixTimeMs = startCalendar.time.time
            // 足して
            startCalendar.add(Calendar.DAY_OF_MONTH, 1)
            // 収集終了時刻
            val endUnixTime = startCalendar.time.time
            // 問い合わせる
            val bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE, null, startUnixTimeMs, endUnixTime)
            // 配列に入れる
            mobileDataUsageList.add(bucket.txBytes + bucket.rxBytes)
        }
        return mobileDataUsageList
    }

    /**
     * 現在接続している　絶対無線周波数チャンネル番号 を返す
     *
     * 失敗したらnullを返す
     *
     * @return 一個目はBand、二個目は周波数チャンネル番号？
     * */
    suspend fun getEarfcnOrNrarfcn(context: Context): Pair<Int, Int>? {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return null
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        // Android Q 以降は最新の値返ってこないらしい（キャッシュを返すため、リクエストが必要）のでコルーチンで解決
        val callIdentity = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            suspendCoroutine {
                telephonyManager.requestCellInfoUpdate(context.mainExecutor, object : TelephonyManager.CellInfoCallback() {
                    override fun onCellInfo(cellInfoList: MutableList<CellInfo>) {
                        val cellInfo = cellInfoList[0]
                        it.resume(cellInfo)
                    }
                })
            }
        } else {
            telephonyManager.allCellInfo[0]
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 以降。5Gに対応
            return when {
                callIdentity is CellInfoLte -> {
                    val earfcn = callIdentity.cellIdentity.earfcn
                    Pair(BandDictionary.toLTEBand(earfcn), earfcn)
                }
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) && callIdentity is CellInfoNr -> {
                    val nrarfcn = (callIdentity.cellIdentity as CellIdentityNr).nrarfcn
                    Pair(BandDictionary.toNRBand(nrarfcn), nrarfcn)
                }
                else -> null
            }
        } else {
            // Android 9 以前
            return when (callIdentity) {
                is CellInfoLte -> {
                    val earfcn = callIdentity.cellIdentity.earfcn
                    Pair(BandDictionary.toLTEBand(earfcn), earfcn)
                }
                else -> null
            }
        }
    }

}