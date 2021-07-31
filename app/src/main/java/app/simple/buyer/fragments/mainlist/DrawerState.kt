package app.simple.buyer.fragments.mainlist

import androidx.annotation.IntDef
import androidx.drawerlayout.widget.DrawerLayout

/**
 * Текущее состояние дравера
 *
 * @author ivan200
 * @since 22.07.2021
 */
enum class DrawerState {
    START_OPENING,
    FINISH_OPENING,
    START_CLOSING,
    FINISH_CLOSING;

    companion object{
        @IntDef(DrawerLayout.STATE_IDLE, DrawerLayout.STATE_DRAGGING, DrawerLayout.STATE_SETTLING)
        annotation class DrawerLayoutState

        fun getDrawerPos(@DrawerLayoutState newState: Int, drawerOpen: Boolean): DrawerState = when {
            !drawerOpen && newState == DrawerLayout.STATE_SETTLING -> START_OPENING
            !drawerOpen && newState == DrawerLayout.STATE_DRAGGING -> START_OPENING
            !drawerOpen && newState == DrawerLayout.STATE_IDLE -> FINISH_CLOSING
            drawerOpen && newState == DrawerLayout.STATE_SETTLING -> START_CLOSING
            drawerOpen && newState == DrawerLayout.STATE_DRAGGING -> START_CLOSING
            drawerOpen && newState == DrawerLayout.STATE_IDLE -> FINISH_OPENING
            else -> START_OPENING
        }
    }
}