package app.simple.buyer.entities.enums

/**
 * Тип сортировки
 *
 * @author ivan200
 * @since 22.07.2021
 */
enum class SortType(val value: Boolean){
    /** Сортировка по возрастанию **/
    ASCENDING(true),

    /** Сортировка по убыванию **/
    DESCENDING(false);

    companion object {
        fun getByValue(value: Boolean): SortType {
            return if (value) ASCENDING else DESCENDING
        }
    }
}