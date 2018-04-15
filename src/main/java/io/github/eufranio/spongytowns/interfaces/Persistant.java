package io.github.eufranio.spongytowns.interfaces;

import com.j256.ormlite.misc.BaseDaoEnabled;
import io.github.eufranio.spongytowns.storage.DataKey;
import io.github.eufranio.spongytowns.storage.DataKeys;
import io.github.eufranio.spongytowns.storage.SerializableObject;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Frani on 13/03/2018.
 */
public interface Persistant<T extends BaseDaoEnabled<T, UUID>> extends Identifiable {

    T get();

    default void updateStorage() {
        try {
            this.get().update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    Map<String, String> getCustomData();

    default <R extends SerializableObject> R get(DataKey<? extends R> key) {
        String str = this.getCustomData().get(key.getKey());
        if (str == null) {
            R obj = key.getDefaultInstance();
            this.writeCustomData(key, obj);
            return obj;
        }
        return SerializableObject.read(str, key.getClazz());
    }

    default void writeCustomData(DataKey key, SerializableObject obj) {
        if (this.getCustomData().containsKey(key.getKey())) {
            this.getCustomData().replace(key.getKey(), obj.write());
        } else {
            this.getCustomData().put(key.getKey(), obj.write());
        }
        this.updateStorage();
    }

}
