/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/18/21, 3:57 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.resources

import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.ObjectMap
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import ktx.assets.toLocalFile
import ktx.collections.*
import xyz.angm.cramolith.common.yaml
import java.util.*

/** The main object used to retrieve locale/language-specific text to be used by the game.
 * The name is a shorthand of 'internationalization'.
 *
 * Retrieving a string is as simple as:
 * I18N['identifier']
 *
 * To add a new string, take a look at the 'i18n' directory in core/assets.
 * Languages are loaded dynamically! It is possible to add new localizations into
 * the same directory as the game executable and have them be loaded.
 */
object I18N {

    private val bundles = ObjectMap<String, I18NBundle>()
    private var bundle: I18NBundle

    init {
        val info = yaml.decodeFromString(
            MapSerializer(String.serializer(), String.serializer()), "i18n/locales.yaml"
                .toLocalFile().readString()
        )
        val fileHandle = "i18n/locale".toLocalFile()
        for (bundleInfo in info) {
            val locale = Locale(bundleInfo.value)
            val bundle = I18NBundle.createBundle(fileHandle, locale)
            bundles[bundleInfo.key] = bundle
        }
        bundle = bundles[configuration.language]
    }

    operator fun get(name: String) = bundle[name]!!

    fun fmt(name: String, arg: String) = bundle.format(name, arg)!!
    fun fmt(name: String, arg1: String, arg2: String) = bundle.format(name, arg1, arg2)!!

    fun tryGet(name: String): String? {
        return try {
            this[name]
        } catch (e: Exception) {
            null
        }
    }

    /** Returns all languages available. */
    fun languages() = bundles.keys().toArray()!!

    /** Sets and saves the current language. */
    fun setLanguage(lang: String) {
        configuration.language = lang
        configuration.save()
        bundle = bundles[lang]
    }
}