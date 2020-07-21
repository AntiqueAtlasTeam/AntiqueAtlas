package hunternif.mc.impl.atlas;

import hunternif.mc.impl.atlas.core.BiomeDetectorBase;
import hunternif.mc.impl.atlas.ext.ExtTileIdMap;
import hunternif.mc.impl.atlas.util.Log;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import java.io.File;
import java.util.Set;
import java.util.function.Function;

public class CommonProxy {
	static File configDir;

	public void init() {
		configDir = new File(net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDirectory(), "antiqueatlas");
		configDir.mkdir();

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return new Identifier("antiqueatlas:scanbiometypes");
			}

			@Override
			public void apply(ResourceManager var1) {
				BiomeDetectorBase.scanBiomeTypes();
			}
		});
	}

	public void registerPackets(Set<Identifier> clientPackets, Set<Identifier> serverPackets, Function<Identifier, PacketConsumer> consumer) {
		serverPackets.forEach((id) -> ServerSidePacketRegistry.INSTANCE.register(id, consumer.apply(id)));
	}
}
