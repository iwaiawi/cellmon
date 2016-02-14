package iwai.cellmon.ui.activity.main

import java.io.{BufferedReader, InputStreamReader}

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.FragmentActivity
import android.support.v7.app.{ActionBarDrawerToggle, AppCompatActivity}
import android.view.ViewGroup.LayoutParams._
import android.view.{MenuItem, Gravity, View, ViewGroup}
import android.widget._
import iwai.cellmon.R
import iwai.cellmon.ui.common.Id
import iwai.cellmon.ui.common.Id._
import iwai.cellmon.ui.activity.TweakHelper
import iwai.cellmon.ui.fragment.about.AboutFragment
import iwai.cellmon.ui.fragment.cells.CellChangesFragment
import iwai.cellmon.ui.fragment.locations.LocationChangesFragment
import iwai.cellmon.ui.fragment.menu.MenuSection._
import iwai.cellmon.ui.fragment.menu.{MenuFragment, MenuSection}
import iwai.cellmon.ui.service.{CellLocationChangeListener, LocationChangeListener, CellChangeListener}
import macroid.{Contexts, IdGeneration, Ui}

// import macroid stuff

import com.fortysevendeg.macroid.extras.DrawerLayoutTweaks._
import com.fortysevendeg.macroid.extras.FragmentExtras._
import com.fortysevendeg.macroid.extras.ToolbarTweaks._
import iwai.support.macroid.Tweaks._
import macroid.FullDsl.{gravity => _, _}
import macroid.contrib.LpTweaks._
import macroid.contrib.TextTweaks._


class MainActivity
	extends AppCompatActivity
		with Contexts[FragmentActivity]
		with Layout
		with IdGeneration {

	var actionBarDrawerToggle: Option[ActionBarDrawerToggle] = None

	override def onCreate(savedInstanceState: Bundle) = {
		super.onCreate(savedInstanceState)
		startService(new Intent(this, classOf[CellLocationChangeListener]))

		setContentView(getUi(layout))

		toolBar.map(setSupportActionBar)
		getSupportActionBar.setDisplayHomeAsUpEnabled(true)
		getSupportActionBar.setHomeButtonEnabled(true)

		drawerLayout map { drawerLayout =>
			val drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.openMenu, R.string.clodeMenu) {
				override def onDrawerClosed(drawerView: View): Unit = {
					super.onDrawerClosed(drawerView)
					invalidateOptionsMenu()
				}

				override def onDrawerOpened(drawerView: View): Unit = {
					super.onDrawerOpened(drawerView)
					invalidateOptionsMenu()
				}
			}
			actionBarDrawerToggle = Some(drawerToggle)
			drawerLayout.setDrawerListener(drawerToggle)
		}

		if(savedInstanceState == null) {
			runUi(
				replaceFragment(
					builder = f[MenuFragment],
					id = Id.menuFragment,
					tag = Some(Tag.menuFragment)))
		}
	}

	override def onPostCreate(savedInstanceState: Bundle): Unit = {
		super.onPostCreate(savedInstanceState)
		actionBarDrawerToggle map (_.syncState)
	}

	override def onConfigurationChanged(newConfig: Configuration): Unit = {
		super.onConfigurationChanged(newConfig)
		actionBarDrawerToggle map (_.onConfigurationChanged(newConfig))
	}

	override def onOptionsItemSelected(item: MenuItem): Boolean = {
		if(actionBarDrawerToggle.isDefined && actionBarDrawerToggle.get.onOptionsItemSelected(item)) true
		else super.onOptionsItemSelected(item)
	}

	def itemSelected(section: MenuSection.Value, title: Id[StringR]): Ui[_] = {
		val builder = section match {
			case CELLS => f[CellChangesFragment]
			case LOCATIONS => f[LocationChangesFragment]
			case ABOUT => f[AboutFragment]
			case _ => throw new IllegalStateException
		}

		(toolBar <~ tbTitle(title)) ~
			(drawerLayout <~ dlCloseDrawer(fragmentMenu)) ~
			replaceFragment(
				builder = builder,
				id = Id.mainFragment,
				tag = Some(Tag.mainFragment))
	}

	override def onResume = {
		super.onResume
	}

	override def onPause = {
		super.onPause
	}

	override def onDestroy() = {
		super.onDestroy()
	}

	override def onBackPressed() = {
		super.onBackPressed()
	}
}
