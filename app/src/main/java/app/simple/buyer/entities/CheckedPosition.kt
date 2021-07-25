package app.simple.buyer.entities

/**
 * Позиция прочеканых элементов относительно остальных
 *
 * @author ivan200
 * @since 31.01.2018
 */
enum class CheckedPosition(val value: Int) {
    /** Купленные продукты улетают вниз **/
    BOTTOM(0),

    /** Купленные продукты улетают вверх **/
    TOP(1),

    /** Купленные продукты просто отмечаются и остаются на месте **/
    BETWEEN(2),

    /** Купленные продукты пропадают из списка **/
    INVISIBLE(3);

    companion object {
        fun getByValue(value: Int): CheckedPosition {
            return values().first { it.value == value }
        }
    }
}