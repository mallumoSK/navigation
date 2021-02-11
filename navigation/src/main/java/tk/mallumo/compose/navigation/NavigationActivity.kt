@file:Suppress("MemberVisibilityCanBePrivate")

package tk.mallumo.compose.navigation

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
        }
    }

    override fun onBackPressed() {
        if (!navigationViewModel.back()) super.onBackPressed()
    }

    abstract fun startupNode(): Node

    open fun startupArgs(): Bundle = intent.extras ?: Bundle()
}