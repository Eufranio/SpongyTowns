package io.github.eufranio.spongytowns.storage;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Created by Frani on 19/03/2018.
 */
public class SerializableObject<T> {

    private Class<T> clazz;

    @SuppressWarnings("unchecked")
    protected SerializableObject(Class<T> clazz) {
        this.clazz = clazz;
    }

    @SuppressWarnings("unchecked")
    public String write() {
        GsonConfigurationLoader loader = GsonConfigurationLoader.builder().build();
        try {
            ConfigurationNode node = loader.createEmptyNode().setValue(TypeToken.of(clazz), (T) this);
            StringWriter writer = new StringWriter();
            loader.saveInternal(node, writer);

            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <R> R read(String json, Class<R> clazz) {
        try {
            GsonConfigurationLoader loader = GsonConfigurationLoader.builder().setSource(() -> new BufferedReader(new StringReader(json))).build();
            ConfigurationNode node = loader.load();
            return node.getValue(TypeToken.of(clazz));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}