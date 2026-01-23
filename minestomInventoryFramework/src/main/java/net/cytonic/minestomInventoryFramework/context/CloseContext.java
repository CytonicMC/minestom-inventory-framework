package net.cytonic.minestomInventoryFramework.context;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.context.IFCloseContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.PlatformConfinedContext;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateValue;
import me.devnatan.inventoryframework.state.StateWatcher;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import net.cytonic.minestomInventoryFramework.MinestomViewer;

public final class CloseContext extends PlatformConfinedContext implements IFCloseContext, Context {

    @NotNull
    private final IFRenderContext parent;
    @NotNull
    private final Viewer subject;
    @NotNull
    private final Player player;
    @NotNull
    private final InventoryCloseEvent closeOrigin;
    private boolean cancelled;

    @Internal
    public CloseContext(@NotNull Viewer subject, @NotNull IFRenderContext parent,
        @NotNull InventoryCloseEvent closeOrigin) {
        Check.notNull(subject, "subject");
        Check.notNull(parent, "parent");
        Check.notNull(closeOrigin, "closeOrigin");
        this.parent = parent;
        this.subject = subject;
        this.player = ((MinestomViewer) subject).getPlayer();
        this.closeOrigin = closeOrigin;
    }

    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    @NotNull
    public List<Player> getAllPlayers() {
        return this.getParent().getAllPlayers();
    }

    public void updateTitleForPlayer(@NotNull Component title, @NotNull Player player) {
        Check.notNull(title, "title");
        Check.notNull(player, "player");
        this.getParent().updateTitleForPlayer(title, player);
    }

    public void resetTitleForPlayer(@NotNull Player player) {
        Check.notNull(player, "player");
        this.getParent().resetTitleForPlayer(player);
    }

    @NotNull
    public Viewer getViewer() {
        return this.subject;
    }

    @NotNull
    public RenderContext getParent() {
        IFRenderContext renderContext = this.parent;
        Check.notNull(renderContext, "renderContext");
        return (RenderContext) renderContext;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @NotNull
    public ViewContainer getContainer() {
        ViewContainer viewContainer = this.getParent().getContainer();
        Check.notNull(viewContainer, "viewContainer");
        return viewContainer;
    }

    @Override
    public Object getPlatformEvent() {
        return closeOrigin;
    }

    @NotNull
    public UUID getId() {
        UUID uuid = this.getParent().getId();
        Check.notNull(uuid, "uuid");
        return uuid;
    }

    @NotNull
    public ViewConfig getConfig() {
        ViewConfig viewConfig = this.getParent().getConfig();
        Check.notNull(viewConfig, "viewConfig");
        return viewConfig;
    }

    @NotNull
    public Object getInitialData() {
        Object initialData = this.getParent().getInitialData();
        Check.notNull(initialData, "initialData");
        return initialData;
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
    public String toString() {
        return "CloseContext{subject=" + this.subject + ", player=" + this.getPlayer() + ", parent=" + this.parent
            + ", cancelled=" + this.cancelled + "} " + super.toString();
    }

    @Nullable
    @UnmodifiableView
    public Map<Long, StateValue> getStateValues() {
        return this.getParent().getStateValues();
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
        this.getParent().updateState(state, value);
    }

    public void watchState(long id, @NotNull StateWatcher listener) {
        Check.notNull(listener, "listener");
        this.getParent().watchState(id, listener);
    }
}
