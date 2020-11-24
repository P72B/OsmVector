package de.p72b.poc.mapsforge.map

import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import de.p72b.poc.mapsforge.App
import de.p72b.poc.mapsforge.BaseViewModel
import de.p72b.poc.mapsforge.R
import de.p72b.poc.mapsforge.util.StorageManagerHelper
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.net.URL


class MainViewModel(private val storageManagerHelper: StorageManagerHelper): BaseViewModel() {

    val viewAction = MutableLiveData<ViewAction>()
    val progress = MutableLiveData<Boolean>().apply { postValue(false) }

    private val fileDir: File = App.appContext.filesDir
    private val fileName: String = "berlin"
    private val fileNameExtension: String = "map"
    private val baseUrl: String = "https://p72b.de/mapsforge/"
    private val url: URL = URL("$baseUrl$fileName.$fileNameExtension")
    private val mapFile = File(fileDir, "$fileName.$fileNameExtension")

    fun onDownloadClicked() {
        if (storageManagerHelper.hasSpaceOnDisk(fileDir, NUM_BYTES_NEEDED)) {
            startDownload()
        } else {
            viewAction.postValue(ViewAction.ShowMessage(MessageType.ERROR, R.string.error_not_enough_space))
        }
    }

    fun onOpenFileClicked() {
        viewAction.postValue(
            ViewAction.OpenMap(Uri.fromFile(mapFile))
        )
    }

    private fun startDownload() {
        progress.postValue(true)

        val result: Deferred<ByteArray?> = GlobalScope.async {
            url.readBytes()
        }

        GlobalScope.launch(Dispatchers.IO) {
            val bytes : ByteArray? = result.await()
            bytes?.apply {
                storageManagerHelper.saveToInternalStorage(
                    mapFile,
                    this
                )?.let {
                    viewAction.postValue(
                        ViewAction.OpenMap(Uri.fromFile(it))
                    )
                }
            }
            progress.postValue(false)
        }
    }

    companion object {
        // Sample 10 MB within internal storage
        const val NUM_BYTES_NEEDED = 1024 * 1024 * 10L
    }

    sealed class ViewAction {
        data class ShowMessage(val type: MessageType, @StringRes val text: Int) : ViewAction()
        data class OpenMap(val uri: Uri) : ViewAction()
    }

    enum class MessageType{
        ERROR,
        INFO
    }
}
