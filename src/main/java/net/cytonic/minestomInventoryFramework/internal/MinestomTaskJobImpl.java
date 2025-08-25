package net.cytonic.minestomInventoryFramework.internal;

import me.devnatan.inventoryframework.internal.Job;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MinestomTaskJobImpl implements Job {
    private final int intervalInTicks;
    @NotNull
    private final Runnable execution;
    @Nullable
    private Task task;

    public MinestomTaskJobImpl(int intervalInTicks, @NotNull Runnable execution) {
        Check.notNull(execution, "execution");
        this.intervalInTicks = intervalInTicks;
        this.execution = execution;
    }

    public boolean isStarted() {
        return this.task != null;
    }

    public void start() {
        if (isStarted()) return;
        TaskSchedule schedule = TaskSchedule.tick(this.intervalInTicks);
        this.task = MinecraftServer.getSchedulerManager().scheduleTask(this::loop, schedule, schedule);
    }

    public void cancel() {
        if (!isStarted()) return;
        task.cancel();
        task = null;
    }

    public void loop() {
        this.execution.run();
    }
}
