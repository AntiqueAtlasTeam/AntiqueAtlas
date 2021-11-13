package hunternif.mc.impl.atlas.forge;

import java.util.Map.Entry;

public class EntryPair<K, V> implements Entry<K, V> {
	final K key;
    V value;

	public EntryPair(K key, V value) {
		this.key = key;
        this.value = value;
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		V oldValue = this.value;
		this.value = value;
		return oldValue;
	}

}
