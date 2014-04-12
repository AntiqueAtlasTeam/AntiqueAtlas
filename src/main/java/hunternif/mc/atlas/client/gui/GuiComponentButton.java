package hunternif.mc.atlas.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

/** A GuiComponent that can act like a button. */
@SuppressWarnings("rawtypes")
public class GuiComponentButton extends GuiComponent {
	public static final ResourceLocation DEFAULT_CLICK_SOUND = new ResourceLocation("gui.button.press");
	
	private final List<IButtonListener> listeners = new ArrayList<IButtonListener>();
	
	private boolean enabled = true;
	private ResourceLocation clickSound = DEFAULT_CLICK_SOUND;
	protected int buttonWidth, buttonHeight;
	
	public void setEnabled(boolean value) {
		enabled = value;
	}
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setClickSound(ResourceLocation clickSound) {
		this.clickSound = clickSound;
	}
	/** Makes the button produce no sound when clicked on. */
	public void mute() {
		clickSound = null;
	}
	
	@Override
	protected void mouseClicked(int x, int y, int mouseButton) {
		super.mouseClicked(x, y, mouseButton);
		if (mouseButton == 0 /*left-click*/ && enabled && isMouseOver(x, y)) {
			onClick();
		}
	}
	
	/** Called when the user left-clicks on this component. */
	@SuppressWarnings("unchecked")
	protected void onClick() {
		if (clickSound != null) {
			mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(clickSound, 1.0F));
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
	
	public void setSize(int width, int height) {
		setWidth(width);
		setHeight(height);
	}
	
	public void setWidth(int value) {
		this.buttonWidth = value;
	}
	@Override
	public int getWidth() {
		return buttonWidth;
	}
	
	public void setHeight(int value) {
		this.buttonHeight = value;
	}
	@Override
	public int getHeight() {
		return buttonHeight;
	}
}
