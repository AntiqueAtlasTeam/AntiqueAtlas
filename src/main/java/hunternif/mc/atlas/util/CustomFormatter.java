package hunternif.mc.atlas.util;

import java.util.Objects;

public class CustomFormatter {
	public static String getRegistryString(net.minecraftforge.registries.IForgeRegistryEntry.Impl<?> entry) {
		return Objects.requireNonNull(entry.getRegistryName()).toString();
	}
}
