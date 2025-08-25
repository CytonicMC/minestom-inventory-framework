package net.cytonic.minestomInventoryFramework.context;


import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.inventory.click.Click;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SlotClickContext extends SlotContext implements IFSlotClickContext {
    @NotNull
    private final Viewer whoClicked;
    @NotNull
    private final ViewContainer clickedContainer;
    @Nullable
    private final Component clickedComponent;
    @NotNull
    private final InventoryPreClickEvent clickOrigin;
    private final boolean combined;
    private boolean cancelled;

    @Internal
    public SlotClickContext(int slot, @NotNull IFRenderContext parent, @NotNull Viewer whoClicked, @NotNull ViewContainer clickedContainer, @Nullable Component clickedComponent, @NotNull InventoryPreClickEvent clickOrigin, boolean combined) {
        super(slot, parent);
        Check.notNull(parent, "parent");
        Check.notNull(whoClicked, "whoClicked");
        Check.notNull(clickedContainer, "clickedContainer");
        Check.notNull(clickOrigin, "clickOrigin");
        this.whoClicked = whoClicked;
        this.clickedContainer = clickedContainer;
        this.clickedComponent = clickedComponent;
        this.clickOrigin = clickOrigin;
        this.combined = combined;
    }

    @NotNull
    public final InventoryPreClickEvent getClickOrigin() {
        return this.clickOrigin;
    }

    @NotNull
    public Player getPlayer() {
        Player var10000 = this.clickOrigin.getPlayer();
        Check.notNull(var10000, "getPlayer(...)");
        return var10000;
    }

    @NotNull
    public ItemStack getItem() {
        ItemStack var10000 = this.clickOrigin.getClickedItem();
        Check.notNull(var10000, "getClickedItem(...)");
        return var10000;
    }

    @NotNull
    public final Click getClick() {
        Click var10000 = this.clickOrigin.getClick();
        Check.notNull(var10000, "getClick(...)");
        return var10000;
    }

    @Nullable
    public Component getComponent() {
        return this.clickedComponent;
    }

    @NotNull
    public ViewContainer getClickedContainer() {
        return this.clickedContainer;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
        this.clickOrigin.setCancelled(cancelled);
    }

    @NotNull
    public Object getPlatformEvent() {
        return this.clickOrigin;
    }

    public int getClickedSlot() {
        return this.clickOrigin.getSlot();
    }

    public boolean isLeftClick() {
        return this.clickOrigin.getClick() instanceof Click.Left;
    }

    public boolean isRightClick() {
        return this.clickOrigin.getClick() instanceof Click.Right;
    }

    public boolean isShiftLeftClick() {
        return this.clickOrigin.getClick() instanceof Click.LeftShift;
    }

    public boolean isShiftRightClick() {
        return this.clickOrigin.getClick() instanceof Click.RightShift;
    }

    public boolean isMiddleClick() {
        return this.clickOrigin.getClick() instanceof Click.Middle;
    }

    public boolean isShiftClick() {
        Click clickType = this.clickOrigin.getClick();
        return clickType instanceof Click.LeftShift || clickType instanceof Click.RightShift;
    }

    public boolean isKeyboardClick() {
        return this.clickOrigin.getClick() instanceof Click.HotbarSwap;
    }

    public boolean isOutsideClick() {
        return this.clickOrigin.getSlot() < 0;
    }

    public boolean isOnEntityContainer() {
        return this.clickOrigin.getInventory() instanceof PlayerInventory;
    }

    @NotNull
    public Viewer getViewer() {
        return this.whoClicked;
    }

    public void closeForPlayer() {
        this.getParent().closeForPlayer();
    }

    public void openForPlayer(@NotNull Class other) {
        Check.notNull(other, "other");
        this.getParent().openForPlayer(other);
    }

    public void openForPlayer(@NotNull Class other, @NotNull Object initialData) {
        Check.notNull(other, "other");
        Check.notNull(initialData, "initialData");
        this.getParent().openForPlayer(other, initialData);
    }

    public void updateTitleForPlayer(@NotNull String title) {
        Check.notNull(title, "title");
        this.getParent().updateTitleForPlayer(title);
    }

    public void resetTitleForPlayer() {
        this.getParent().resetTitleForPlayer();
    }

    public boolean isCombined() {
        return this.combined;
    }

    @Override
    public String toString() {
        return "SlotClickContext{" +
                "whoClicked=" + whoClicked +
                ", clickedContainer=" + clickedContainer +
                ", clickedComponent=" + clickedComponent +
                ", clickOrigin=" + clickOrigin +
                ", combined=" + combined +
                ", cancelled=" + cancelled +
                '}';
    }
}
