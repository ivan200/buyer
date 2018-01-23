package app.simple.buyer.util;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by Zakharovi on 23.01.2018.
 */

public class DBMigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

        RealmSchema schema = realm.getSchema();
        if (oldVersion == 0) {
            schema.get("BuyItem")
                    .removeField("count");
            schema.get("BuyListItem")
                    .removeField("price");
            oldVersion++;
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