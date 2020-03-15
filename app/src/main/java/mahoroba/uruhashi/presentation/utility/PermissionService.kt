package mahoroba.uruhashi.presentation.utility

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast

object PermissionService {

    fun requestPermissionIfNotGranted(
        activity: Activity, permission: String, requestCode: Int
    ): Boolean {
        if (Build.VERSION.SDK_INT < 23) return false

        if (ContextCompat.checkSelfPermission(activity, permission) ==
            PackageManager.PERMISSION_GRANTED
        ) return false

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
        } else {
            Toast.makeText(activity, "Your permission is needed to export.", Toast.LENGTH_SHORT)
                .show()
            ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
        }

        return true
    }
}