package iwai.cellmon

import android.support.multidex.MultiDexApplication
import timber.log.Timber
import timber.log.Timber.DebugTree

class LoggingApplication extends MultiDexApplication {
	override def onCreate(): Unit = {
		super.onCreate

		if (BuildConfig.DEBUG) {
			Timber.plant(new DebugTree())
		} else {
//			Timber.plant(new CrashReportingTree())
		}
	}
}
