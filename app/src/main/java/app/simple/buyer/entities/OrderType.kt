package app.simple.buyer.entities

/**
 * Тип сортировки
 *
 * @author ivan200
 * @since 31.01.2018
 */
enum class OrderType(val value: Int) {
    /** Сортировка по дате создания **/
    CREATED(0),

    /** Сортировка по дате изменения **/
    MODIFIED(1),

    /** Сортировка по алфавиту **/
    ALPHABET(2),

    /** Сортировка по популярности **/
    POPULARITY(3),

    /** Сортировка по размеру **/
    SIZE(4),

    /** Сортировка по суммарной цене **/
    PRICE(5),

    /** Ручная сортировка **/
    HAND(6);

    //TODO возможно прикрутить сортировку по категории
//    /** Сортировка по категории **/
//    CATEGORY(7);

    companion object {
        fun getByValue(value: Int): OrderType {
            return values().first { it.value == value }
        }
    }
}

