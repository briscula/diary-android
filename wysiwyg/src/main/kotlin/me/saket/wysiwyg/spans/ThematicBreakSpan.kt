package me.saket.wysiwyg.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.LineBackgroundSpan
import me.saket.wysiwyg.style.WysiwygStyle

/**
 * @param syntax Used for calculating the left offset to avoid drawing under the text.
 */
actual class ThematicBreakSpan actual constructor(
  private val style: WysiwygStyle,
  private val recycler: Recycler,
  actual val syntax: CharSequence
) : LineBackgroundSpan, WysiwygSpan {

  private var offsetForSyntax = -1f

  /** Used for centering the rule with the text. */
  private val topOffsetFactor: Float = when (syntax[0]) {
    '*' -> -0.3f
    '-' -> -0.05f
    '_' -> 0.35f
    else -> throw UnsupportedOperationException("Unknown thematic break mode: $syntax")
  }

  override fun drawBackground(
    canvas: Canvas,
    paint: Paint,
    left: Int,
    right: Int,
    top: Int,
    baseline: Int,
    bottom: Int,
    ignored: CharSequence,
    start: Int,
    end: Int,
    lineNumber: Int
  ) {
    val originalPaintColor = paint.color
    paint.color = style.thematicBreak.color
    paint.strokeWidth = style.thematicBreak.height

    if (offsetForSyntax == -1f) {
      offsetForSyntax = paint.measureText(syntax.toString())
    }

    val lineCenter = ((top + bottom) / 2 + paint.textSize * topOffsetFactor).toInt()
    canvas.drawLine(
        left + offsetForSyntax,
        lineCenter.toFloat(),
        right.toFloat(),
        lineCenter.toFloat(),
        paint
    )

    paint.color = originalPaintColor
  }

  override fun recycle() {
    recycler(this)
  }
}
