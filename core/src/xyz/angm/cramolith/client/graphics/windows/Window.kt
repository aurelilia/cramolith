/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/12/21, 2:34 AM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.graphics.windows

import com.kotcrab.vis.ui.widget.VisWindow
import xyz.angm.cramolith.client.resources.I18N

abstract class Window(ident: String) : VisWindow(I18N["window.$ident"])
