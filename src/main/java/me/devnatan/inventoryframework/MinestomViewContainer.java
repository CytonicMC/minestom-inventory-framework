package me.devnatan.inventoryframework;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record MinestomViewContainer(@NotNull Inventory inventory, boolean isShared, @NotNull ViewType type,
                                    boolean proxied) implements ViewContainer {
    public MinestomViewContainer {
        Check.notNull(inventory, "inventory");
        Check.notNull(type, "type");
    }

    public boolean isProxied() {
        return this.proxied;
    }

    @NotNull
    public String getTitle() {
        boolean diffTitle = inventory.getViewers().stream().map(Player::getOpenInventory).filter(inv -> inv instanceof Inventory).map(inv -> (Inventory) inv).map(inv -> PlainTextComponentSerializer.plainText().serialize(inv.getTitle())).distinct().anyMatch(title -> true);
        if (diffTitle && this.isShared) {
            throw new IllegalStateException("Cannot get unique title of shared inventory");
        }
        AbstractInventory openInventory = inventory.getViewers().iterator().next().getOpenInventory();
        if (openInventory instanceof Inventory) {
            return PlainTextComponentSerializer.plainText().serialize(((Inventory) openInventory).getTitle());
        }
        return "";
    }

    @NotNull
    public String getTitle(@NotNull Viewer viewer) {
        Check.notNull(viewer, "viewer");
        MinestomViewer minestomViewer = (MinestomViewer) viewer;
        if (minestomViewer.getPlayer().getOpenInventory() instanceof Inventory) {
            return PlainTextComponentSerializer.plainText().serialize(((Inventory) minestomViewer.getPlayer().getOpenInventory()).getTitle());
        }
        return "";
    }

    @NotNull
    public ViewType getType() {
        return this.type;
    }

    public int getRowsCount() {
        return this.getSize() / this.getColumnsCount();
    }

    public int getColumnsCount() {
        return this.type.getColumns();
    }

    public void renderItem(int slot, @NotNull Object item) {
        Check.notNull(item, "item");
        this.requireSupportedItem(item);
        this.inventory.setItemStack(slot, (ItemStack) item);
    }

    public void removeItem(int slot) {
        this.inventory.setItemStack(slot, ItemStack.AIR);
    }

    public boolean matchesItem(int slot, @Nullable Object item, boolean exactly) {
        this.requireSupportedItem(item);
        Check.notNull(inventory.getItemStack(slot), "inventory.getItemStack(slot)");
        ItemStack target = inventory.getItemStack(slot);
        if (item instanceof ItemStack) {
            return exactly ? Objects.equals(target, item) : target.isSimilar((ItemStack) item);
        } else {
            return false;
        }
    }

    public boolean isSupportedItem(@Nullable Object item) {
        return item == null || item instanceof ItemStack;
    }

    private void requireSupportedItem(Object item) {
        if (!this.isSupportedItem(item)) {
            Check.notNull(item, "item");
            throw new IllegalStateException("Unsupported item type: " + item.getClass().getName());
        }
    }

    public boolean hasItem(int slot) {
        return !this.inventory.getItemStack(slot).isAir();
    }

    public int getSize() {
        return this.inventory.getSize();
    }

    public int getSlotsCount() {
        return this.getSize() - 1;
    }

    public int getFirstSlot() {
        return 0;
    }

    public int getLastSlot() {
        int[] resultSlots = this.getType().getResultSlots();
        int lastSlot = getSlotsCount();
        if (resultSlots != null) {
            for (int resultSlot : resultSlots) {
                if (resultSlot == lastSlot) lastSlot--;
            }
        }
        return lastSlot;
    }

    public void changeTitle(@Nullable String title, @NotNull Viewer target) {
        MinestomViewer minestomViewer = (MinestomViewer) target;
        if (title == null) {
            changeTitle(Component.empty(), minestomViewer.getPlayer());
        }
    }

    public void changeTitle(@NotNull Component title, @NotNull Player target) {
        Check.notNull(title, "title");
        Check.notNull(target, "target");
        if (target.getOpenInventory() instanceof Inventory openInventory) {
            if (openInventory.getInventoryType() == InventoryType.CRAFTING || openInventory.getInventoryType() == InventoryType.CRAFTER_3X3) {
                return;
            }
            openInventory.setTitle(title);
        }
    }

    public boolean isEntityContainer() {
        return false;
    }

    public void open(@NotNull Viewer viewer) {
        Check.notNull(viewer, "viewer");
        viewer.open(this);
    }

    public void close() {
        inventory.getViewers().forEach(Player::closeInventory);
    }

    public void close(@NotNull Viewer viewer) {
        Check.notNull(viewer, "viewer");
        viewer.close();
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        } else if (other != null && Objects.equals(this.getClass(), other.getClass())) {
            MinestomViewContainer that = (MinestomViewContainer) other;
            return this.isShared == that.isShared && Objects.equals(this.inventory, that.inventory) && Objects.equals(this.getType(), that.getType());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(this.inventory, this.isShared, this.getType());
    }

    @NotNull
    public String toString() {
        return "MinestomViewContainer{inventory=" + this.inventory + ", shared=" + this.isShared + ", type=" + this.type + "}";
    }
}
