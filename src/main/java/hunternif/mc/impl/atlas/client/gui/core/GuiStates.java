package hunternif.mc.impl.atlas.client.gui.core;

/**
 * A mechanism to encapsulate actions that need to be done every time a GUI
 * switches between distinct states of behavior.
 *
 * @author Hunternif
 */
public class GuiStates {
    /**
     * Meant to declare anonymous classes.
     */
    public interface IState {
        void onEnterState();

        void onExitState();
    }

    /**
     * A simple state that does nothing upon enter or exit.
     */
    public static class SimpleState implements IState {
        @Override
        public void onEnterState() {
        }

        @Override
        public void onExitState() {
        }
    }

    private volatile IState currentState;

    public IState current() {
        return currentState;
    }

    public boolean is(IState state) {
        return currentState == state;
    }

    public void switchTo(IState state) {
        if (currentState != null) {
            currentState.onExitState();
        }
        currentState = state;
        if (state != null) {
            state.onEnterState();
        }
    }
}
