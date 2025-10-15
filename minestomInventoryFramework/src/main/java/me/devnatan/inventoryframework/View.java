package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.component.MinestomItemComponentBuilder;
import me.devnatan.inventoryframework.context.*;
import me.devnatan.inventoryframework.pipeline.*;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.OverrideOnly
public class View extends PlatformView<
        ViewFrame,
        Player,
        MinestomItemComponentBuilder,
        Context,
        OpenContext,
        CloseContext,
        RenderContext,
        SlotClickContext> {
    @Override
    public void registerPlatformInterceptors() {
        Pipeline<VirtualView> pipeline = getPipeline();
        Check.notNull(pipeline, "getPipeline(...)");
        pipeline.intercept(StandardPipelinePhases.CLICK, new ItemClickInterceptor());
        pipeline.intercept(StandardPipelinePhases.CLICK, new GlobalClickInterceptor());
        pipeline.intercept(StandardPipelinePhases.CLICK, new ItemCloseOnClickInterceptor());
        pipeline.intercept(StandardPipelinePhases.CLOSE, new CancelledCloseInterceptor());
    }

    public void nextTick(@NotNull Runnable task) {
        Check.notNull(task, "task");
        MinecraftServer.getSchedulerManager().scheduleNextTick(task);
    }
}
