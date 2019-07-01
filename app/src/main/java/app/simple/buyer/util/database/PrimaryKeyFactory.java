package app.simple.buyer.util.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import app.simple.buyer.entities.BuyItem;
import app.simple.buyer.entities.BuyList;
import app.simple.buyer.entities.BuyListItem;
import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by Zakharovi on 06.03.2018.
 */

//Определение первичных ключей для моделей таблиц, где ключи требуется генерировать.
public class PrimaryKeyFactory {
    private final static Logger logger = Logger.getLogger(PrimaryKeyFactory.class.getName());

    private static final String PRIMARY_KEY_FIELD = "id";

    private static Map<Class<? extends RealmObject>, AtomicLong> keys = new HashMap<>();

    public static void initialize(Realm realm) {
        List<Class<? extends RealmObject>> model = new ArrayList<>();
        model.add(BuyItem.class);
        model.add(BuyList.class);
        model.add(BuyListItem.class);

        keys = new HashMap<>();

        for (Class<? extends RealmObject> c : model) {
            Number keyValue = null;
            try {
                if(realm.where(c).count() > 0 ){
                    keyValue = realm.where(c).max(PRIMARY_KEY_FIELD);
                }
            } catch (Exception ex) {
                logger.log(Level.WARNING, "error while getting number primary key for " + c.getSimpleName(), ex);
            }
            if (keyValue != null) {
                keys.put(c, new AtomicLong(keyValue.longValue()));
            }
        }
    }

    // Automitically create next key
    public static synchronized long nextKey(Class<? extends RealmObject> clazz) {
        if (keys == null) {
            throw new IllegalStateException("not initialized yet");
        }
        AtomicLong l = keys.get(clazz);
        if (l == null) {
            //There was no primary keys for " + clazz.getSimpleName());
            //RealmConfiguration#getRealmObjectClasses() returns only classes with existing instances
            //so we need to store value for the first instance created
            l = new AtomicLong(0);
            keys.put(clazz, l);
        }
        return l.incrementAndGet();
    }
}
