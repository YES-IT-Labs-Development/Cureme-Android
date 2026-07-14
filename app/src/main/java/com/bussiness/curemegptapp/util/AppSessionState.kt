package com.bussiness.curemegptapp.util

/**
 * AppSessionState - Singleton object to maintain in-memory application session state.
 * These variables persist as long as the app process is alive (not killed).
 */
object AppSessionState {
    var hasProfileDialogBeenShown: Boolean = false
}
