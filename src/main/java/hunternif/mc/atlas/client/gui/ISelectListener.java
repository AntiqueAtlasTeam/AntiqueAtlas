package hunternif.mc.atlas.client.gui;

/** Listener for button select in a RadioGroup. */
public interface ISelectListener<B extends GuiRadioButton> {
	/** Called when a button in the group was selected.
	 * @param button the button which was selected.
	 */
	void onSelect(B button);
}
