package hunternif.mc.impl.atlas.client.gui.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A group of GuiToggleButtons only one of which can be selected at any time.
 */
public class ToggleGroup<B extends GuiToggleButton> implements Iterable<B> {
    private final List<B> buttons = new ArrayList<>();

    private final List<ISelectListener<? extends B>> listeners = new ArrayList<>();

    private B selectedButton = null;

    private final ClickListener clickListener;

    public ToggleGroup() {
        clickListener = this.new ClickListener();
    }

    public boolean addButton(B button) {
        if (!buttons.contains(button)) {
            buttons.add(button);
            button.addListener(clickListener);
            button.setRadioGroup(this);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeButton(B button) {
        if (buttons.remove(button)) {
            button.removeListener(clickListener);
            button.setRadioGroup(null);
            return true;
        } else {
            return false;
        }
    }

    public void removeAllButtons() {
        Iterator<B> iter = buttons.iterator();
        while (iter.hasNext()) {
            B button = iter.next();
            button.removeListener(clickListener);
            button.setRadioGroup(null);
            iter.remove();
        }
    }

    /**
     * Returns the RadioButton that is selected at the moment, or null if none is.
     */
    public B getSelectedButton() {
        return selectedButton;
    }

    /**
     * Sets the specified button as selected, provided it is in this group.
     * Doesn't trigger the select handlers!
     */
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
        @SuppressWarnings({"rawtypes", "unchecked"})
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
