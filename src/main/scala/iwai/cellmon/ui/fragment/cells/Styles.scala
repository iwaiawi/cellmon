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

package iwai.cellmon.ui.fragment.cells

import android.view.Gravity
import android.widget.{ImageView, LinearLayout, ScrollView, TextView}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.LinearLayoutTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import iwai.cellmon.R
import iwai.cellmon.model.core.entity.cell._
import macroid.{ContextWrapper, Tweak}
import macroid.FullDsl._

import scala.language.postfixOps

trait Styles {

	def rootStyle(implicit context: ContextWrapper): Tweak[LinearLayout] =
		vMatchParent +
				llVertical +
				vPaddings(resGetDimensionPixelSize(R.dimen.padding_default))

	val scrollStyle: Tweak[ScrollView] = llMatchWeightVertical

	val contentStyle: Tweak[LinearLayout] =
		vMatchWidth +
				llVertical

	val about47ContentStyle: Tweak[LinearLayout] =
		vMatchWidth +
				llHorizontal +
				llGravity(Gravity.CENTER)

	def about47ImageStyle(implicit context: ContextWrapper): Tweak[ImageView] =
		vWrapContent +
				ivSrc(R.drawable.logo_47deg) +
				vPadding(0, 0, resGetDimensionPixelSize(R.dimen.padding_about_logo), 0)

	def about47TextStyle(implicit context: ContextWrapper): Tweak[TextView] =
		vWrapContent +
				tvText(R.string.about47) +
				tvColorResource(R.color.primary) +
				tvSize(resGetInteger(R.integer.text_small))

	def titleStyle(implicit context: ContextWrapper): Tweak[TextView] =
		vMatchParent +
				tvText(R.string.codeOfConduct) +
				tvColorResource(R.color.accent) +
				tvSize(resGetInteger(R.integer.text_big))

	def descriptionStyle(implicit context: ContextWrapper): Tweak[TextView] =
		vWrapContent +
				tvColorResource(R.color.primary) +
				tvSize(context.application.getResources.getInteger(R.integer.text_medium)) +
				vPadding(0, resGetDimensionPixelSize(R.dimen.padding_default), 0, 0)

}

trait AdapterStyles {

	def tvCell(cell: Cell): Tweak[TextView] = cell match {
		case GsmCellLocation(lac: Int, cid: Int, psc: Int) =>
			tvText("GSM(lac: %05d, cid:%09d, psc:%03d)".format(lac, cid, psc))
		case LteCellIdentity(mcc: Int, mnc: Int, ci: Int, pci: Int, tac: Int) =>
			tvText("LTE(mcc:%03d, mnc:%02d, ci:%09d, pci:%03d, tac:%05d)".format(mcc, mnc, ci, pci, tac))
		case WcdmaCellIdentity(mcc: Int, mnc: Int, lac: Int, cid: Int, psc: Int) =>
			tvText("WCDMA(mcc:%03d, mnc:%02d, lac:%05d, ci:%09d, psc:%03d)".format(mcc, mnc, lac, cid, psc))
		case _: NoCell => tvText("No Cell")
		case _ => tvText(cell.toString)
	}
}

trait ItemStyles {

	def contentStyle(implicit context: ContextWrapper): Tweak[LinearLayout] =
		vMatchParent +
				vPaddings(resGetDimensionPixelSize(R.dimen.padding_default)) +
				llVertical +
				//      llGravity(Gravity.CENTER) +
				vBackground(R.drawable.background_list_default)

	def changeAtStyle(implicit context: ContextWrapper): Tweak[TextView] =
		vWrapContent +
				tvColorResource(R.color.accent) +
				tvSize(resGetInteger(R.integer.text_small))

	//+
	//      vPadding(0, resGetDimensionPixelSize(R.dimen.padding_default), 0, 0)
	//
	def cellStyle(implicit context: ContextWrapper): Tweak[TextView] =
		vWrapContent +
				tvColorResource(R.color.primary) +
				tvSize(resGetInteger(R.integer.text_medium)) //+
	//      vPadding(0, resGetDimensionPixelSize(R.dimen.padding_default), 0, 0)

}