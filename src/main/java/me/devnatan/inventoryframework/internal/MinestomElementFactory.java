package me.devnatan.inventoryframework.internal;

import me.devnatan.inventoryframework.*;
import me.devnatan.inventoryframework.component.ComponentBuilder;
import me.devnatan.inventoryframework.component.MinestomItemComponentBuilder;
import me.devnatan.inventoryframework.context.*;
import me.devnatan.inventoryframework.logging.Logger;
import me.devnatan.inventoryframework.logging.NoopLogger;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class MinestomElementFactory extends ElementFactory {
    @NotNull
    private static final ViewType defaultType = ViewType.CHEST;

    private static @NotNull InventoryType getInventoryType(ViewType finalType, int size) {
        InventoryType type;
        if (Objects.equals(finalType, ViewType.CHEST)) {
            switch (size / finalType.getColumns()) {
                case 1 -> type = InventoryType.CHEST_1_ROW;
                case 2 -> type = InventoryType.CHEST_2_ROW;
                case 3 -> type = InventoryType.CHEST_3_ROW;
                case 4 -> type = InventoryType.CHEST_4_ROW;
                case 5 -> type = InventoryType.CHEST_5_ROW;
                default -> type = InventoryType.CHEST_6_ROW;
            }
        } else if (Objects.equals(finalType, ViewType.BEACON)) {
            type = InventoryType.BEACON;
        } else if (Objects.equals(finalType, ViewType.HOPPER)) {
            type = InventoryType.HOPPER;
        } else if (Objects.equals(finalType, ViewType.SMOKER)) {
            type = InventoryType.SMOKER;
        } else if (Objects.equals(finalType, ViewType.BLAST_FURNACE)) {
            type = InventoryType.BLAST_FURNACE;
        } else if (Objects.equals(finalType, ViewType.FURNACE)) {
            type = InventoryType.FURNACE;
        } else if (Objects.equals(finalType, ViewType.ANVIL)) {
            type = InventoryType.ANVIL;
        } else if (Objects.equals(finalType, ViewType.CRAFTING_TABLE)) {
            type = InventoryType.CRAFTING;
        } else if (Objects.equals(finalType, ViewType.DROPPER)) {
            type = InventoryType.WINDOW_3X3;
        } else if (Objects.equals(finalType, ViewType.BREWING_STAND)) {
            type = InventoryType.BREWING_STAND;
        } else if (Objects.equals(finalType, ViewType.SHULKER_BOX)) {
            type = InventoryType.SHULKER_BOX;
        } else {
            throw new IllegalStateException("Unsupported type: " + finalType);
        }
        return type;
    }

    @NotNull
    public RootView createUninitializedRoot() {
        return new View();
    }

    @NotNull
    public ViewContainer createContainer(@NotNull IFContext context) {
        Check.notNull(context, "context");
        ViewConfig config = context.getConfig();
        Check.notNull(config, "config");
        ViewType configType = config.getType();
        if (configType == null) {
            configType = defaultType;
        }

        ViewType finalType = configType;
        int size = finalType.normalize(config.getSize());
        if (size != 0 && !finalType.isExtendable()) {
            throw new IllegalArgumentException(
                    String.format(
                            "Only \"%s\" type can have a custom size," +
                                    " \"%s\" always have a size of %d. Remove the parameter that specifies the size" +
                                    " of the container on %s or just set the type explicitly.",
                            ViewType.CHEST.getIdentifier(),
                            finalType.getIdentifier(),
                            finalType.getMaxSize(),
                            context.getRoot().getClass().getName()
                    )
            );
        }
        InventoryType type = getInventoryType(finalType, size);

        Component title;
        if (config.getTitle() instanceof Component) {
            title = (Component) config.getTitle();
        } else {
            title = Component.empty();
        }
        Inventory inventory = new Inventory(type, title);
        return new MinestomViewContainer(inventory, false, finalType, false);
    }

    @NotNull
    public Viewer createViewer(@NotNull Object entity, @Nullable IFRenderContext context) {
        Check.notNull(entity, "entity");
        if (!(entity instanceof Player)) {
            throw new IllegalArgumentException("createViewer(...) first parameter must be a Player");
        }
        return new MinestomViewer((Player) entity, context);
    }

    @Override
    public IFOpenContext createOpenContext(@NotNull RootView root, Viewer subject, List<Viewer> viewers, Object initialData) {
        return new OpenContext((View) root, subject, viewers.stream().collect(Collectors.toMap(Viewer::getId, Function.identity())), initialData);
    }


    @NotNull
    public IFRenderContext createRenderContext(@NotNull UUID id, @NotNull RootView root, @NotNull ViewConfig config, @Nullable ViewContainer container, @NotNull Map<String, Viewer> viewers, @NotNull Viewer subject, @Nullable Object initialData) {
        Check.notNull(id, "id");
        Check.notNull(root, "root");
        Check.notNull(config, "config");
        Check.notNull(viewers, "viewers");
        Check.notNull(subject, "subject");
        return new RenderContext(id, (View) root, config, container, viewers, subject, initialData);
    }

    @NotNull
    public IFSlotClickContext createSlotClickContext(int slotClicked, @NotNull Viewer whoClicked, @NotNull ViewContainer interactionContainer, @Nullable me.devnatan.inventoryframework.component.Component componentClicked, @NotNull Object origin, boolean combined) {
        Check.notNull(whoClicked, "whoClicked");
        Check.notNull(interactionContainer, "interactionContainer");
        Check.notNull(origin, "origin");
        IFRenderContext activeContext = whoClicked.getActiveContext();
        Check.notNull(activeContext, "activeContext");
        return new SlotClickContext(slotClicked, activeContext, whoClicked, interactionContainer, componentClicked, (InventoryPreClickEvent) origin, combined);
    }

    @NotNull
    public IFSlotRenderContext createSlotRenderContext(int slot, @NotNull IFRenderContext parent, @Nullable Viewer viewer) {
        Check.notNull(parent, "parent");
        return new SlotRenderContext(slot, parent, viewer);
    }

    @NotNull
    public IFCloseContext createCloseContext(@NotNull Viewer viewer, @NotNull IFRenderContext parent, @NotNull Object origin) {
        Check.notNull(viewer, "viewer");
        Check.notNull(parent, "parent");
        Check.notNull(origin, "origin");
        return new CloseContext(viewer, parent, (InventoryCloseEvent) origin);
    }

    @NotNull
    public ComponentBuilder<?, Context> createComponentBuilder(@NotNull VirtualView root) {
        Check.notNull(root, "root");
        return new MinestomItemComponentBuilder(root);
    }

    public boolean worksInCurrentPlatform() {
        return true;
    }

    @NotNull
    public Logger getLogger() {
        return new NoopLogger();
    }

    @NotNull
    public Job scheduleJobInterval(@NotNull RootView root, long intervalInTicks, @NotNull Runnable execution) {
        Check.notNull(root, "root");
        Check.notNull(execution, "execution");
        return new MinestomTaskJobImpl((int) intervalInTicks, execution);
    }
}
