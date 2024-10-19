package io.github.takusan23.mobilestatuswidget

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import io.github.takusan23.mobilestatuswidget.activity.LicenseActivity
import io.github.takusan23.mobilestatuswidget.activity.PermissionRequestActivity
import io.github.takusan23.mobilestatuswidget.databinding.ActivityMainBinding
import io.github.takusan23.mobilestatuswidget.tool.MobileDataUsageTool
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    /** ViewBinding */
    private val viewBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        // 権限がなければ権限取得画面に飛ばす
        if (MobileDataUsageTool.isGrantedUsageStatusPermission(this) && MobileDataUsageTool.isGrantedReadPhoneAndFineLocation(this)) {
            // 権限ありますね
            // Toast.makeText(this, "権限が付与されています", Toast.LENGTH_SHORT).show()
        } else {
            // 権限ください画面
            Intent(this, PermissionRequestActivity::class.java).let { intent -> startActivity(intent) }
            return
        }

        // ライセンス
        viewBinding.activityMainLicenseButton.setOnClickListener {
            startActivity(Intent(this, LicenseActivity::class.java))
        }

        // ソースコード
        viewBinding.activityMainSourceCodeButton.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, "https://github.com/takusan23/MobileStatusWidget".toUri()))
        }

        // EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.root) { root, insets ->
            val systemInsets = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            root.updatePadding(
                left = systemInsets.left,
                top = systemInsets.top,
                right = systemInsets.right,
                bottom = systemInsets.bottom
            )
            insets
        }
    }
}