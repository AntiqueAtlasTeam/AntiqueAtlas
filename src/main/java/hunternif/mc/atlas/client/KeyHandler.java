package hunternif.mc.atlas.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.client.gui.GuiAtlas;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import none.XX_1_12_2_none_blk_XX;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class KeyHandler {
    /** ID's of keys */
    private static final int KEY_ATLAS = 0;

    /** List of bindings (at this moment with only one binding) */
    private static List<KeyBinding> bindings = new ArrayList<>(1);

    public static void registerBindings() {
        // Initialisation of bindings
        bindings.add(KEY_ATLAS, new KeyBinding("key.openatlas.desc", Keyboard.KEY_M, "key.antiqueatlas.category"));

        // Registering all binding
        bindings.forEach(ClientRegistry::registerKeyBinding);
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onKeyEvent(InputEvent.KeyInputEvent event) {
        if (bindings.get(KEY_ATLAS).wasPressed()) {
            XX_1_12_2_none_blk_XX currentScreen = MinecraftClient.getInstance().XX_1_12_2_m_XX;
            if (currentScreen instanceof GuiAtlas) {
                ((GuiAtlas) currentScreen).close();
            } else {
                AntiqueAtlasMod.proxy.openAtlasGUI();
            }
        }
    }
}
