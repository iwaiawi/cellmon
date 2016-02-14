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
import android.widget._
import iwai.cellmon.ui.common.CheckableFrameLayout
import macroid.FullDsl._
import macroid.ActivityContextWrapper

trait Layout
	extends Styles {

	var drawerMenuLayout = slot[LinearLayout]

	var bigImageLayout = slot[FrameLayout]

	var bigImage = slot[ImageView]

	var conferenceTitle = slot[TextView]

	var conferenceSelector = slot[ImageView]

	var recyclerView = slot[RecyclerView]

	def layout(implicit context: ActivityContextWrapper) = {
		l[LinearLayout](
			l[FrameLayout](
				w[ImageView] <~ wire(bigImage) <~ bigImageStyle,
				l[LinearLayout](
					w[TextView] <~ wire(conferenceTitle) /* <~ conferenceTitleStyle*/ ,
					w[ImageView] <~ wire(conferenceSelector) /* <~ conferenceSelectorStyle*/
				) <~ bigImageActionLayout
			) <~ wire(bigImageLayout) <~ bigImageLayoutStyle,
			w[RecyclerView] <~ wire(recyclerView) <~ drawerMenuStyle
		) <~ menuStyle
	}

}

class MainMenuItemSlots {
	var content = slot[CheckableFrameLayout]
	var title = slot[TextView]
}

class MainMenuItemLayout(implicit context: ActivityContextWrapper)
	extends ItemStyles {

	val slots = new MainMenuItemSlots
	//	var menuItem = slot[TextView]

	//	val content = layout

	val layout = l[CheckableFrameLayout](
		w[TextView] <~ wire(slots.title) <~ textMenuItemStyle
	) <~ wire(slots.content) <~ mainMenuItemStyle
}

class MainMenuViewHolder(itemLayout: MainMenuItemLayout)
	(implicit context: ActivityContextWrapper)
	extends RecyclerView.ViewHolder(getUi(itemLayout.layout)) {

	// shortcut to slot
	val slots = itemLayout.slots
}
