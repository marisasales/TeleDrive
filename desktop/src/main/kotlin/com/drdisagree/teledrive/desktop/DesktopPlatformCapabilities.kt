package com.drdisagree.teledrive.desktop

import com.drdisagree.teledrive.presentation.platform.PlatformCapabilities

class DesktopPlatformCapabilities : PlatformCapabilities {

    private val uiPreferences = java.util.prefs.Preferences.userRoot()
        .node("com/drdisagree/teledrive/desktop-ui")

    override val supportsAutoBackup: Boolean = false

    override val requiresPermissions: Boolean = false

    override val supportsPullToRefresh: Boolean = false

    override val supportsAppLock: Boolean = false

    override val supportsScreenCaptureBlocking: Boolean = false

    override val supportsDynamicColor: Boolean = false

    override val isDesktop: Boolean = true

    override fun loadNavigationSidebarCollapsed(): Boolean =
        uiPreferences.getBoolean("navigation-sidebar-collapsed", false)

    override fun saveNavigationSidebarCollapsed(collapsed: Boolean) {
        uiPreferences.putBoolean("navigation-sidebar-collapsed", collapsed)
    }
}
