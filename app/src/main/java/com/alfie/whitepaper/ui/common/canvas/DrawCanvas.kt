package com.alfie.whitepaper.ui.common.canvas

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.viewinterop.AndroidView
import java.io.ByteArrayOutputStream

@Composable
fun DrawCanvas(
    drawController: DrawController,
    modifier: Modifier = Modifier.fillMaxSize(),
    backgroundColor: Color = Color.White,
    bitmapCallback: (ImageBitmap?, Throwable?) -> Unit = { _, _ -> },
    trackHistory: (undoCount: Int, redoCount: Int) -> Unit = { _, _ -> }
) = AndroidView(

    factory = {
        ComposeView(it).apply {
            setContent {
                LaunchedEffect(drawController) {
                    drawController.canvasView = this@apply
                    drawController.changeBgColor(backgroundColor)
                    drawController.trackBitmaps(this@apply, this, bitmapCallback)
                    drawController.trackHistory(this, trackHistory)
                }
                Canvas(modifier = modifier
                    .background(drawController.bgColor)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { offset ->
                                //   println("TAP!")
                                drawController.insertNewPath(offset)
                                drawController.updateLatestPath(offset)
                                drawController.pathList
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                drawController.insertNewPath(offset)
                                // println("DRAG!")
                            }
                        ) { change, _ ->
                            val newPoint = change.position
                            drawController.updateLatestPath(newPoint)
                        }

                    }) {
                    drawController.pathList.forEach { pw ->
                        drawPath(
                            createPath(pw.points),
                            color = pw.strokeColor,
                            alpha = pw.alpha,
                            style = Stroke(
                                width = pw.strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }
        }
    },
    modifier = modifier
)

@Composable
fun DrawCanvas(
    modifier: Modifier,
    drawCanvasPayLoad: DrawCanvasPayLoad
) {
    Canvas(
        modifier = Modifier
            .background(drawCanvasPayLoad.bgColor)
    ) {
        drawCanvasPayLoad.path.forEach { pw ->
            drawPath(
                createPath(pw.points),
                color = pw.strokeColor,
                alpha = pw.alpha,
                style = Stroke(
                    width = pw.strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }

    /* val configuration = LocalConfiguration.current

     Image(
         bitmap = drawToBitmap(
             modifier,
             Size(
                 configuration.screenWidthDp.toFloat(),
                 configuration.screenHeightDp.toFloat()
             ),
             drawCanvasPayLoad
         ), contentScale = ContentScale.FillBounds, contentDescription = null
     )*/

    val configuration = LocalConfiguration.current

    imageBitmapToBase64(
        drawToBitmap(
            modifier,
            Size(
                configuration.screenWidthDp.toFloat(),
                configuration.screenHeightDp.toFloat()
            ),
            drawCanvasPayLoad
        )
    )
}

fun drawToBitmap(
    modifier: Modifier,
    size: Size,
    drawCanvasPayLoad: DrawCanvasPayLoad
): ImageBitmap {
    val drawScope = CanvasDrawScope()
    //  val size = Size(400f, 400f) // simple example of 400px by 400px image
    val bitmap = ImageBitmap(size.width.toInt(), size.height.toInt())
    val canvas = Canvas(bitmap)

    drawScope.draw(
        density = Density(1f),
        layoutDirection = LayoutDirection.Ltr,
        canvas = canvas,
        size = size,
    ) {
        // Draw whatever you want here; for instance, a white background and a red line.
        /* drawRect(color = Color.White, topLeft = Offset.Zero, size = size)
         drawLine(
             color = Color.Red,
             start = Offset.Zero,
             end = Offset(size.width, size.height),
             strokeWidth = 5f
         )*/

        drawCanvasPayLoad.path.forEach { pw ->
            drawPath(
                createPath(pw.points),
                color = pw.strokeColor,
                alpha = pw.alpha,
                style = Stroke(
                    width = pw.strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

    }
    return bitmap
}


fun imageBitmapToBase64(bitmap: ImageBitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val byteArray = outputStream.toByteArray()
    val string = Base64.encodeToString(byteArray, Base64.DEFAULT)
    Log.e("TAG", "imageBitmapToBase64: $string")
    return string
}