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

import android.support.v7.widget.RecyclerView
import android.view.View.OnClickListener
import android.view.{View, ViewGroup}
import MenuSection._
import iwai.cellmon.R
import iwai.cellmon.ui.common.Id.{DrawableR, StringR}
import iwai.cellmon.ui.common.{Id, CheckableFrameLayout}
import macroid.{Tweak, Ui, IdGeneration, ActivityContextWrapper}
import com.fortysevendeg.macroid.extras.TextTweaks._
import iwai.support.macroid.CheckableViewTweaks._
import macroid.FullDsl._

class MainMenuAdapter(
	viewModels: Seq[MainMenuViewModel],
	afterCreateViewHolder: MainMenuViewHolder => Ui[_] = (_ => Ui.nop)
)(implicit context: ActivityContextWrapper)
	extends RecyclerView.Adapter[MainMenuViewHolder]
		with IdGeneration {

	var selectedModel: Option[MainMenuViewModel] = None

	override def onCreateViewHolder(parentViewGroup: ViewGroup, i: Int): MainMenuViewHolder = {
		val viewHolder = new MainMenuViewHolder(new MainMenuItemLayout)
		runUi(afterCreateViewHolder(viewHolder))
		viewHolder
	}

	override def getItemCount: Int = viewModels.size

	override def onBindViewHolder(viewHolder: MainMenuViewHolder, position: Int): Unit = {
		val model = viewModels(position)

		runUi(
			(viewHolder.slots.title <~ tvText(model.name)
				<~ tvCompoundDrawablesWithIntrinsicBounds(model.icon, 0, 0, 0)) ~
				(viewHolder.slots.content <~ (selectedModel match {
					case Some(selected) if selected.id == model.id => cvChecked(true)
					case _ => cvChecked(false)
				}))
		)
	}

	def selectItem(model: Option[MainMenuViewModel]) {
		selectedModel = model
		notifyDataSetChanged()
	}
}

case class MainMenuViewModel(id: Int, name: Id[StringR], icon: Id[DrawableR], section: MenuSection)
