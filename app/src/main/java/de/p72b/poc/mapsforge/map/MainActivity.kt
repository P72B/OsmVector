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
import org.oscim.android.MapView
import org.oscim.backend.CanvasAdapter
import org.oscim.layers.tile.buildings.BuildingLayer
import org.oscim.layers.tile.vector.VectorTileLayer
import org.oscim.layers.tile.vector.labeling.LabelLayer
import org.oscim.renderer.GLViewport
import org.oscim.scalebar.DefaultMapScaleBar
import org.oscim.scalebar.MapScaleBar
import org.oscim.scalebar.MapScaleBarLayer
import org.oscim.tiling.source.mapfile.MapFileTileSource

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
        mapView = findViewById<View>(R.id.mapView) as MapView
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    private fun openMap(uri: Uri) {
        try {
            // Tile source
            val tileSource = MapFileTileSource()
            tileSource.setMapFile(uri.path)

            // Vector layer
            val tileLayer: VectorTileLayer = mapView.map().setBaseMap(tileSource)

            // Building layer
            mapView.map().layers().add(BuildingLayer(mapView.map(), tileLayer))

            // Label layer
            mapView.map().layers().add(LabelLayer(mapView.map(), tileLayer))

            // Render theme
            mapView.map().setTheme(VtmThemes.DEFAULT)

            // Scale bar
            val mapScaleBar: MapScaleBar = DefaultMapScaleBar(mapView.map())
            val mapScaleBarLayer = MapScaleBarLayer(mapView.map(), mapScaleBar)
            mapScaleBarLayer.renderer.setPosition(GLViewport.Position.BOTTOM_LEFT)
            mapScaleBarLayer.renderer.setOffset(5 * CanvasAdapter.getScale(), 0f)
            mapView.map().layers().add(mapScaleBarLayer)

            // Note: this map position is specific to Berlin area
            mapView.map().setMapPosition(52.52629378878745, 13.41604471206665, 12.0)
        } catch (e: java.lang.Exception) {
            /*
             * In case of map file errors avoid crash, but developers should handle these cases!
             */
            e.printStackTrace()
        }
    }

    private fun showSnackBar(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG)
            .show()
    }
}