package hunternif.mc.atlas.client;

public class KeyHandler {
//    public static final String KEY_DESCRIPTION_ATLAS = "antiqueatlas.key.atlas";
//
//    public static final KeyBinding bindingJournal = new KeyBinding(KEY_DESCRIPTION_ATLAS, Keyboard.KEY_M);
//
//    public KeyHandler() {
//        super(new KeyBinding[]{bindingJournal}, new boolean[]{false});
//    }
//
//    public KeyHandler() {
//        for (int i = 0; i < desc.length; ++i) {
//            keys[i] = new KeyBinding(desc[i], keyValues[i], StatCollector.translateToLocal("key.tutorial.label"));
//            ClientRegistry.registerKeyBinding(keys[i]);
//        }
//    }
//
//    /**
//     * KeyInputEvent is in the FML package, so we must register to the FML event bus
//     */
//    @SubscribeEvent
//    public void onKeyInput(InputEvent.KeyInputEvent event) {
//        Minecraft mc = Minecraft.getMinecraft();
//        // checking inGameHasFocus prevents your keys from firing when the player is typing a chat message
//        // NOTE that the KeyInputEvent will NOT be posted when a gui screen such as the inventory is open
//        // so we cannot close an inventory screen from here; that should be done in the GUI itself
//        if (mc.inGameHasFocus) {
//            if (bindingJournal.isKeyDown()) {
//                if (mc.currentScreen == null) { // In-game screen
//                    PlayerInfo info = AntiqueAtlasMod.playerTracker.getPlayerInfo(mc.thePlayer);
//                    FMLNetworkHandler.openGui(mc.player, new GuiAtlas());
//                } else if (mc.currentScreen instanceof GuiAtlas) {
//                    mc.player.closeScreen();
//                }
//            }
//        }
//    }
}
