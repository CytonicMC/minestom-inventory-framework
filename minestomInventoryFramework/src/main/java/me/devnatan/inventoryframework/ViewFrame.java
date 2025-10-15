package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.context.EndlessContextInfo;
import me.devnatan.inventoryframework.feature.DefaultFeatureInstaller;
import me.devnatan.inventoryframework.feature.Feature;
import me.devnatan.inventoryframework.feature.FeatureInstaller;
import me.devnatan.inventoryframework.internal.MinestomElementFactory;
import me.devnatan.inventoryframework.internal.PlatformUtils;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;

public final class ViewFrame extends IFViewFrame<ViewFrame, View> {
    @NotNull
    private static final Logger LOGGER;

    static {
        PlatformUtils.setFactory(new MinestomElementFactory());
        LOGGER = Logger.getLogger("IF");
    }

    private final FeatureInstaller<ViewFrame> featureInstaller;

    private ViewFrame() {
        this.featureInstaller = new DefaultFeatureInstaller<>(this);
    }

    public static ViewFrame create() {
        return new ViewFrame();
    }

    @NotNull
    public String open(@NotNull Class<? extends View> viewClass, @NotNull Player player) {
        Check.notNull(viewClass, "viewClass");
        Check.notNull(player, "player");
        return this.open(viewClass, player, null);
    }

    @NotNull
    public String open(@NotNull Class<? extends View> viewClass, @NotNull Player player, @Nullable Object initialData) {
        Check.notNull(viewClass, "viewClass");
        Check.notNull(player, "player");
        return this.open(viewClass, List.of(player), initialData);
    }

    @Experimental
    @NotNull
    public String open(@NotNull Class<? extends View> viewClass, @NotNull Collection<Player> players) {
        Check.notNull(viewClass, "viewClass");
        Check.notNull(players, "players");
        return this.open(viewClass, players, null);
    }

    @Experimental
    @NotNull
    public String open(@NotNull Class<? extends View> viewClass, @NotNull Collection<Player> players, @Nullable Object initialData) {
        Check.notNull(viewClass, "viewClass");
        Check.notNull(players, "players");
        Map<String, Player> map = new HashMap<>();
        for (Player player : players) {
            map.put(player.getUuid().toString(), player);
        }

        String id = this.internalOpen(viewClass, map, initialData);
        Check.notNull(id, "id");
        return id;
    }

    @Experimental
    public void openActive(@NotNull Class<? extends View> viewClass, @NotNull String contextId, @NotNull Player player) {
        Check.notNull(viewClass, "viewClass");
        Check.notNull(contextId, "contextId");
        Check.notNull(player, "player");
        this.openActive(viewClass, contextId, player, null);
    }

    @Experimental
    public void openActive(@NotNull Class<? extends View> viewClass, @NotNull String contextId, @NotNull Player player, @Nullable Object initialData) {
        Check.notNull(viewClass, "viewClass");
        Check.notNull(contextId, "contextId");
        Check.notNull(player, "player");
        this.internalOpenActiveContext(viewClass, contextId, player, initialData);
    }

    @Experimental
    public void openEndless(@NotNull EndlessContextInfo endlessContextInfo, @NotNull Player player) {
        Check.notNull(endlessContextInfo, "endlessContextInfo");
        Check.notNull(player, "player");
        this.openEndless(endlessContextInfo, player, null);
    }

    @Experimental
    public void openEndless(@NotNull EndlessContextInfo endlessContextInfo, @NotNull Player player, @Nullable Object initialData) {
        Check.notNull(endlessContextInfo, "endlessContextInfo");
        Check.notNull(player, "player");
        //noinspection unchecked
        Class<? extends View> clazz = (Class<? extends View>) endlessContextInfo.getView().getClass();
        Check.notNull(clazz, "clazz");
        String contextId = endlessContextInfo.getContextId();
        Check.notNull(contextId, "contextId");
        this.openActive(clazz, contextId, player, initialData);
    }

    @NotNull
    public ViewFrame register() {
        if (this.isRegistered()) throw new IllegalStateException("This view frame is already registered");
        this.setRegistered(true);
        PlatformUtils.setFactory(new MinestomElementFactory());
        this.getPipeline().execute(FRAME_REGISTERED, this);
        this.initializeViews();
        new IFInventoryListener(this);
        return this;
    }

    public void unregister() {
        if (!this.isRegistered()) return;
        this.setRegistered(false);

        for (Iterator<View> iterator = this.registeredViews.values().iterator(); iterator.hasNext(); iterator.remove()) {
            View view = iterator.next();

            try {
                view.closeForEveryone();
            } catch (RuntimeException ignored) {
            }
        }
        this.getPipeline().execute(FRAME_UNREGISTERED, this);
    }

    /**
     * All registered views.
     *
     * @return A Map containing all registered views in this view frame.
     */
    public @NotNull @UnmodifiableView Map<UUID, View> getAllRegisteredViews() {
        return Collections.unmodifiableMap(registeredViews);
    }

    private void initializeViews() {
        getRegisteredViews().forEach((key, view) -> {
            try {
                view.internalInitialization(this);
                view.setInitialized(true);
            } catch (RuntimeException exception) {
                view.setInitialized(false);
                LOGGER.severe(
                        String.format(
                                "An error occurred while enabling view %s: %s",
                                view.getClass().getName(),
                                exception
                        )
                );
                //noinspection CallToPrintStackTrace
                exception.printStackTrace();
            }
        });
    }

    @Internal
    @Nullable
    public Viewer getViewer(@NotNull Player player) {
        Check.notNull(player, "player");
        return this.viewerById.get(player.getUuid().toString());
    }

    @NotNull
    public <C, R> ViewFrame install(
            Feature<C, R, ViewFrame> feature,
            UnaryOperator<C> configure
    ) {
        featureInstaller.install(feature, configure);
        IFDebug.debug("Feature %s installed", feature.name());
        return this;
    }

    @NotNull
    public ViewFrame install(@NotNull Feature<?, ?, ViewFrame> feature) {
        this.install(feature, UnaryOperator.identity());
        return this;
    }
}
