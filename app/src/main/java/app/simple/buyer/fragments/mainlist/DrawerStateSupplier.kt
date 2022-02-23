package app.simple.buyer.fragments.mainlist

/**
 * Интерфейс для обработки изменения позиции правого дравера
 *
 * @author ivan200
 * @since 23.07.2021
 */
interface DrawerStateSupplier {
    fun isDrawerOpen(drawerGravity: Int): Boolean
}