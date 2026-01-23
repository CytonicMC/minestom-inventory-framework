package net.cytonic.minestomInventoryFramework.context;

import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import me.devnatan.inventoryframework.context.SlotContext;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import net.cytonic.minestomInventoryFramework.MinestomViewer;

public final class SlotRenderContext extends SlotContext implements IFSlotRenderContext {

    @Nullable
    private final Viewer viewer;
    @NotNull
    private final Player player;
    @NotNull
    private ItemStack item;
    private boolean cancelled;
    private boolean changed;
    private boolean forceUpdate;
    @NotNull
    private final Component component;

    @Internal
    public SlotRenderContext(int slot, @NotNull IFRenderContext parent, @Nullable Viewer viewer,
        @NotNull Component component) {
        super(slot, parent);
        Check.notNull(parent, "parent");
        this.viewer = viewer;
        Check.notNull(viewer, "viewer");
        this.player = ((MinestomViewer) viewer).getPlayer();
        Check.notNull(ItemStack.AIR, "AIR");
        this.item = ItemStack.AIR;
        Check.notNull(component, "component");
        this.component = component;
    }

    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    @NotNull
    public ItemStack getItem() {
        return this.item;
    }

    public void setItem(@NotNull ItemStack var1) {
        Check.notNull(var1, "<set-?>");
        this.item = var1;
    }

    @NotNull
    public ItemStack getResult() {
        return this.getItem();
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void clear() {
        ItemStack var10001 = ItemStack.AIR;
        Check.notNull(var10001, "AIR");
        this.setItem(var10001);
    }

    public boolean hasChanged() {
        return this.changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public boolean isForceUpdate() {
        return this.forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public boolean isOnEntityContainer() {
        return this.getContainer().isEntityContainer();
    }

    @Override
    public @NonNull Component getComponent() {
        return component;
    }

    @Nullable
    public Viewer getViewer() {
        return this.viewer;
    }

    public void closeForPlayer() {
        this.getParent().closeForPlayer();
    }

    public void openForPlayer(@NotNull Class<? extends RootView> other) {
        Check.notNull(other, "other");
        this.getParent().openForPlayer(other);
    }

    public void openForPlayer(@NotNull Class<? extends RootView> other, @NotNull Object initialData) {
        Check.notNull(other, "other");
        Check.notNull(initialData, "initialData");
        this.getParent().openForPlayer(other, initialData);
    }

    public void updateTitleForPlayer(@NotNull String title) {
        Check.notNull(title, "title");
        this.getParent().updateTitleForPlayer(title);
    }

    @Override
    public void updateTitleForPlayer(@NotNull Object titleComponent) {
        this.getParent().updateTitleForPlayer(titleComponent);
    }

    public void resetTitleForPlayer() {
        this.getParent().resetTitleForPlayer();
    }
}
