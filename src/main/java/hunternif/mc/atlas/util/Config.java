package hunternif.mc.atlas.util;

/**
 * This class is responsible for saving {@link SaveData} in a persistent
 * storage, such as a config file.
 * @author Hunternif
 * @param <T>	the data container class to be loaded into and saved from.
 */
public interface Config<T extends SaveData> {
	void load(T data);
	void save(T data);
}
