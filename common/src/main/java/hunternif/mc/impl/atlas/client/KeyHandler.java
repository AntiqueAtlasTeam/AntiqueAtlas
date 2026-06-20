package hunternif.mc.impl.atlas.client;

import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import hunternif.mc.impl.atlas.AntiqueAtlasModClient;
import hunternif.mc.impl.atlas.client.gui.GuiAtlas;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class KeyHandler {
    public static final KeyBinding ATLAS_KEYMAPPING = new KeyBinding("key.openatlas.desc", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M, "key.antiqueatlas.category");

    public static void registerBindings() {
        KeyMappingRegistry.register(ATLAS_KEYMAPPING);
    }

    public static void onClientTick(MinecraftClient client) {
        while (ATLAS_KEYMAPPING.wasPressed()) {
            Screen currentScreen = MinecraftClient.getInstance().currentScreen;
            if (currentScreen instanceof GuiAtlas) {
                currentScreen.close();
            } else {
                AntiqueAtlasModClient.openAtlasGUI();
            }
        }
    }
}
