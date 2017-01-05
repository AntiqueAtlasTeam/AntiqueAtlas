package hunternif.mc.atlas.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

public class MarkerRegistryImpl<V extends IRegistryEntry> implements IRegistry<ResourceLocation, V>
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<ResourceLocation, V> registryObjects = this.createUnderlyingMap();
    private List<V> values;

    private final ResourceLocation defaultKey;
    
    public MarkerRegistryImpl(ResourceLocation defaultKey) {
		this.defaultKey = defaultKey;
	}
    
    private Map<ResourceLocation, V> createUnderlyingMap()
    {
        return Maps.newHashMap();
    }

    @Nullable
    public V getObject(@Nullable ResourceLocation name)
    {
        V value = this.registryObjects.get(name);
        if(value == null) {
        	value = this.registryObjects.get(defaultKey);
        }
        return value;
    }

    public void putObject(ResourceLocation key, V value)
    {
        Validate.notNull(key);
        Validate.notNull(value);
        this.values = null;

        if (this.registryObjects.containsKey(key))
        {
            LOGGER.debug("Adding duplicate key \'" + key + "\' to registry");
        }

        this.registryObjects.put(key, value);
    }
    
    /**
     * Register an object on this registry.
     */
    public void register(V value)
    {
    	ResourceLocation key = value.getRegistryName();
    	
    	putObject(key, value);
    }

    public Set<ResourceLocation> getKeys()
    {
        return Collections.unmodifiableSet(this.registryObjects.keySet());
    }

    @Nullable
    public V getRandomObject(Random random)
    {
        getValues();

        return this.values.get( random.nextInt(this.values.size()) );
    }
    
    @SuppressWarnings("unchecked")
	public List<V> getValues() {
    	if (this.values == null)
        {
            Collection<?> collection = this.registryObjects.values();

            if (collection.isEmpty())
            {
                this.values = ImmutableList.of();
            } else {
            	this.values = ImmutableList.copyOf((Collection<? extends V>) collection);
            }
        }
    	return this.values;
    }

    /**
     * Does this registry contain an entry for the given key?
     */
    public boolean containsKey(ResourceLocation key)
    {
        return this.registryObjects.containsKey(key);
    }

    public Iterator<V> iterator()
    {
        return this.registryObjects.values().iterator();
    }
}
