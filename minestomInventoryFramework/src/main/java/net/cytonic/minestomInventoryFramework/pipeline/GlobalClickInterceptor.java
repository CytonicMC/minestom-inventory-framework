package net.cytonic.minestomInventoryFramework.pipeline;

import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;

import net.cytonic.minestomInventoryFramework.context.SlotClickContext;

import static me.devnatan.inventoryframework.ViewConfig.CANCEL_ON_CLICK;

public final class GlobalClickInterceptor implements PipelineInterceptor<VirtualView> {

    public void intercept(@NotNull PipelineContext<VirtualView> pipeline, @NotNull VirtualView subject) {
        Check.notNull(pipeline, "pipeline");
        Check.notNull(subject, "subject");
        if (!(subject instanceof SlotClickContext context)) return;

        final InventoryPreClickEvent event = context.getClickOrigin();

        // inherit cancellation so we can un-cancel it
        context.setCancelled(event.isCancelled() || context.getConfig().isOptionSet(CANCEL_ON_CLICK, true));
        //noinspection OverrideOnly
        context.getRoot().onClick(context);
    }
}