package hunternif.mc.atlas.client.gui.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

/** A GuiComponent that can act like a button. */
public class GuiComponentButton extends GuiComponent {
  private final List<IButtonListener> listeners = new ArrayList<>();
	
	private boolean enabled = true;
	private SoundEvent clickSound = SoundEvents.UI_BUTTON_CLICK;
	
	public void setEnabled(boolean value) {
		enabled = value;
	}
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setClickSound(SoundEvent clickSound) {
		this.clickSound = clickSound;
	}
	/** Makes the button produce no sound when clicked on. */
	public void mute() {
		clickSound = null;
	}
	
	@Override
	protected void a(int x, int y, int mouseButton) throws IOException {
		super.mouseClicked(x, y, mouseButton);
		if (mouseButton == 0 /*left-click*/ && enabled && isMouseOver) {
			onClick();
			mouseHasBeenHandled();
		}
	}
	
	/** Called when the user left-clicks on this component. */
	@SuppressWarnings("unchecked")
    void onClick() {
		if (clickSound != null) {
			// TODO FABRIC gui instead of master??
			client.getSoundLoader().play(PositionedSoundInstance.master(clickSound, 1.0F));
		}
		for (IButtonListener listener : listeners) {
			listener.onClick(this);
		}
	}
	
	public void addListener(IButtonListener listener) {
		listeners.add(listener);
	}
	public void removeListener(IButtonListener listener) {
		listeners.remove(listener);
	}
}
