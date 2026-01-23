package me.devnatan.inventoryframework.context;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.cytonic.minestomInventoryFramework.MinestomViewContainer;
import net.cytonic.minestomInventoryFramework.MinestomViewer;
import net.cytonic.minestomInventoryFramework.component.MinestomItemComponentBuilder;
import net.cytonic.minestomInventoryFramework.context.Context;

public final class RenderContext extends PlatformRenderContext<MinestomItemComponentBuilder, Context> implements
    Context {

    @NotNull
    private final Player player;

    @Internal
    public RenderContext(@NotNull UUID id, @NotNull View root, @NotNull ViewConfig config,
        @Nullable ViewContainer container, @NotNull Map<String, Viewer> viewers, @NotNull Viewer subject,
        @Nullable Object initialData) {
        super(id, root, config, container, viewers, subject, initialData);
        Check.notNull(id, "id");
        Check.notNull(root, "root");
        Check.notNull(config, "config");
        Check.notNull(viewers, "viewers");
        Check.notNull(subject, "subject");
        this.player = ((MinestomViewer) subject).getPlayer();
    }

    @NotNull
    public Player getPlayer() {
        this.tryThrowDoNotWorkWithSharedContext("getAllPlayers");
        return this.player;
    }

    @NotNull
    public List<Player> getAllPlayers() {
        List<Player> list = this.getViewers().stream().map(viewer -> ((MinestomViewer) viewer).getPlayer()).toList();
        Check.notNull(list, "list");
        return list;
    }

    public void updateTitleForPlayer(@NotNull Component title, @NotNull Player player) {
        Check.notNull(title, "title");
        Check.notNull(player, "player");
        ViewContainer container = this.getContainer();
        Check.notNull(container, "container");
        ((MinestomViewContainer) container).changeTitle(title, player);
    }

    public void resetTitleForPlayer(@NotNull Player player) {
        Check.notNull(player, "player");
        Check.notNull(this.getContainer(), "container");
        ((MinestomViewContainer) getContainer()).changeTitle(Component.empty(), player);
    }

    @NotNull
    public View getRoot() {
        return (View) root;
    }

    @NotNull
    public String toString() {
        return "RenderContext{player=" + getPlayer() + "} " + super.toString();
    }

    @NotNull
    public MinestomItemComponentBuilder slot(int slot, @NotNull ItemStack item) {
        Check.notNull(item, "item");
        return this.slot(slot).withItem(item);
    }

    @NotNull
    public MinestomItemComponentBuilder slot(int row, int column, @Nullable ItemStack item) {
        return this.slot(row, column).withItem(item);
    }

    @NotNull
    public MinestomItemComponentBuilder firstSlot(@Nullable ItemStack item) {
        return this.firstSlot().withItem(item);
    }

    @NotNull
    public MinestomItemComponentBuilder lastSlot(@Nullable ItemStack item) {
        return this.lastSlot().withItem(item);
    }

    @NotNull
    public MinestomItemComponentBuilder availableSlot(@Nullable ItemStack item) {
        return this.availableSlot().withItem(item);
    }

    @NotNull
    public MinestomItemComponentBuilder layoutSlot(char character, @Nullable ItemStack item) {
        return this.layoutSlot(character).withItem(item);
    }

    @Experimental
    @NotNull
    public MinestomItemComponentBuilder resultSlot(@Nullable ItemStack item) {
        return this.resultSlot().withItem(item);
    }

    @NotNull
    protected MinestomItemComponentBuilder createBuilder() {
        return new MinestomItemComponentBuilder(this);
    }

    public int hashCode() {
        return Objects.hash(super.hashCode(), this.getPlayer());
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        } else if (other != null && Objects.equals(this.getClass(), other.getClass())) {
            if (!super.equals(other)) {
                return false;
            } else {
                RenderContext that = (RenderContext) other;
                return Objects.equals(this.getPlayer(), that.getPlayer());
            }
        } else {
            return false;
        }
    }
}