package net.cytonic.minestomInventoryFramework;

import java.util.Objects;

import me.devnatan.inventoryframework.IFDebug;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.context.IFCloseContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.pipeline.StandardPipelinePhases;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.trait.EntityEvent;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.inventory.click.Click;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;

public final class IFInventoryListener {

    @NotNull
    private final ViewFrame viewFrame;

    public IFInventoryListener(@NotNull ViewFrame viewFrame) {
        Check.notNull(viewFrame, "viewFrame");
        this.viewFrame = viewFrame;
        IFDebug.debug("Registering IF listener");
        EventNode<@NotNull EntityEvent> node = EventNode.type("IF", EventFilter.ENTITY,
                (_, entity) -> entity instanceof Player && viewFrame.getViewer((Player) entity) != null)
            .setPriority(10).addListener(InventoryPreClickEvent.class, this::onInventoryClick)
            .addListener(InventoryCloseEvent.class, this::onInventoryClose);
        MinecraftServer.getGlobalEventHandler().addChild(node);
        IFDebug.debug("IF listener registered");
    }

    public void onInventoryClick(@NotNull InventoryPreClickEvent event) {
        Check.notNull(event, "event");
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        if (viewFrame.getViewer(player) == null) return;
        Viewer viewer = Objects.requireNonNull(viewFrame.getViewer(player));

        if (event.getClick() instanceof Click.DropSlot) {
            IFContext context = viewer.getActiveContext();
            if (!context.getConfig().isOptionSet(ViewConfig.CANCEL_ON_DROP)) return;

            event.setCancelled(context.getConfig().getOptionValue(ViewConfig.CANCEL_ON_DROP));
            return;
        }

        if (event.getClick() instanceof Click.LeftDrag || event.getClick() instanceof Click.RightDrag) {
            IFContext context = viewer.getActiveContext();
            if (!context.getConfig().isOptionSet(ViewConfig.CANCEL_ON_DRAG)) return;

            event.setCancelled(context.getConfig().getOptionValue(ViewConfig.CANCEL_ON_DRAG));
            return;
        }

        IFRenderContext context = viewer.getActiveContext();
        Component clickedComponent = context
            .getComponentsAt(event.getSlot())
            .stream()
            .filter(Component::isVisible)
            .findFirst()
            .orElse(null);

        ViewContainer clickedContainer =
            event.getInventory() instanceof PlayerInventory ? viewer.getSelfContainer() : context.getContainer();
        IFSlotClickContext clickContext = context.getRoot().getElementFactory().createSlotClickContext(
            event.getSlot(),
            viewer,
            clickedContainer,
            clickedComponent,
            event,
            false);

        context.getRoot().getPipeline().execute(StandardPipelinePhases.CLICK, clickContext);
    }

    public void onInventoryClose(@NotNull InventoryCloseEvent event) {
        Check.notNull(event, "event");
        Player player = event.getPlayer();
        Check.notNull(player, "player");
        Viewer viewer = this.viewFrame.getViewer(player);
        if (viewer == null) return;
        IFRenderContext context = viewer.getActiveContext();
        IFCloseContext closeContext = context.getRoot().getElementFactory().createCloseContext(viewer, context, event);
        context.getRoot().getPipeline().execute(StandardPipelinePhases.CLOSE, closeContext);
    }
}
