package com.example.ninepiecepuzzle

import android.annotation.SuppressLint
import android.content.ClipData
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.DragShadowBuilder
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.abs


class MyDragShadowBuilder(v: View) : View.DragShadowBuilder(v) {

    private val shadow = ColorDrawable(Color.LTGRAY)

    // Defines a callback that sends the drag shadow dimensions and touch point back to the
    // system.
    override fun onProvideShadowMetrics(size: Point, touch: Point) {
        // Sets the width of the shadow to half the width of the original View
        val width: Int = view.width / 2

        // Sets the height of the shadow to half the height of the original View
        val height: Int = view.height / 2

        // The drag shadow is a ColorDrawable. This sets its dimensions to be the same as the
        // Canvas that the system will provide. As a result, the drag shadow will fill the
        // Canvas.
        shadow.setBounds(0, 0, width, height)

        // Sets the size parameter's width and height values. These get back to the system
        // through the size parameter.
        size.set(width, height)

        // Sets the touch point's position to be in the middle of the drag shadow
        touch.set(width / 2, height / 2)
    }

    // Defines a callback that draws the drag shadow in a Canvas that the system constructs
    // from the dimensions passed in onProvideShadowMetrics().
    override fun onDrawShadow(canvas: Canvas) {
        // Draws the ColorDrawable in the Canvas passed in from the system.
        shadow.draw(canvas)
    }
}

class MainActivity : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewIds = listOf(
            R.id.imageView1,
            R.id.imageView2,
            R.id.imageView3,
            R.id.imageView4,
            R.id.imageView5,
        )
        // Initialize our views based on the previous array of ids
        val views = ArrayList<ImageView>() // { i -> findViewById(viewIds[i]) })
        for (id in viewIds) {
            views.add(findViewById(id))
        }
        views.add(findViewById(R.id.emptyView))
        val verticalLength = 2

        val dragListen = View.OnDragListener { v, event ->

            val old = event.localState as ImageView
            val target = v as ImageView
            when (event.action) {
                DragEvent.ACTION_DRAG_ENTERED -> {
                    true
                }
                DragEvent.ACTION_DROP -> {
                    val oldPosition = views.indexOf(old)
                    val oldX = oldPosition % verticalLength
                    val oldY = oldPosition / verticalLength

                    val newPosition = views.indexOf(target)
                    val newX = newPosition % verticalLength
                    val newY = newPosition / verticalLength
                    // The user may only move a piece once in either direction
                    // adding the difference together makes it so that if they move
                    // 1 in x or 1 in y, they will move in total 2 times, that is disallowed
                    if (abs(oldX - newX) + abs(oldY - newY) == 1) {
                        if (findViewById<ImageView>(R.id.emptyView) == target) {
                            views[oldPosition] = target
                            views[newPosition] = old
                            val p = old.parent as LinearLayout
                            val d = target.parent as LinearLayout
                            p.removeView(old)
                            d.removeView(target)
                            p.addView(target)
                            d.addView(old)
                        }
                    }
                    true
                }
                else -> {
                    // Ignore all other types of events
                    true
                }
            }
        }
        findViewById<ImageView>(R.id.emptyView).setOnDragListener(dragListen)
        val t = views[0]
        for (id in viewIds) {
            val view = findViewById<ImageView>(id)
            view.setOnDragListener(dragListen)
            view.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        val shadowBuilder = DragShadowBuilder(v)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            v.startDragAndDrop(ClipData.newPlainText("",""), shadowBuilder, v, 0)
                        } else {
                            v.startDrag(ClipData.newPlainText("",""), shadowBuilder, v, 0)
                        }
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        v.visibility = View.VISIBLE
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        }
//        t.setOnDragListener(dragListen)
//        t.setOnTouchListener { v, event ->
//            if (event.action == MotionEvent.ACTION_DOWN) {
//                val shadowBuilder = DragShadowBuilder(v)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    v.startDragAndDrop(ClipData.newPlainText("",""), shadowBuilder, v, 0)
//                } else {
//                    v.startDrag(ClipData.newPlainText("",""), shadowBuilder, v, 0)
//                }
//                true
//            } else if (event.action == MotionEvent.ACTION_UP) {
//                v.visibility = View.VISIBLE
//                true
//            } else {
//                false
//            }
//        }
    }
}