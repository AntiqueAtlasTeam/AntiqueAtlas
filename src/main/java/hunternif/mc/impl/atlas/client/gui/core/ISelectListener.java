package hunternif.mc.impl.atlas.client.gui.core;

/**
 * Listener for button select in a RadioGroup.
 */
public interface ISelectListener<B extends GuiToggleButton> {
    /**
     * Called when a button in the group was selected.
     *
     * @param button the button which was selected.
     */
    void onSelect(B button);
}
