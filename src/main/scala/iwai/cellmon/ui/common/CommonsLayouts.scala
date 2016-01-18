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

package iwai.cellmon.ui.common

import android.support.v7.widget.RecyclerView
import android.widget.{LinearLayout, TextView}
import macroid.ActivityContextWrapper
import macroid.FullDsl._

class HeaderLayoutAdapter(implicit context: ActivityContextWrapper)
    extends HeaderAdapterStyles {

  var headerName = slot[TextView]

  val content = layout

  private def layout(implicit context: ActivityContextWrapper) = getUi(
    l[LinearLayout](
      w[TextView] <~ wire(headerName) <~ headerNameStyle
    ) <~ headerContentStyle
  )
}

class ViewHolderHeaderAdapter(adapter: HeaderLayoutAdapter)(implicit context: ActivityContextWrapper)
    extends RecyclerView.ViewHolder(adapter.content) {

  val content = adapter.content

  val headerName = adapter.headerName

}