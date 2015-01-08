package hunternif.mc.atlas.client.gui.core;

/** A button that can be toggled on, and only toggled off by selecting
 * a different RadioButton. */
@SuppressWarnings("rawtypes")
public class GuiRadioButton extends GuiComponentButton {
	private boolean selected;
	
	private RadioGroup radioGroup;
	
	/** Sets the button selected state. If the button is part of a RadioGroup,
	 * use the RadioGroup's setSelected method instead! */
	public void setSelected(boolean value) {
		this.selected = value;
	}
	public boolean isSelected() {
		return selected;
	}
	
	protected void setRadioGroup(RadioGroup radioGroup) {
		this.radioGroup = radioGroup;
	}
	public RadioGroup getRadioGroup() {
		return radioGroup;
	}
	
	@Override
	protected void onClick() {
		if (!isSelected()) {
			super.onClick();
			if (isEnabled()) {
				setSelected(true);
			}
		}
	}
}
