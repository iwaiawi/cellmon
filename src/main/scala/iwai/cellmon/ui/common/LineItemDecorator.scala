package iwai.cellmon.ui.common

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.State
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import macroid.ContextWrapper
import iwai.cellmon.R

import scala.language.postfixOps

class LineItemDecorator(implicit context: ContextWrapper)
  extends RecyclerView.ItemDecoration {

  val divider = new ColorDrawable(context.application.getResources.getColor(R.color.list_line_default))

  override def onDrawOver(c: Canvas, parent: RecyclerView, state: State): Unit = {
    val left = parent.getPaddingLeft
    val right = parent.getWidth - parent.getPaddingRight

    val childCount = parent.getChildCount
    (0 until childCount) foreach { i =>
      val child = parent.getChildAt(i)
      val params = child.getLayoutParams.asInstanceOf[RecyclerView.LayoutParams]
      val top = child.getBottom + params.bottomMargin
      val bottom = top + 1
      divider.setBounds(left, top, right, bottom)
      divider.draw(c)
    }
  }
}
