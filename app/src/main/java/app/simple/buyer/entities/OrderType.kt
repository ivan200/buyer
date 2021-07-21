package app.simple.buyer.entities

/**
 * Created by Zakharovi on 31.01.2018.
 */
enum class OrderType(val value: Int) {
    /** Ручная сортировка **/
    NOT_SET(-1),

    /** Ручная сортировка **/
    HAND(0),

    /** Сортировка по алфавиту **/
    ALPHABET(1),

    /** Сортировка по популярности **/
    POPULARITY(2),

    /** Сортировка по размеру **/
    SIZE(3),

    /** Сортировка по дате создания **/
    CREATED(4),

    /** Сортировка по дате изменения **/
    MODIFIED(5),

    /** Сортировка по суммарной цене **/
    PRICE(6),

    /** Сортировка по категории **/
    CATEGORY(7);
}