package hunternif.mc.atlas.client.gui;

/** Listener for left click on a button. */
public interface IButtonListener<B extends GuiComponentButton> {
	/** Called when the button was left-clicked on.
	 * @param button the button which was clicked on.
	 */
	void onClick(B button);
}
