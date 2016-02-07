package iwai.cellmon.ui.activity.main

import java.io.{BufferedReader, InputStreamReader}

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.FragmentActivity
import android.support.v7.app.{ActionBarDrawerToggle, AppCompatActivity}
import android.view.ViewGroup.LayoutParams._
import android.view.{Gravity, View, ViewGroup}
import android.widget._
import iwai.cellmon.R
import iwai.cellmon.ui.activity.TweakHelper
import iwai.cellmon.ui.fragment.about.AboutFragment
import iwai.cellmon.ui.fragment.cells.CellChangesFragment
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
//  with TweakHelper
  with Contexts[FragmentActivity]
  with Layout
  with IdGeneration {

  private var startManualButton = slot[Button]

  private var startAutoButton = slot[Button]
  private var stopAutoButton = slot[Button]

  private var logView = slot[EditText]

  lazy val pref = PreferenceManager.getDefaultSharedPreferences(this)

  lazy val verticalLayout = l[LinearLayout](
    w[Button] <~
      wire(startManualButton) <~
      text("ボタン1") <~
      lp[ViewGroup](MATCH_PARENT, WRAP_CONTENT) <~
      On.click {
        logView <~ text(fileList().mkString("\n"))
      },
    w[Button] <~
      wire(startAutoButton) <~
      text("ボタン2") <~
      lp[ViewGroup](MATCH_PARENT, WRAP_CONTENT) <~
      enable <~
      On.click {
        val reader = new BufferedReader(
          new InputStreamReader(
            openFileInput(fileList().last),
            "UTF-8"
          )
        )
        val s = Iterator.continually(reader.readLine()).takeWhile(_ != null).toList
        reader.close()
        logView <~ text(s.mkString("\n"))
      },
    w[Button] <~
      wire(stopAutoButton) <~
      text("ボタン3") <~
      lp[ViewGroup](MATCH_PARENT, WRAP_CONTENT) <~
      disable <~
      On.click {
        Ui.nop
      },
    w[EditText] <~
      wire(logView) <~
      size(12) <~
      matchParent <~
      gravity(Gravity.TOP) <~
      focus(false)
  ) <~
    matchParent <~
    vertical <~
    center

  var actionBarDrawerToggle: Option[ActionBarDrawerToggle] = None

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)
    //		setContentView(getUi(verticalLayout))
    startService(new Intent(this, classOf[CellLocationChangeListener]))


    setContentView(layout)

    toolBar.map(setSupportActionBar)
    getSupportActionBar.setDisplayHomeAsUpEnabled(true)
    getSupportActionBar.setHomeButtonEnabled(true)

    drawerLayout map { drawerLayout =>
      val drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.openMenu, R.string.clodeMenu) {
        override def onDrawerClosed(drawerView: View): Unit = {
          super.onDrawerClosed(drawerView)
          invalidateOptionsMenu()
          //					findFragmentById[MenuFragment](Id.menuFragment) map (_.showMainMenu)
        }

        override def onDrawerOpened(drawerView: View): Unit = {
          super.onDrawerOpened(drawerView)
          invalidateOptionsMenu()
        }
      }
      actionBarDrawerToggle = Some(drawerToggle)
      drawerLayout.setDrawerListener(drawerToggle)
    }

    if (savedInstanceState == null) {
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


  def itemSelected(section: MenuSection.Value, title: String) {
//    val builder = f[AboutFragment]
    		val builder = section match {
//    			case SPEAKERS => f[SpeakersFragment]
//    			case SCHEDULE => f[ScheduleFragment]
//    			case SOCIAL => f[SocialFragment]
//    			case CONTACTS => f[QrCodeFragment]
//    			case SPONSORS => f[SponsorsFragment]
//    			case PLACES => f[PlacesFragment]
    			case CELLS => f[CellChangesFragment]
//    			case LOCATIONS => f[PlacesFragment]
    			case ABOUT => f[AboutFragment]
    			case _ => throw new IllegalStateException
    		}
    runUi(
      (toolBar <~ tbTitle(title)) ~
        (drawerLayout <~ dlCloseDrawer(fragmentMenu)) ~
        replaceFragment(
          builder = builder,
          id = Id.mainFragment,
          tag = Some(Tag.mainFragment))
    )
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
