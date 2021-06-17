package hunternif.mc.impl.atlas.client;

import hunternif.mc.impl.atlas.AntiqueAtlasModClient;
import hunternif.mc.impl.atlas.client.gui.GuiAtlas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class KeyHandler {
    /** ID's of keys */
    private static final int KEY_ATLAS = 0;

    /** List of bindings (at this moment with only one binding) */
    private static List<KeyBinding> bindings = new ArrayList<>(1);

    public static void registerBindings() {
        // Initialisation of bindings
        bindings.add(KEY_ATLAS, new KeyBinding("key.openatlas.desc", InputMappings.Type.KEYSYM, 77, "key.antiqueatlas.category"));

        // Registering all binding
        bindings.forEach(ClientRegistry::registerKeyBinding);
    }

    public static void onClientTick(Minecraft client) {
        if (bindings.get(KEY_ATLAS).isPressed()) {
            Screen currentScreen = Minecraft.getInstance().currentScreen;
            if (currentScreen instanceof GuiAtlas) {
                currentScreen.closeScreen();
            } else {
                AntiqueAtlasModClient.openAtlasGUI();
            }
        }
    }
}
