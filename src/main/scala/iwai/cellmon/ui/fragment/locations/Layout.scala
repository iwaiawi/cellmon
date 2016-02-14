/*
 * Copyright (C) 2015 47 Degrees, LLC http://47deg.com hello@47deg.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may
 *  not use this file except in compliance with the License. You may obtain
 *  a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package iwai.cellmon.ui.fragment.locations

import android.support.v7.widget.RecyclerView
import android.widget._
import macroid.ActivityContextWrapper
import macroid.FullDsl._

class LocationChangeItemSlots {
	var content = slot[LinearLayout]
	var changeAt = slot[TextView]
	var location = slot[TextView]
	var cell = slot[TextView]
}

class LocationChangeItemLayout(implicit context: ActivityContextWrapper)
	extends ItemStyles {

	val slots = new LocationChangeItemSlots

	val layout = l[LinearLayout](
		w[TextView] <~ wire(slots.changeAt) <~ changeAtStyle,
		w[TextView] <~ wire(slots.location) <~ locationStyle,
		w[TextView] <~ wire(slots.cell) <~ cellStyle
	) <~ wire(slots.content) <~ contentStyle
}

class LocationChangeViewHolder(itemLayout: LocationChangeItemLayout)
	(implicit context: ActivityContextWrapper)
	extends RecyclerView.ViewHolder(getUi(itemLayout.layout)) {

	// shortcut to slot
	val slots = itemLayout.slots
}