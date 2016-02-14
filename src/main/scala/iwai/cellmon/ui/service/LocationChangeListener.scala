package iwai.cellmon.ui.service

import android.app.Service
import android.os.Bundle
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.{ConnectionCallbacks, OnConnectionFailedListener}
import com.google.android.gms.location.LocationServices.FusedLocationApi
import com.google.android.gms.location.{LocationListener, LocationRequest, LocationServices}
import iwai.cellmon.model.core.entity.location.{Location, LocationChange}
import iwai.cellmon.model.core.service.LocationChangeService
import iwai.cellmon.model.support.android.repository.locationchange.LocationChangeFileRepository
import iwai.cellmon.ui._
import iwai.cellmon.ui.common.{LocationChangePutCompleteEvent, CellChangePutCompleteEvent}
import macroid.Contexts
import macroid.FullDsl._
import timber.log.Timber

import scala.util.Try
import scalaz.concurrent.Task
import scalaz.{-\/, \/-}

trait LocationChangeListener {
	self: CellChangeListener with Service with Contexts[Service] =>

	def startLocationChangeListener() = {
		googleApiClient.connect()
	}

	def stopLocationChangeListener() = {
		if(googleApiClient.isConnected) googleApiClient.disconnect()
	}

	lazy private val googleApiClient: GoogleApiClient = new GoogleApiClient.Builder(getApplicationContext)
		.addApi(LocationServices.API)
		.addConnectionCallbacks(connectionCallBack)
		.addOnConnectionFailedListener(connectionFailedListener)
		.build()

	private val connectionCallBack = new ConnectionCallbacks {
		override def onConnected(bundle: Bundle): Unit = {
			val request = new LocationRequest
			//      request.setInterval(15 * 1000)
			//      request.setFastestInterval(15 * 1000)
			request.setPriority(LocationRequest.PRIORITY_NO_POWER)
			FusedLocationApi.requestLocationUpdates(googleApiClient, request, listener)
		}

		override def onConnectionSuspended(cause: Int): Unit = {
			val causeMessage = cause match {
				case ConnectionCallbacks.CAUSE_NETWORK_LOST => "network lost"
				case ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED => "service disconnected"
			}
			Timber.w(s"GoogleApiClient's connection was suspended by {$causeMessage}.")
			googleApiClient.connect() // reconnect
		}
	}

	private val connectionFailedListener = new OnConnectionFailedListener {
		override def onConnectionFailed(result: ConnectionResult): Unit = {
			val message = s"GoogleApiClient's connection was failed by {$result.getErrorCode}: {$result.getErrorMessage}."
			Timber.e(message)
			runUi(uiShortToast(message))
		}
	}

	private val listener = new LocationListener {
		override def onLocationChanged(location: android.location.Location): Unit = {
			val change = LocationChange(Location(location), lastCell)
			storeLocationChange(change)
		}
	}

	def storeLocationChange(change: LocationChange): Unit = {
		val repo = new LocationChangeFileRepository
		val task: Task[(LocationChangeService, LocationChange)] = LocationChangeService(repo).store(change)

		task.runAsync {
			case \/-((_, result)) => eventBus.post(LocationChangePutCompleteEvent(Try(result)))
			case -\/(e) => Timber.e(e, e.getMessage)
		}
	}
}
