package hunternif.mc.atlas.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** A group of GuiRadioButtons only one of which can be selected at any time. */
public class RadioGroup<B extends GuiRadioButton> implements Iterable<B> {
	private final List<B> buttons = new ArrayList<B>();
	
	private final List<ISelectListener<? extends B>> listeners = new ArrayList<ISelectListener<? extends B>>();
	
	private B selectedButton = null;
	
	private final ClickListener clickListener;
	
	public RadioGroup() {
		clickListener = this.new ClickListener();
	}
	
	public void addButton(B button) {
		buttons.add(button);
		button.addListener(clickListener);
		button.setRadioGroup(this);
	}
	
	/** Returns the RadioButton that is selected at the moment, or null if none is. */
	public B getSelectedButton() {
		return selectedButton;
	}
	/** Sets the specified button as selected, provided it is in this group.
	 * Doesn't trigger the select handlers! */
	public void setSelectedButton(B button) {
		if (buttons.contains(button)) {
			if (selectedButton != null) {
				selectedButton.setSelected(false);
			}
			button.setSelected(true);
			selectedButton = button;
		}
	}
	
	@Override
	public Iterator<B> iterator() {
		return buttons.iterator();
	}
	
	public void addListener(ISelectListener<? extends B> listener) {
		listeners.add(listener);
	}
	public void removeListener(ISelectListener<? extends B> listener) {
		listeners.remove(listener);
	}
	
	private class ClickListener implements IButtonListener<B> {
		@Override
		public void onClick(B button) {
			if (button != selectedButton) {
				if (selectedButton != null) {
					selectedButton.setSelected(false);
				}
				selectedButton = button;
				for (ISelectListener listener : listeners) {
					listener.onSelect(selectedButton);
				}
			}
		}
	}
}
