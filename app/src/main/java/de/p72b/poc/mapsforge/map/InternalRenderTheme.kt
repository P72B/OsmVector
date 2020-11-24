package de.p72b.poc.mapsforge.map

import de.p72b.poc.mapsforge.App
import org.mapsforge.map.rendertheme.XmlRenderTheme
import org.mapsforge.map.rendertheme.XmlRenderThemeMenuCallback
import java.io.InputStream


enum class InternalRenderTheme(private val path: String) : XmlRenderTheme {
    CUSTOM("mapsforge/custom.xml");

    override fun getMenuCallback(): XmlRenderThemeMenuCallback? {
        return null
    }

    /**
     * @return the prefix for all relative resource paths.
     */
    override fun getRelativePathPrefix(): String {
        return "/assets/"
    }

    override fun getRenderThemeAsStream(): InputStream {
        return App.appContext.assets.open(CUSTOM.path)
    }

    override fun setMenuCallback(menuCallback: XmlRenderThemeMenuCallback) {}
}