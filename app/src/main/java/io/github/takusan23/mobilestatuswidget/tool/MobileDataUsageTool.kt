package io.github.takusan23.mobilestatuswidget.tool

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
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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
        // 今日の日付
        val currentDate = calendar.get(Calendar.DAY_OF_MONTH)
        // 月初めに移動
        val startCalendar = calendar.apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        // 通信量を足していくので
        var addMobileDataUsage = 0L
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
            addMobileDataUsage += bucket.txBytes + bucket.rxBytes
            mobileDataUsageList.add(addMobileDataUsage)
        }
        return mobileDataUsageList
    }

    /**
     * 現在接続している基地局のバンド番号、周波数チャンネル番号、キャリア名？を返す
     *
     * 失敗したらnullを返す
     *
     * @return 一個目はバンド、二個目は周波数チャンネル番号、三番目はキャリア名（Android 9以降のみ対応）
     * */
    suspend fun getBandDataFromEarfcnOrNrafcn(context: Context): Triple<String, Int, String>? {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return null
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        // Android Q 以降は最新の値返ってこないらしい（キャッシュを返すため、リクエストが必要）のでコルーチンで解決
        val callIdentity: CellInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // コルーチンでコールバック形式も同期っぽくかける
            suspendCoroutine {
                telephonyManager.requestCellInfoUpdate(context.mainExecutor, object : TelephonyManager.CellInfoCallback() {
                    override fun onCellInfo(cellInfoList: MutableList<CellInfo>) {
                        if (cellInfoList.isEmpty()) {
                            it.resume(null)
                        } else {
                            val cellInfo = cellInfoList.filterIsInstance<CellInfoNr>().firstOrNull() ?: cellInfoList.firstOrNull()
                            it.resume(cellInfo)
                        }
                    }
                })
            }
        } else {
            val cellInfoList = telephonyManager.allCellInfo
            if (cellInfoList.isEmpty()) {
                null
            } else {
                cellInfoList[0]
            }
        } ?: return null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 以降。5Gに対応
            return when {
                // LTE
                callIdentity is CellInfoLte -> {
                    val earfcn = callIdentity.cellIdentity.earfcn
                    Triple(BandDictionary.toLTEBand(earfcn), earfcn, callIdentity.cellIdentity.operatorAlphaShort.toString())
                }
                // 5G (NR)
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) && callIdentity is CellInfoNr -> {
                    val nrarfcn = (callIdentity.cellIdentity as CellIdentityNr).nrarfcn
                    Triple(BandDictionary.toNRBand(nrarfcn), nrarfcn, callIdentity.cellIdentity.operatorAlphaShort.toString())
                }
                else -> null
            }
        } else {
            // Android 9 以前
            return when (callIdentity) {
                // LTE
                is CellInfoLte -> {
                    val earfcn = callIdentity.cellIdentity.earfcn
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        Triple(BandDictionary.toLTEBand(earfcn), earfcn, callIdentity.cellIdentity.operatorAlphaShort.toString())
                    } else {
                        Triple(BandDictionary.toLTEBand(earfcn), earfcn, "")
                    }
                }
                else -> null
            }
        }
    }

}