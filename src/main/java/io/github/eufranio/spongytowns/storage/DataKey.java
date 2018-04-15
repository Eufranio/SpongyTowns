package io.github.eufranio.spongytowns.storage;

import lombok.Getter;

/**
 * Created by Frani on 23/03/2018.
 */
@Getter
public class DataKey<R> {

    private String key;
    private Class<R> clazz;

    private DataKey(String key, Class<R> clazz) {
        this.key = key;
        this.clazz = clazz;
    }

    @SuppressWarnings("unchecked")
    public <R> R getDefaultInstance() {
        try {
            return (R) clazz.newInstance();
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public static <R> DataKey<R> of(String key, Class<R> clazz) {
        return new DataKey<>(key, clazz);
    }

}
