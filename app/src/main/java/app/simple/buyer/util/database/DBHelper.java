package app.simple.buyer.util.database;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Zakharovi on 10.01.2018.
 */

public class DBHelper {
    private static RealmConfiguration realmConfiguration;
    private static RealmConfiguration getRealmConfiguration(){
        if(realmConfiguration == null){
            realmConfiguration = new RealmConfiguration.Builder()
                    .schemaVersion(1)
                    .migration(new DBMigration())
                    .build();
        }
        return realmConfiguration;
    }

    public static Realm getRealm(){
        return Realm.getInstance(getRealmConfiguration());
    }

}
