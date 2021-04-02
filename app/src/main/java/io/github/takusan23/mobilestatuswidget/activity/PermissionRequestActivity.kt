package io.github.takusan23.mobilestatuswidget.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import io.github.takusan23.mobilestatuswidget.databinding.ActivityPermissionRequestBinding

class PermissionRequestActivity : AppCompatActivity() {
    /** ViewBinding */
    private val viewBinding by lazy { ActivityPermissionRequestBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        viewBinding.activityPermissionRequestPermissionButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }

    }
}