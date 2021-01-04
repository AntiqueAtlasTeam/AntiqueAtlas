package hunternif.mc.impl.atlas.util;

/**
 * This class is responsible for saving {@link SaveData} in a persistent
 * storage, such as a config file.
 *
 * @param <T> the data container class to be loaded into and saved from.
 * @author Hunternif
 */
interface Config<T extends SaveData> {
    void load(T data);

    void save(T data);
}
