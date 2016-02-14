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

import android.widget.{LinearLayout, TextView}
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import iwai.cellmon.R
import iwai.cellmon.model.core.entity.cell._
import iwai.cellmon.model.core.entity.location.Location
import macroid.{ContextWrapper, Tweak}

import scala.language.postfixOps

trait AdapterStyles {

}

trait ItemStyles {

	def contentStyle(implicit context: ContextWrapper): Tweak[LinearLayout] =
		vMatchParent +
			vPaddings(resGetDimensionPixelSize(R.dimen.padding_default)) +
			llVertical +
			vBackground(R.drawable.background_list_default)

	def changeAtStyle(implicit context: ContextWrapper): Tweak[TextView] =
		vWrapContent +
			tvColorResource(R.color.accent) +
			tvSize(resGetInteger(R.integer.text_small))

	def locationStyle(implicit context: ContextWrapper): Tweak[TextView] =
		vWrapContent +
			tvColorResource(R.color.primary) +
			tvSize(resGetInteger(R.integer.text_big)) +
			vPadding(0, resGetDimensionPixelSize(R.dimen.padding_default_extra_small))

	def cellStyle(implicit context: ContextWrapper): Tweak[TextView] =
		vWrapContent +
			tvColorResource(R.color.primary_dark) +
			tvSize(resGetInteger(R.integer.text_small)) +
			vPadding(resGetDimensionPixelSize(R.dimen.padding_default), 0, 0, 0)

}