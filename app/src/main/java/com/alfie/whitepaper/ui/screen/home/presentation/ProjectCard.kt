package com.alfie.whitepaper.ui.screen.home.presentation

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.alfie.whitepaper.R
import com.alfie.whitepaper.ui.common.DrawRectangleShimmer
import com.alfie.whitepaper.ui.common.canvas.DrawCanvas
import com.alfie.whitepaper.ui.common.canvas.DrawCanvasPayLoad
import com.alfie.whitepaper.ui.theme.montserratFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProjectCard(
    @StringRes label: Int,
    @DrawableRes icon: Int,
    onclick: () -> Unit
) {
    val contentDesc = stringResource(id = label)
    ConstraintLayout(modifier = Modifier
        .semantics {
            contentDescription = contentDesc
            role = Role.Button
        }
        .fillMaxSize()
        .size(200.dp)
        .clickable {
            onclick.invoke()
        }) {
        val (newProjectIcon, newProjectText) = createRefs()
        Icon(modifier = Modifier
            .constrainAs(newProjectIcon) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                top.linkTo(parent.top)
            }
            .size(80.dp),
            painter = painterResource(icon),
            contentDescription = null)
        Text(modifier = Modifier
            .constrainAs(newProjectText) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                top.linkTo(newProjectIcon.bottom)
            }
            .padding(
                start = 12.dp,
                end = 12.dp,
                top = 4.dp,
                bottom = 12.dp
            )
            .basicMarquee(),
            text = stringResource(id = label),
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontFamily = montserratFamily)
    }
}

@Composable
fun ProjectCard(
    drawCanvasPayLoad: DrawCanvasPayLoad,
    onclick: () -> Unit,
    onLongPress: () -> Unit
) {
    val openProjectContentDesc = stringResource(id = R.string.msg_open_delete_project)
    Card(
        modifier = Modifier
            .semantics {
                contentDescription = openProjectContentDesc
                role = Role.Button
            }
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                onclick.invoke()
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onclick.invoke()
                    }, onLongPress = {
                        onLongPress.invoke()
                    }
                )
            },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    ) {

        var isLoadingCompleted by remember { mutableStateOf(true) }
        var bitmap by remember { mutableStateOf(ImageBitmap(1, 1)) }
        LaunchedEffect(drawCanvasPayLoad.thumbnail) {
            if (drawCanvasPayLoad.thumbnail?.isNotBlank() == true) {
                launch {
                    isLoadingCompleted = false
                    withContext(Dispatchers.IO) {
                        val byteArray = Base64.decode(drawCanvasPayLoad.thumbnail, Base64.DEFAULT)
                        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                            .asImageBitmap()
                    }
                }
                withContext(Dispatchers.Main) {
                    isLoadingCompleted = true
                }
            }
        }
        ProjectCard(
            drawCanvasPayLoad = drawCanvasPayLoad,
            bitmap = bitmap,
            isLoadingCompleted = isLoadingCompleted
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProjectCard(
    drawCanvasPayLoad: DrawCanvasPayLoad,
    bitmap: ImageBitmap,
    isLoadingCompleted: Boolean
) {
    val configuration = LocalConfiguration.current
    val size = (configuration.screenWidthDp / 2).dp
    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
    ) {
        val (projectName, canvas, background) = createRefs()
        if (drawCanvasPayLoad.thumbnail?.isNotBlank() == true) {
            if (isLoadingCompleted) {
                Image(
                    modifier = Modifier
                        .size(size)
                        .constrainAs(
                            canvas
                        ) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                            top.linkTo(parent.top)
                        },
                    bitmap = bitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            } else {
                DrawRectangleShimmer(isLoadingCompleted, size)
            }
        } else {
            DrawCanvas(drawCanvasPayLoad = drawCanvasPayLoad,
                modifier = Modifier
                    .size(size)
                    .constrainAs(
                        canvas
                    ) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        top.linkTo(parent.top)
                    }
            )
        }
        Box(modifier = Modifier
            .size(55.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent, MaterialTheme.colorScheme.primary
                    ),5f
                )
            )
            .constrainAs(background) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
            })
        Text(modifier = Modifier
            .constrainAs(projectName) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
            }
            .padding(12.dp)
            .basicMarquee(),
            text = drawCanvasPayLoad.name,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color(0xFFFFFFFF),
            textAlign = TextAlign.Center,
            fontFamily = montserratFamily,
            fontWeight = FontWeight.Bold
        )
    }
}


