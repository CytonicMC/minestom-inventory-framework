package net.cytonic.minestomInventoryFramework.context;

import me.devnatan.inventoryframework.context.IFConfinedContext;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Context extends IFConfinedContext {
    @NotNull
    Player getPlayer();

    @Experimental
    @NotNull
    List<Player> getAllPlayers();

    @Experimental
    void updateTitleForPlayer(@NotNull Component var1, @NotNull Player var2);

    @Experimental
    void resetTitleForPlayer(@NotNull Player var1);
}
