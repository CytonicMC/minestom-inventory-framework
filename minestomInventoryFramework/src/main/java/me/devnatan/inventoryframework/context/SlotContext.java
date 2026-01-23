package me.devnatan.inventoryframework.context;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.devnatan.inventoryframework.IFDebug;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateValue;
import me.devnatan.inventoryframework.state.StateWatcher;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.cytonic.minestomInventoryFramework.context.Context;

public abstract class SlotContext extends PlatformContext implements IFSlotContext, Context {

    @NotNull
    private final IFRenderContext parent;
    private int slot;

    @Internal
    public SlotContext(int slot, @NotNull IFRenderContext parent) {
        Check.notNull(parent, "parent");
        this.slot = slot;
        this.parent = parent;
    }

    @NotNull
    public abstract ItemStack getItem();

    @NotNull
    public RenderContext getParent() {
        IFRenderContext renderContext = this.parent;
        Check.notNull(renderContext, "renderContext");
        return (RenderContext) renderContext;
    }

    public int getSlot() {
        return this.slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    @NotNull
    public ViewContainer getContainer() {
        ViewContainer container = this.getParent().getContainer();
        Check.notNull(container, "container");
        return container;
    }

    @NotNull
    public Map<String, Viewer> getIndexedViewers() {
        Map<String, Viewer> indexedViewers = this.getParent().getIndexedViewers();
        Check.notNull(indexedViewers, "indexedViewers");
        return indexedViewers;
    }

    @NotNull
    public List<Component> getComponents() {
        List<Component> components = this.getParent().getComponents();
        Check.notNull(components, "components");
        return components;
    }

    @NotNull
    public List<Component> getInternalComponents() {
        List<Component> components = this.getParent().getInternalComponents();
        Check.notNull(components, "components");
        return components;
    }

    @NotNull
    public List<Component> getComponentsAt(int position) {
        List<Component> components = this.getParent().getComponentsAt(position);
        Check.notNull(components, "components");
        return components;
    }

    public void addComponent(@NotNull Component component) {
        Check.notNull(component, "component");
        this.getParent().addComponent(component);
    }

    public void removeComponent(@NotNull Component component) {
        Check.notNull(component, "component");
        this.getParent().removeComponent(component);
    }

    public void renderComponent(@NotNull Component component) {
        Check.notNull(component, "component");
        this.getParent().renderComponent(component);
    }

    public void updateComponent(@NotNull Component component, boolean force) {
        Check.notNull(component, "component");
        this.getParent().updateComponent(component, force);
    }

    public void performClickInComponent(@NotNull Component component, @NotNull Viewer viewer,
        @NotNull ViewContainer clickedContainer, @NotNull Object platformEvent, int clickedSlot, boolean combined) {
        Check.notNull(component, "component");
        Check.notNull(viewer, "viewer");
        Check.notNull(clickedContainer, "clickedContainer");
        Check.notNull(platformEvent, "platformEvent");
        this.getParent()
            .performClickInComponent(component, viewer, clickedContainer, platformEvent, clickedSlot, combined);
    }

    public void update() {
        this.getParent().update();
    }

    @NotNull
    public StateValue getUninitializedStateValue(long stateId) {
        StateValue uninitializedStateValue = this.getParent().getUninitializedStateValue(stateId);
        Check.notNull(uninitializedStateValue, "uninitializedStateValue");
        return uninitializedStateValue;
    }

    @NotNull
    public Object getRawStateValue(@Nullable State state) {
        Object rawStateValue = this.getParent().getRawStateValue(state);
        Check.notNull(rawStateValue, "rawStateValue");
        return rawStateValue;
    }

    @NotNull
    public StateValue getInternalStateValue(@NotNull State state) {
        Check.notNull(state, "state");
        StateValue stateValue = this.getParent().getInternalStateValue(state);
        Check.notNull(stateValue, "stateValue");
        return stateValue;
    }

    public void initializeState(long id, @NotNull StateValue value) {
        Check.notNull(value, "value");
        this.getParent().initializeState(id, value);
    }

    public void updateState(@NotNull State state, @NotNull Object value) {
        Check.notNull(state, "state");
        Check.notNull(value, "value");
        IFDebug.debug("minestom IF update state -> %s", value);
        this.getParent().updateState(state, value);
    }

    public void watchState(long id, @NotNull StateWatcher listener) {
        Check.notNull(listener, "listener");
        IFDebug.debug("minestom IF watch state -> %s", listener);
        this.getParent().watchState(id, listener);
    }

    @NotNull
    public UUID getId() {
        UUID id = this.getParent().getId();
        Check.notNull(id, "id");
        return id;
    }

    @NotNull
    public ViewConfig getConfig() {
        ViewConfig config = this.getParent().getConfig();
        Check.notNull(config, "config");
        return config;
    }

    @NotNull
    public Object getInitialData() {
        Object var10000 = this.getParent().getInitialData();
        Check.notNull(var10000, "getInitialData(...)");
        return var10000;
    }

    public void setInitialData(@NotNull Object initialData) {
        Check.notNull(initialData, "initialData");
        this.getParent().setInitialData(initialData);
    }

    @NotNull
    public View getRoot() {
        return this.getParent().getRoot();
    }

    @NotNull
    public String getTitle() {
        String title = this.getParent().getTitle();
        Check.notNull(title, "title");
        return title;
    }

    public boolean isActive() {
        return this.getParent().isActive();
    }

    public void setActive(boolean active) {
        this.getParent().setActive(active);
    }

    public boolean isEndless() {
        return this.getParent().isEndless();
    }

    public void setEndless(boolean endless) {
        this.getParent().setEndless(endless);
    }

    @NotNull
    public List<Player> getAllPlayers() {
        return this.getParent().getAllPlayers();
    }

    public void updateTitleForPlayer(@NotNull net.kyori.adventure.text.Component title, @NotNull Player player) {
        Check.notNull(title, "title");
        Check.notNull(player, "player");
        this.getParent().updateTitleForPlayer(title, player);
    }

    public void resetTitleForPlayer(@NotNull Player player) {
        Check.notNull(player, "player");
        this.getParent().resetTitleForPlayer(player);
    }

    public void back() {
        this.getParent().back();
    }

    public void back(@NotNull Object initialData) {
        Check.notNull(initialData, "initialData");
        this.getParent().back(initialData);
    }

    public boolean canBack() {
        return this.getParent().canBack();
    }
}
