package io.github.takusan23.mobilestatuswidget.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import io.github.takusan23.mobilestatuswidget.R
import io.github.takusan23.mobilestatuswidget.databinding.ActivityPermissionRequestBinding
import io.github.takusan23.mobilestatuswidget.tool.MobileDataUsageTool

/**
 * 権限をお願いする画面
 * */
class PermissionRequestActivity : AppCompatActivity() {

    /** 権限コールバック */
    private val permissionCallBack = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultList ->
        if (resultList.all { entry -> entry.value }) {
            Toast.makeText(this, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show()
        }
    }

    /** ViewBinding */
    private val viewBinding by lazy { ActivityPermissionRequestBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        viewBinding.activityPermissionRequestPermissionButton.setOnClickListener {
            // とりあえずRuntime Permission
            permissionCallBack.launch(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.READ_PHONE_STATE))
        }

        viewBinding.activityPermissionRequestSettingButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }

    }

    override fun onResume() {
        super.onResume()
        if (MobileDataUsageTool.isGrantedUsageStatusPermission(this) && MobileDataUsageTool.isGrantedReadPhoneAndFineLocation(this)) {
            // 終わった。戻る
            Toast.makeText(this, getString(R.string.permission_setting_finish), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

}