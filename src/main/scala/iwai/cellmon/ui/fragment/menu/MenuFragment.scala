/*
 * Copyright (C) 2015 47 Degrees, LLC http://47deg.com hello@47deg.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package iwai.cellmon.ui.fragment.menu

import android.app.Activity
import android.content.{Context, Intent}
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.{LayoutInflater, View, ViewGroup}
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import iwai.cellmon.R
import iwai.cellmon.ui._
import iwai.cellmon.ui.activity.main.MainActivity
import iwai.cellmon.ui.common.Id._
import iwai.cellmon.ui.fragment.menu.MenuSection._
import macroid.FullDsl._
import macroid._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._

//import scala.concurrent.ExecutionContext.Implicits.global

class MenuFragment
	extends Fragment
		with Contexts[Fragment]
		//  with ComponentRegistryImpl
		//  with UiServices
		with IdGeneration
		with Layout {

	private var mainActivity: Option[MainActivity] = None

	val menuItemList = List(
		MainMenuViewModel(Id.cells, R.string.cells, R.drawable.menu_icon_places, CELLS),
		MainMenuViewModel(Id.locations, R.string.locations, R.drawable.menu_icon_places, LOCATIONS),
		MainMenuViewModel(Id.about, R.string.about, R.drawable.menu_icon_about, ABOUT)
	)

	private val previousItemSelectedKey = "previous_item_selected_key"

	val defaultItem = 0

	lazy val mainMenuAdapter: MainMenuAdapter = new MainMenuAdapter(menuItemList, afterCreateViewHolder = { vh =>
		vh.slots.content <~ FuncOn.click { v: View =>
			val menuItem = menuItemList(vh.getAdapterPosition)
			itemSelected(menuItem)
		}
	})

	override def onAttach(context: Context) = {
		super.onAttach(context)
		mainActivity = Some(context.asInstanceOf[MainActivity])
	}

	override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
		getUi(layout)
	}

	override def onViewCreated(view: View, savedInstanceState: Bundle) = {
		super.onViewCreated(view, savedInstanceState)
		val defaultSection = Option(savedInstanceState) map (_.getInt(previousItemSelectedKey, defaultItem)) getOrElse defaultItem

		runUi(
			(recyclerView <~ rvLayoutManager(new LinearLayoutManager(fragmentContextWrapper.application))) ~
				(recyclerView <~ rvAdapter(mainMenuAdapter)) ~
				itemSelected(menuItemList(defaultSection), savedInstanceState == null)
		)

	}

	override def onSaveInstanceState(outState: Bundle): Unit = {
		outState.putInt(previousItemSelectedKey, mainMenuAdapter.selectedModel map (menuItemList.indexOf(_)) getOrElse defaultItem)
		super.onSaveInstanceState(outState)
	}

	override def onDetach(): Unit = {
		mainActivity = None
		super.onDetach()
	}

	def itemSelected(menuItem: MainMenuViewModel, callCallback: Boolean = true): Ui[_] = {
		mainMenuAdapter.selectItem(Some(menuItem))

		mainActivity match {
			case Some(activity) if callCallback => activity.itemSelected(menuItem.section, menuItem.name)
			case _ => Ui.nop
		}
	}

}
