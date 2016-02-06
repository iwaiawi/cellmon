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

import android.content.Intent
import android.net.Uri
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import iwai.cellmon.R
import iwai.cellmon.ui.common.PlaceHolderLayout
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Ui}

trait Layout
  extends Styles
    with PlaceHolderLayout {

  self: Fragment =>

  var description = slot[TextView]

  var mainContent = slot[LinearLayout]

  var placeholderContent = slot[LinearLayout]

  var aboutContent = slot[LinearLayout]

  def layout(implicit context: ActivityContextWrapper) = /*getUi(*/
    l[FrameLayout](
      l[LinearLayout](
        l[ScrollView](
          l[LinearLayout](
            w[TextView] <~ titleStyle,
            w[TextView] <~ wire(description) <~ descriptionStyle
          ) <~ contentStyle
        ) <~ scrollStyle,
        l[LinearLayout](
          w[ImageView] <~ about47ImageStyle,
          w[TextView] <~ about47TextStyle
        ) <~ about47ContentStyle <~ wire(aboutContent) <~ On.click {
          Ui {
            startActivity(new Intent(Intent.ACTION_VIEW,
              Uri.parse(resGetString(R.string.url_47deg))))
          }
        }
      ) <~ wire(mainContent) <~ rootStyle,
      placeholder <~ wire(placeholderContent)
    )

  /*  )*/

}


class CellChangeItemSlots {
  var content = slot[LinearLayout]
  var changeAt = slot[TextView]
  var cell = slot[TextView]
}

class CellChangeItemLayout(implicit context: ActivityContextWrapper)
extends ItemStyles {

  val slots = new CellChangeItemSlots

  //  val content = layout

  val layout = l[LinearLayout](
    w[TextView] <~ wire(slots.changeAt) <~ changeAtStyle,
    w[TextView] <~ wire(slots.cell) <~ cellStyle
  ) <~ wire(slots.content) <~ contentStyle

  //  def layout(implicit context: ActivityContextWrapper) = /*getUi(*/
  //    l[LinearLayout](
  //      w[TextView] <~ wire(changeAt) <~ changeAtStyle,
  //      w[TextView] <~ wire(cell) <~ cellStyle
  //    ) <~ wire(content) <~ itemContentStyle
  ///*  )*/
}

class CellChangeViewHolder(itemLayout: CellChangeItemLayout)(implicit context: ActivityContextWrapper)
  extends RecyclerView.ViewHolder(getUi(itemLayout.layout))
    with RecyclerClickableViewHolder {

  // shortcut to slot
  val slots = itemLayout.slots
  override val clickableSlot = slots.content
}