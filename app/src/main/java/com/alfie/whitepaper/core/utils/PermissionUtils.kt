import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

internal val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
internal const val PERMISSION_CODE = 100
internal fun Activity.checkAndAskPermission(continueNext: () -> Unit) {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P && ContextCompat.checkSelfPermission(
            this, permissions[0]
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            this, permissions, PERMISSION_CODE
        )
        return
    }
    continueNext()
}