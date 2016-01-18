package iwai.cellmon.ui.activity

import android.app.Activity
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.preference.{EditTextPreference, ListPreference, Preference, PreferenceFragment}
import iwai.cellmon.R
import macroid.Contexts

// import macroid stuff
import macroid.FullDsl._

class SettingActivity extends Activity with Contexts[Activity] {

	override def onCreate(savedInstanceState: Bundle) = {
		super.onCreate(savedInstanceState)

		getFragmentManager.beginTransaction.replace(
			android.R.id.content,
			new SettingActivity.SettingFragment
		).commit
	}

	override def onDestroy() {
		super.onDestroy()
	}
}

object SettingActivity {
	val PREF_KEY_PERIOD = "period"
	val PREF_KEY_SWITCH_INTERVAL = "switchingInterval"

	class SettingFragment extends PreferenceFragment {

		val updatableKeys = List(PREF_KEY_PERIOD, PREF_KEY_SWITCH_INTERVAL)

		val summaryUpdater = new OnSharedPreferenceChangeListener() {
			override def onSharedPreferenceChanged(shared: SharedPreferences, key: String) = key match {
				case k if updatableKeys.contains(k) => updateSummary(findPreference(k))
				case _ =>
			}
		}

		override def onCreate(savedInstanceState: Bundle) {
			super.onCreate(savedInstanceState)

			addPreferencesFromResource(R.xml.setting)

			updatableKeys
					.map(findPreference(_))
					.map(updateSummary(_))
		}

		override def onResume() = {
			super.onResume

			getPreferenceScreen.getSharedPreferences
					.registerOnSharedPreferenceChangeListener(summaryUpdater)
		}

		override def onPause() = {
			super.onPause

			getPreferenceScreen.getSharedPreferences
					.unregisterOnSharedPreferenceChangeListener(summaryUpdater)
		}

		def updateSummary[P <: Preference](pref: P): Unit = pref match {
			case p: EditTextPreference => p.setSummary(p.getText)
			case p: ListPreference => p.setSummary(p.getEntry)
			case _ =>
		}
	}
}
