package net.cytonic.minestomInventoryFramework.component;


import me.devnatan.inventoryframework.Ref;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.*;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.context.IFSlotContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.utils.SlotConverter;
import net.cytonic.minestomInventoryFramework.context.Context;
import net.cytonic.minestomInventoryFramework.context.SlotClickContext;
import net.cytonic.minestomInventoryFramework.context.SlotContext;
import net.cytonic.minestomInventoryFramework.context.SlotRenderContext;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class MinestomItemComponentBuilder extends DefaultComponentBuilder<MinestomItemComponentBuilder, Context>
        implements ItemComponentBuilder<MinestomItemComponentBuilder, Context>, ComponentFactory {
    @NotNull
    private final VirtualView root;
    private int slot;
    @Nullable
    private ItemStack item;
    @Nullable
    private Consumer<IFSlotRenderContext> renderHandler;
    @Nullable
    private Consumer<IFSlotClickContext> clickHandler;
    @Nullable
    private Consumer<IFSlotContext> updateHandler;

    private MinestomItemComponentBuilder(@NotNull VirtualView root, int slot, @Nullable ItemStack item, @Nullable Consumer<IFSlotRenderContext> renderHandler, @Nullable Consumer<IFSlotClickContext> clickHandler, @Nullable Consumer<IFSlotContext> updateHandler,
                                         Ref<Component> reference, Map<String, Object> data, boolean cancelOnClick,
                                         boolean closeOnClick, boolean updateOnClick, Set<State<?>> watchingStates, boolean isManagedExternally, Predicate<Context> displayCondition) {
        super(reference, data, cancelOnClick, closeOnClick, updateOnClick, watchingStates, isManagedExternally, displayCondition);
        this.root = root;
        this.slot = slot;
        this.item = item;
        this.renderHandler = renderHandler;
        this.clickHandler = clickHandler;
        this.updateHandler = updateHandler;
    }

    public MinestomItemComponentBuilder(@NotNull VirtualView root) {
        this(root, -1, null, null, null, null, null, new HashMap<>(), false, false, false, new LinkedHashSet<>(), false, null);
        Check.notNull(root, "root");
    }

    @NotNull
    public String toString() {
        int var10000 = this.slot;
        return "MinestomItemComponentBuilder{slot=" + var10000 + ", item=" + this.item + ", renderHandler=" + this.renderHandler + ", clickHandler=" + this.clickHandler + ", updateHandler=" + this.updateHandler + "} " + super.toString();
    }

    public boolean isContainedWithin(int position) {
        return position == this.slot;
    }

    @NotNull
    public MinestomItemComponentBuilder withSlot(int slot) {
        this.slot = slot;
        return this;
    }

    @NotNull
    public MinestomItemComponentBuilder withSlot(int row, int column) {
        ViewContainer container = ((IFRenderContext) (root)).getContainer();
        return this.withSlot(SlotConverter.convertSlot(row, column, container.getRowsCount(), container.getColumnsCount()));
    }

    @NotNull
    public MinestomItemComponentBuilder withItem(@Nullable ItemStack item) {
        this.item = item;
        return this;
    }

    @NotNull
    public MinestomItemComponentBuilder onRender(@Nullable Consumer<SlotRenderContext> renderHandler) {
        //noinspection unchecked
        this.renderHandler = (Consumer<IFSlotRenderContext>) (Consumer<?>) renderHandler;
        return this;
    }


    @NotNull
    public MinestomItemComponentBuilder renderWith(@NotNull final Supplier<ItemStack> renderFactory) {
        Check.notNull(renderFactory, "renderFactory");
        return this.onRender(render -> render.setItem(renderFactory.get()));
    }

    @NotNull
    public MinestomItemComponentBuilder onClick(@Nullable Consumer<SlotClickContext> clickHandler) {
        //noinspection unchecked
        this.clickHandler = (Consumer<IFSlotClickContext>) (Consumer<?>) clickHandler;
        return this;
    }

    @NotNull
    public MinestomItemComponentBuilder onClick(@Nullable Runnable clickHandler) {
        return this.onClick(click -> {
            if (clickHandler != null) {
                clickHandler.run();
            }
        });
    }

    @NotNull
    public MinestomItemComponentBuilder onUpdate(@Nullable Consumer<SlotContext> updateHandler) {
        //noinspection unchecked
        this.updateHandler = (Consumer<IFSlotContext>) (Consumer<?>) updateHandler;
        return this;
    }

    @NotNull
    public Component create() {
        return new ItemComponent(
                root,
                slot,
                item,
                cancelOnClick,
                closeOnClick,
                displayCondition,
                renderHandler,
                updateHandler,
                clickHandler,
                watchingStates,
                isManagedExternally,
                updateOnClick,
                false,
                reference);
    }

    @NotNull
    public MinestomItemComponentBuilder copy() {
        return new MinestomItemComponentBuilder(
                root,
                slot,
                item,
                renderHandler,
                clickHandler,
                updateHandler,
                reference,
                data,
                cancelOnClick,
                closeOnClick,
                updateOnClick,
                watchingStates,
                isManagedExternally,
                displayCondition);
    }
}
