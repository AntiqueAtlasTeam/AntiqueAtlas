package hunternif.mc.atlas.client.gui.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

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
	protected void mouseClicked(int x, int y, int mouseButton) throws IOException {
		super.mouseClicked(x, y, mouseButton);
		if (mouseButton == 0 /*left-click*/ && enabled && isMouseOver) {
			onClick();
			mouseHasBeenHandled();
		}
	}
	
	/** Called when the user left-clicks on this component. */
	@SuppressWarnings("unchecked")
	protected void onClick() {
		if (clickSound != null) {
			mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(clickSound, 1.0F));//.playSound(new PositionedSoundRecord(clickSound, 1.0F));
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
