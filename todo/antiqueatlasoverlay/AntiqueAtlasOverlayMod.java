package kenkron.antiqueatlasoverlay;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = AntiqueAtlasOverlayMod.MODID, version = AntiqueAtlasOverlayMod.VERSION, name = AntiqueAtlasOverlayMod.MODID, clientSideOnly = true, dependencies = "required-after:antiqueatlas;after:forge@[13.20.0.2262,)")
public class AntiqueAtlasOverlayMod {
    public static final String MODID = "antiqueatlasoverlay";
    public static final String VERSION = "1.2";
    public static final Logger LOGGER = LogManager.getLogger("AntiqueAtlasOverlay");
}
