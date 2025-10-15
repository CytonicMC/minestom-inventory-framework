package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.CloseContext;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;

public final class CancelledCloseInterceptor implements PipelineInterceptor<VirtualView> {

    public void intercept(@NotNull PipelineContext<VirtualView> pipeline, @NotNull VirtualView subject) {
        Check.notNull(pipeline, "pipeline");
        Check.notNull(subject, "subject");
        if (subject instanceof CloseContext) {
            if (((CloseContext) subject).isCancelled()) {
                ((CloseContext) subject).getRoot().nextTick(() -> ((CloseContext) subject).getViewer().open(((CloseContext) subject).getContainer()));
            }
        }
    }
}
