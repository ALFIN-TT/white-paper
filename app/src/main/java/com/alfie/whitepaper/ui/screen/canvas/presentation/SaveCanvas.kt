package com.alfie.whitepaper.ui.screen.canvas.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.alfie.whitepaper.R
import com.alfie.whitepaper.data.constants.EXPORT_PROJECT
import com.alfie.whitepaper.data.constants.SAVE_AS_JPEG
import com.alfie.whitepaper.data.constants.SAVE_AS_PNG
import com.alfie.whitepaper.data.constants.SAVE_AS_PROJECT
import com.alfie.whitepaper.data.constants.SHARE
import com.alfie.whitepaper.data.constants.SHARE_PROJECT
import com.alfie.whitepaper.data.model.SaveOption
import com.alfie.whitepaper.ui.common.addmob.AdmobBanner
import com.alfie.whitepaper.ui.theme.montserratFamily


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawSaveDrawingBottomSheet(
    onSave: (SaveOption) -> Unit, onDismiss: () -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState(/*skipPartiallyExpanded = true*/)

    ModalBottomSheet(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        DrawSaveOptions(onSave)
    }
}


@Composable
fun DrawSaveOptions(
    onSave: (SaveOption) -> Unit,
) {
    val saveOptions = listOf(
        SaveOption(
            SHARE,
            R.drawable.ic_share,
            R.string.str_share,
            R.string.str_you_can_share_your_drawing_directly
        ),
        SaveOption(
            SAVE_AS_JPEG,
            R.drawable.ic_jpg,
            R.string.str_save_as_jpeg,
            R.string.str_saving_image_as_a_jpeg_file
        ),
        SaveOption(
            SAVE_AS_PNG,
            R.drawable.ic_png,
            R.string.str_save_as_png,
            R.string.str_saving_image_as_a_png_file
        ),
        SaveOption(
            SAVE_AS_PROJECT,
            R.drawable.ic_project,
            R.string.str_save_as_project,
            R.string.str_saving_image_as_a_project_file_you_can_rework_it_later
        ),
        SaveOption(
            EXPORT_PROJECT,
            R.drawable.ic_file,
            R.string.str_export_project,
            R.string.str_exporting_your_drawing_as_file_import_later
        ),
        SaveOption(
            SHARE_PROJECT,
            R.drawable.ic_share_file,
            R.string.str_share_project,
            R.string.str_sharing_your_drawing_as_file_draw_later
        )
    )

    LazyColumn {
        items(saveOptions) { saveOption ->
            DrawSaveOptionCard(saveOption, onSave)
        }
        item {
            AdmobBanner(modifier = Modifier.fillMaxWidth())
        }
        item {
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(42.dp)
            )
        }
    }
}

@Composable
fun DrawSaveOptionCard(
    saveOption: SaveOption,
    onSave: (SaveOption) -> Unit,
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(
                start = 16.dp,
                top = 5.dp,
                end = 16.dp,
                bottom = 12.dp
            )
            .clickable {
                onSave(saveOption)
            },
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            val (icon, title, description) = createRefs()
            Icon(modifier = Modifier
                .constrainAs(icon) {
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                    top.linkTo(parent.top)
                    width = Dimension.fillToConstraints
                }
                .size(24.dp),
                painter = painterResource(id = saveOption.iconRes),
                contentDescription = null)
            Text(modifier = Modifier
                .constrainAs(title) {
                    start.linkTo(icon.end, 10.dp)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    width = Dimension.fillToConstraints
                }
                .padding(end = 20.dp),
                text = stringResource(id = saveOption.name),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = montserratFamily,
                textAlign = TextAlign.Start)
            Text(
                modifier = Modifier.constrainAs(description) {
                    start.linkTo(icon.end, 10.dp)
                    end.linkTo(parent.end)
                    top.linkTo(title.bottom)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                },
                text = stringResource(id = saveOption.description),
                textAlign = TextAlign.Start,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = montserratFamily,
                style = TextStyle()
            )
        }
    }
}
