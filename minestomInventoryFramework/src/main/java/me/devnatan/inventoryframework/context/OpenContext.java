package me.devnatan.inventoryframework.context;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.cytonic.minestomInventoryFramework.MinestomViewer;
import net.cytonic.minestomInventoryFramework.context.Context;

public final class OpenContext extends PlatformConfinedContext implements IFOpenContext, Context {

    @NotNull
    private final View root;
    @Nullable
    private final Viewer subject;
    @NotNull
    private final Map<String, Viewer> viewers;
    @NotNull
    private final UUID id;
    @NotNull
    private final Player player;
    @Nullable
    private Object initialData;
    @Nullable
    private ViewContainer container;
    @Nullable
    private CompletableFuture<Void> waitTask;
    @Nullable
    private ViewConfigBuilder inheritedConfigBuilder;
    private boolean cancelled;

    @Internal
    public OpenContext(@NotNull View root, @Nullable Viewer subject, @NotNull Map<String, Viewer> viewers,
        @Nullable Object initialData) {
        Check.notNull(root, "root");
        Check.notNull(viewers, "viewers");
        this.root = root;
        this.subject = subject;
        this.viewers = viewers;
        this.initialData = initialData;
        this.id = UUID.randomUUID();
        Viewer viewer = this.subject;

        Check.notNull(viewer, "viewer");
        this.player = ((MinestomViewer) viewer).getPlayer();
    }

    @NotNull
    public Player getPlayer() {
        this.tryThrowDoNotWorkWithSharedContext("getAllPlayers()");
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
        this.tryThrowDoNotWorkWithSharedContext();
        this.modifyConfig().title(title);
    }

    public void resetTitleForPlayer(@NotNull Player player) {
        Check.notNull(player, "player");
        this.tryThrowDoNotWorkWithSharedContext();
        if (this.getModifiedConfig() != null) {
            this.modifyConfig().title(null);
        }
    }

    @Nullable
    public CompletableFuture<Void> getAsyncOpenJob() {
        return this.waitTask;
    }

    public void waitUntil(@NotNull CompletableFuture<Void> task) {
        Check.notNull(task, "task");
        this.waitTask = task;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @NotNull
    public ViewConfigBuilder modifyConfig() {
        if (this.inheritedConfigBuilder == null) this.inheritedConfigBuilder = new ViewConfigBuilder();

        Check.notNull(inheritedConfigBuilder, "inheritedConfigBuilder");
        return inheritedConfigBuilder;
    }

    @Nullable
    public ViewConfig getModifiedConfig() {
        if (this.inheritedConfigBuilder == null) return null;
        ViewConfigBuilder configBuilder = this.inheritedConfigBuilder;
        Check.notNull(configBuilder, "configBuilder");
        return configBuilder.build().merge(this.getRoot().getConfig());
    }

    @Nullable
    public ViewContainer getContainer() {
        return this.container;
    }

    public void setContainer(@NotNull ViewContainer container) {
        Check.notNull(container, "container");
        this.container = container;
    }

    @NotNull
    public View getRoot() {
        return this.root;
    }

    @NotNull
    public Map<String, Viewer> getIndexedViewers() {
        return this.viewers;
    }

    @NotNull
    public UUID getId() {
        return this.id;
    }

    @NotNull
    public ViewConfig getConfig() {
        if (inheritedConfigBuilder == null) {
            return getRoot().getConfig();
        }
        return inheritedConfigBuilder.build().merge(getRoot().getConfig());

    }

    @Nullable
    public Object getInitialData() {
        return this.initialData;
    }

    public void setInitialData(@Nullable Object initialData) {
        this.initialData = initialData;
    }

    @Nullable
    public Viewer getViewer() {
        this.tryThrowDoNotWorkWithSharedContext("getViewer");
        return this.subject;
    }
}
