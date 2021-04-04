package io.github.takusan23.mobilestatuswidget.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.takusan23.mobilestatuswidget.databinding.ActivityLicenseBinding

/**
 * ライセンス画面
 * */
class LicenseActivity : AppCompatActivity() {

    /** ViewBinding */
    private val viewBinding by lazy { ActivityLicenseBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        viewBinding.activityLicenseTextView.text = """
            --- PhilJay/MPAndroidChart ---
            
            Copyright 2020 Philipp Jahoda

            Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

            http://www.apache.org/licenses/LICENSE-2.0

            Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

            --- Kotlin/kotlinx.coroutines ---
            
            Copyright 2000-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.

            Licensed under the Apache License, Version 2.0 (the "License");
            you may not use this file except in compliance with the License.
            You may obtain a copy of the License at

                http://www.apache.org/licenses/LICENSE-2.0

            Unless required by applicable law or agreed to in writing, software
            distributed under the License is distributed on an "AS IS" BASIS,
            WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
            See the License for the specific language governing permissions and
            limitations under the License.

        """.trimIndent()

    }
}