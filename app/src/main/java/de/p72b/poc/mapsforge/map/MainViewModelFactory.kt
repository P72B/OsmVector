package de.p72b.poc.mapsforge.map

import android.content.Context
import android.os.storage.StorageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.p72b.poc.mapsforge.App
import de.p72b.poc.mapsforge.util.StorageManagerHelper

class MainViewModelFactory : ViewModelProvider.Factory {

    private val storageManagerHelper: StorageManagerHelper by lazy {
        StorageManagerHelper(App.appContext.getSystemService(Context.STORAGE_SERVICE) as StorageManager)
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(StorageManagerHelper::class.java).newInstance(storageManagerHelper)
    }
}