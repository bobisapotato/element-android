/*
 * Copyright 2019 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.app.features.settings

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import im.vector.app.R
import im.vector.app.features.navigation.Navigator
import im.vector.app.features.pin.PinActivity
import im.vector.app.features.pin.PinCodeStore
import im.vector.app.features.pin.PinLocker
import im.vector.app.features.pin.PinMode
import javax.inject.Inject

class VectorSettingsPinFragment @Inject constructor(
        private val pinLocker: PinLocker,
        private val pinCodeStore: PinCodeStore,
        private val navigator: Navigator
) : VectorSettingsBaseFragment() {

    override var titleRes = R.string.settings_security_application_protection_screen_title
    override val preferenceXmlRes = R.xml.vector_settings_pin

    private val usePinCodePref by lazy {
        findPreference<SwitchPreference>(VectorPreferences.SETTINGS_SECURITY_USE_PIN_CODE_FLAG)!!
    }

    override fun bindPref() {
        refreshPinCodeStatus()
    }

    private fun refreshPinCodeStatus() {
        lifecycleScope.launchWhenResumed {
            val hasPinCode = pinCodeStore.hasEncodedPin()
            usePinCodePref.isChecked = hasPinCode
            usePinCodePref.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val pinMode = if (hasPinCode) {
                    PinMode.DELETE
                } else {
                    PinMode.CREATE
                }
                navigator.openPinCode(this@VectorSettingsPinFragment, pinMode)
                true
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PinActivity.PIN_REQUEST_CODE) {
            pinLocker.unlock()
            refreshPinCodeStatus()
        }
    }
}
