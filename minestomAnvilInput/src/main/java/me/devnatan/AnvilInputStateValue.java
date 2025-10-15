package me.devnatan;

import me.devnatan.inventoryframework.IFDebug;
import me.devnatan.inventoryframework.state.MutableValue;
import me.devnatan.inventoryframework.state.State;

class AnvilInputStateValue extends MutableValue {

    private final AnvilInputConfig config;

    public AnvilInputStateValue(State<?> state, AnvilInputConfig config) {
        super(state, config.initialInput);
        this.config = config;
    }

    @Override
    public void set(Object value) {
        final Object newValue;
        IFDebug.debug("Anvil input value changed to: %s", value);
        if (config.inputChangeHandler == null) newValue = value;
        else newValue = config.inputChangeHandler.apply((String) value);

        super.set(newValue);
    }
}
