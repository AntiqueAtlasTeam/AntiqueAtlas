package kenkron.antiqueatlasoverlay;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = AntiqueAtlasOverlayMod.MODID,  certificateFingerprint = AntiqueAtlasOverlayMod.SHA_HASH, version = AntiqueAtlasOverlayMod.VERSION, name = AntiqueAtlasOverlayMod.NAME, clientSideOnly = true, dependencies = "required-after:antiqueatlas;after:forge@[13.20.0.2262,)")
public class AntiqueAtlasOverlayMod {
    public static final String MODID = "antiqueatlasoverlay";
    public static final String NAME = "AntiqueAtlasOverlay";
    public static final String VERSION = "1.2";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static final String SHA_HASH = "@FINGERPRINT@";

    @Mod.EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
        LOGGER.warn("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }
}
