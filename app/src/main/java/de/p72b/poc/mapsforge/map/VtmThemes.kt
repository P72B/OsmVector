package de.p72b.poc.mapsforge.map

import org.oscim.backend.AssetAdapter
import org.oscim.theme.IRenderTheme.ThemeException
import org.oscim.theme.ThemeFile
import org.oscim.theme.XmlRenderThemeMenuCallback
import java.io.InputStream


enum class VtmThemes(private val mPath: String) : ThemeFile {
    CUSTOM("mapsforge/custom.xml"),
    DEFAULT("mapsforge/default.xml"),
    NEWTRON("mapsforge/newtron.xml"),
    OSMAGRAY("mapsforge/osmagray.xml"),
    OSMARENDER("mapsforge/osmarender.xml"),
    TRONRENDER("mapsforge/tronrender.xml");

    override fun getMenuCallback(): XmlRenderThemeMenuCallback? {
        return null
    }

    override fun getRelativePathPrefix(): String {
        return ""
    }

    @Throws(ThemeException::class)
    override fun getRenderThemeAsStream(): InputStream {
        return AssetAdapter.readFileAsStream(mPath)
    }

    override fun isMapsforgeTheme(): Boolean {
        return false
    }

    override fun setMenuCallback(menuCallback: XmlRenderThemeMenuCallback) {}
}