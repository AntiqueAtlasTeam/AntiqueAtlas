package hunternif.mc.impl.atlas.client.gui.core;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

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
	public boolean mouseClicked(double x, double y, int mouseButton) {
		if (mouseButton == 0 /*left-click*/ && enabled && isMouseOver) {
			onClick();
			return true;
		}

		return super.mouseClicked(x, y, mouseButton);
	}
	
	/** Called when the user left-clicks on this component. */
	@SuppressWarnings("unchecked")
    void onClick() {
		if (clickSound != null) {
			Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(clickSound, 1.0F));
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
