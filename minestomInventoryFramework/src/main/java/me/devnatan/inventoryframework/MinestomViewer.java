package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.context.IFRenderContext;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;

public final class MinestomViewer implements Viewer {
    @NotNull
    private final Player player;
    @NotNull
    private final Deque<IFRenderContext> previousContexts;
    @Nullable
    private IFRenderContext activeContext;
    @Nullable
    private ViewContainer selfContainer;
    private long lastInteractionInMillis;
    private boolean switching;

    public MinestomViewer(@NotNull Player player, @Nullable IFRenderContext activeContext) {
        Check.notNull(player, "player");
        this.player = player;
        this.activeContext = activeContext;
        this.previousContexts = new LinkedList<>();
    }

    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    @NotNull
    public IFRenderContext getCurrentContext() {
        if (this.isSwitching()) {
            IFRenderContext renderContext;
            renderContext = this.getPreviousContext();
            if (renderContext == null) {
                throw new IllegalStateException("Previous context cannot be null when switching");
            }
            return renderContext;
        } else {
            return this.getActiveContext();
        }

    }

    @NotNull
    public IFRenderContext getActiveContext() {
        Check.notNull(activeContext, "activeContext");
        return activeContext;
    }

    public void setActiveContext(@NotNull IFRenderContext context) {
        Check.notNull(context, "context");
        this.activeContext = context;
    }

    @NotNull
    public String getId() {
        Check.notNull(player.getUuid().toString(), "getId");
        return player.getUuid().toString();
    }

    public void open(@NotNull ViewContainer container) {
        Check.notNull(container, "container");
        this.player.openInventory(((MinestomViewContainer) container).inventory());
    }

    public void close() {
        this.player.closeInventory();
    }

    @NotNull
    public ViewContainer getSelfContainer() {
        if (this.selfContainer == null) {
            AbstractInventory abstractInventory = this.player.getOpenInventory();
            Check.notNull(abstractInventory, "abstractInventory");
            Inventory inventory = (Inventory) abstractInventory;
            boolean shared = this.getActiveContext().isShared();
            Check.notNull(player, "PLAYER");
            this.selfContainer = new MinestomViewContainer(inventory, shared, ViewType.PLAYER, false);
        }

        Check.notNull(selfContainer, "container");
        return selfContainer;
    }

    public long getLastInteractionInMillis() {
        return this.lastInteractionInMillis;
    }

    public void setLastInteractionInMillis(long lastInteractionInMillis) {
        this.lastInteractionInMillis = lastInteractionInMillis;
    }

    public boolean isBlockedByInteractionDelay() {
        IFRenderContext renderContext = this.activeContext;
        if (renderContext != null) {
            ViewConfig var3 = renderContext.getConfig();
            long configuredDelay = var3.getInteractionDelayInMillis();
            if (configuredDelay > 0L && this.getLastInteractionInMillis() > 0L) {
                return this.getLastInteractionInMillis() + configuredDelay >= System.currentTimeMillis();
            }
            return false;
        }
        return false;
    }

    public boolean isSwitching() {
        return this.switching;
    }

    public void setSwitching(boolean switching) {
        this.switching = switching;
    }

    @Nullable
    public IFRenderContext getPreviousContext() {
        return this.previousContexts.peekLast();
    }

    public void setPreviousContext(@NotNull IFRenderContext previousContext) {
        Check.notNull(previousContext, "previousContext");
        this.previousContexts.pollLast();
        this.previousContexts.add(previousContext);
    }

    @NotNull
    public Object getPlatformInstance() {
        return this.player;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        } else if (o != null && Objects.equals(this.getClass(), o.getClass())) {
            MinestomViewer that = (MinestomViewer) o;
            return Objects.equals(this.player, that.player);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.player.hashCode();
    }

    @NotNull
    public String toString() {
        return "MinestomViewer{player=" + this.player + ", selfContainer=" + this.selfContainer + ", lastInteractionInMillis=" + this.lastInteractionInMillis + ", isSwitching=" + this.switching + "}";
    }
}
