@file:Suppress("MemberVisibilityCanBePrivate")

package tk.mallumo.compose.navigation

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class NavigationActivity : AppCompatActivity() {

    val navigationViewModel by lazy {
        val vmProvider = ViewModelProvider(this)
        (vmProvider[ImplNavigationViewModel::class.java]).apply {

            init(startupNode(), startupArgs())

            viewModelScope.launch(Dispatchers.IO) {
                viewModelsToRelease.collect {
                    vmProvider.get(it, EmptyViewModel::class.java)
                }
            }

            viewModelScope.launch(Dispatchers.Main) {
                permissionFlow.collect { systemPermission ->
                    val arePermissionsGranted =
                        systemPermission.permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }

                    if (arePermissionsGranted) {
                        consumePermission(systemPermission.requestCode, true)
                    } else {
                        requestPermissions(
                            systemPermission.permissions,
                            systemPermission.requestCode
                        )
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (!navigationViewModel.consumePermission(
                requestCode = requestCode,
                granted = grantResults.all { it == PackageManager.PERMISSION_GRANTED })
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onBackPressed() {
        if (!navigationViewModel.back()) super.onBackPressed()
    }

    abstract fun startupNode(): Node

    open fun startupArgs(): Bundle = intent.extras ?: Bundle()
}