package net.cytonic.minestomInventoryFramework;

import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.pipeline.StandardPipelinePhases;
import net.cytonic.minestomInventoryFramework.component.MinestomItemComponentBuilder;
import net.cytonic.minestomInventoryFramework.context.*;
import net.cytonic.minestomInventoryFramework.pipeline.CancelledCloseInterceptor;
import net.cytonic.minestomInventoryFramework.pipeline.GlobalClickInterceptor;
import net.cytonic.minestomInventoryFramework.pipeline.ItemClickInterceptor;
import net.cytonic.minestomInventoryFramework.pipeline.ItemCloseOnClickInterceptor;
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
