package hunternif.mc.atlas.client.gui.core;

/** A button that can be toggled on, and only toggled off by selecting
 * a different ToggleButton. */
@SuppressWarnings("rawtypes")
public class GuiToggleButton extends GuiComponentButton {
	private boolean selected;
	
	private ToggleGroup radioGroup;
	
	/** Sets the button selected state. If the button is part of a RadioGroup,
	 * use the RadioGroup's setSelected method instead! */
	public void setSelected(boolean value) {
		this.selected = value;
	}
	public boolean isSelected() {
		return selected;
	}
	
	protected void setRadioGroup(ToggleGroup radioGroup) {
		this.radioGroup = radioGroup;
	}
	public ToggleGroup getRadioGroup() {
		return radioGroup;
	}
	
	@Override
	protected void onClick() {
		if (!isSelected()) {
			if (isEnabled()) {
				setSelected(true);
			}
			super.onClick();
		}
	}
}
