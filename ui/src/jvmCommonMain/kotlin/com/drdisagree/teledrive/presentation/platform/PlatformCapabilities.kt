package com.drdisagree.teledrive.presentation.platform

import androidx.compose.runtime.staticCompositionLocalOf

interface PlatformCapabilities {

    val supportsAutoBackup: Boolean

    val requiresPermissions: Boolean

    val supportsPullToRefresh: Boolean

    val supportsAppLock: Boolean

    val supportsScreenCaptureBlocking: Boolean

    val supportsDynamicColor: Boolean

    /** Enables desktop-specific navigation and visual treatment without changing Android. */
    val isDesktop: Boolean

    fun loadNavigationSidebarCollapsed(): Boolean = false

    fun saveNavigationSidebarCollapsed(collapsed: Boolean) = Unit
}

val LocalPlatformCapabilities = staticCompositionLocalOf<PlatformCapabilities> {
    error("PlatformCapabilities is not provided")
}
