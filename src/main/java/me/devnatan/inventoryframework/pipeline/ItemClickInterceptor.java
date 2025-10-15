package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ItemComponent;
import me.devnatan.inventoryframework.context.SlotClickContext;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;

public final class ItemClickInterceptor implements PipelineInterceptor<VirtualView> {
    public void intercept(@NotNull PipelineContext<VirtualView> pipeline, @NotNull VirtualView subject) {
        Check.notNull(pipeline, "pipeline");
        Check.notNull(subject, "subject");
        if (subject instanceof SlotClickContext) {
            Component component = ((SlotClickContext) subject).getComponent();
            if (component != null) {
                if (component instanceof ItemComponent item) {
                    ((SlotClickContext) subject).setCancelled(item.isCancelOnClick());
                }
            }
        }
    }
}
