package hunternif.mc.atlas.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
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
        if (bindings.get(KEY_ATLAS).isPressed()) {
            AntiqueAtlasMod.proxy.openAtlasGUI();
        }
    }
}
