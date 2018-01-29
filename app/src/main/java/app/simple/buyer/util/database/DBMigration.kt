package app.simple.buyer.util.database

import io.realm.DynamicRealm
import io.realm.RealmMigration
import java.util.*

/**
 * Created by Zakharovi on 23.01.2018.
 */

class DBMigration : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        var oldVersion = oldVersion

        val schema = realm.schema
        if (oldVersion == 0L) {
            schema.get("BuyItem")?.removeField("count")
            schema.get("BuyListItem")?.removeField("price")
            oldVersion++
        }
        if (oldVersion == 1L) {
            schema.get("BuyList")?.addField("isHidden", Boolean::class.java)
            schema.get("BuyList")?.addField("handSortPosition", Long::class.java)
            schema.get("BuyListItem")?.addField("modified", Date::class.java)
            schema.get("BuyListItem")?.addField("created", Date::class.java)
            schema.get("BuyListItem")?.addField("handSortPosition", Long::class.java)
            oldVersion++
        }
        if (oldVersion == 2L) {
            schema.get("BuyList")?.addField("personalOrderType", Int::class.java)
            oldVersion++
        }
//
//        // DynamicRealm exposes an editable schema
//
//
//        // Migrate to version 1: Add a new class.
//        // Example:
//        // public Person extends RealmObject {
//        //     private String name;
//        //     private int age;
//        //     // getters and setters left out for brevity
//        // }
//        if (oldVersion == 0) {
//            schema.create("Person")
//                    .addField("name", String.class)
//                    .addField("age", int.class);
//            oldVersion++;
//        }
//
//        // Migrate to version 2: Add a primary key + object references
//        // Example:
//        // public Person extends RealmObject {
//        //     private String name;
//        //     @PrimaryKey
//        //     private int age;
//        //     private Dog favoriteDog;
//        //     private RealmList<Dog> dogs;
//        //     // getters and setters left out for brevity
//        // }
//        if (oldVersion == 1) {
//            schema.get("Person")
//                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
//                    .addRealmObjectField("favoriteDog", schema.get("Dog"))
//                    .addRealmListField("dogs", schema.get("Dog"));
//            oldVersion++;
//        }
    }
}