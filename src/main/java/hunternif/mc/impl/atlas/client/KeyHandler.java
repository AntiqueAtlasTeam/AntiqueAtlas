package hunternif.mc.impl.atlas.client;

import hunternif.mc.impl.atlas.AntiqueAtlasModClient;
import hunternif.mc.impl.atlas.client.gui.GuiAtlas;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import java.util.ArrayList;
import java.util.List;
import com.mojang.blaze3d.platform.InputConstants;

@OnlyIn(Dist.CLIENT)
public class KeyHandler {
    /**
     * ID's of keys
     */
    private static final int KEY_ATLAS = 0;

    /**
     * List of bindings (at this moment with only one binding)
     */
    private static List<KeyMapping> bindings = new ArrayList<>(1);

    public static void registerBindings() {
        // Initialisation of bindings
        bindings.add(KEY_ATLAS, new KeyMapping("key.openatlas.desc", InputConstants.Type.KEYSYM, 77, "key.antiqueatlas.category"));

        // Registering all binding
        bindings.forEach(ClientRegistry::registerKeyBinding);
    }

    public static void onClientTick(ClientTickEvent event) {
        if (bindings.get(KEY_ATLAS).consumeClick()) {
            Screen currentScreen = Minecraft.getInstance().screen;
            if (currentScreen instanceof GuiAtlas) {
                currentScreen.onClose();
            } else {
                AntiqueAtlasModClient.openAtlasGUI();
            }
        }
    }
}
