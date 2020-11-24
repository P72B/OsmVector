package de.p72b.poc.mapsforge.map

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import de.p72b.poc.mapsforge.R
import de.p72b.poc.mapsforge.databinding.ActivityMainBinding
import de.p72b.poc.mapsforge.map.MainViewModel.ViewAction.OpenMap
import de.p72b.poc.mapsforge.map.MainViewModel.ViewAction.ShowMessage
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.datastore.MapDataStore
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import java.io.FileInputStream

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var mapView: MapView

    private val viewModel: MainViewModel by lazy {
        val factory = MainViewModelFactory()
        ViewModelProvider(this@MainActivity, factory).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel

        viewModel.viewAction.observe(this, {
            when (it) {
                is ShowMessage -> showSnackBar(getString(it.text))
                is OpenMap -> openMap(it.uri)
            }
        })


        AndroidGraphicFactory.createInstance(application)
        mapView = findViewById<View>(R.id.mapView) as MapView
    }

    override fun onDestroy() {
        mapView.destroyAll()
        AndroidGraphicFactory.clearResourceMemoryCache()
        super.onDestroy()
    }

    private fun openMap(uri: Uri) {
        try {
            mapView.mapScaleBar.isVisible = true
            mapView.setBuiltInZoomControls(true)
            val tileCache = AndroidUtil.createTileCache(
                this, "mapcache",
                mapView.model.displayModel.tileSize, 1f,
                mapView.model.frameBufferModel.overdrawFactor
            )
            val fis = contentResolver.openInputStream(uri) as FileInputStream?
            val mapDataStore: MapDataStore = MapFile(fis, 0L, null)
            val tileRendererLayer = TileRendererLayer(
                tileCache, mapDataStore,
                mapView.model.mapViewPosition, AndroidGraphicFactory.INSTANCE
            )
            tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.CUSTOM)

            mapView.layerManager.layers.add(tileRendererLayer)

            mapView.setCenter(LatLong(52.52629378878745, 13.41604471206665))
            mapView.setZoomLevel(12.toByte())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showSnackBar(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG)
            .show()
    }
}