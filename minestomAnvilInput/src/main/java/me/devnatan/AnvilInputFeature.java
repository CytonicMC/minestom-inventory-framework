package me.devnatan;

import me.devnatan.inventoryframework.*;
import me.devnatan.inventoryframework.context.*;
import me.devnatan.inventoryframework.feature.Feature;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import me.devnatan.inventoryframework.pipeline.StandardPipelinePhases;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateValue;
import me.devnatan.inventoryframework.state.StateValueHost;
import me.devnatan.inventoryframework.state.StateWatcher;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minestom.server.component.DataComponents;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.type.AnvilInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.UnaryOperator;

import static java.util.Objects.requireNonNull;
import static me.devnatan.AnvilInput.defaultConfig;
import static me.devnatan.inventoryframework.IFViewFrame.FRAME_REGISTERED;

@SuppressWarnings({"rawtypes", "unchecked", "unused"})
public final class AnvilInputFeature implements Feature<AnvilInputConfig, Void, ViewFrame> {

    private static final int INGREDIENT_SLOT = 0;

    /**
     * Instance of the Anvil Input feature.
     *
     * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/anvil-input">Anvil Input on Wiki</a>
     */
    public static final Feature<AnvilInputConfig, Void, ViewFrame> AnvilInput = new AnvilInputFeature();

    private AnvilInputConfig config;
    private PipelineInterceptor frameInterceptor;

    private AnvilInputFeature() {
    }

    @Override
    public @NotNull String name() {
        return "Anvil Input";
    }

    @Override
    public @NotNull Void install(ViewFrame framework, UnaryOperator<AnvilInputConfig> configure) {
        config = configure.apply(defaultConfig());
        framework.getPipeline().intercept(FRAME_REGISTERED, (frameInterceptor = createFrameworkInterceptor()));
        return null;
    }

    @Override
    public void uninstall(ViewFrame framework) {
        framework.getPipeline().removeInterceptor(FRAME_REGISTERED, frameInterceptor);
    }

    private PipelineInterceptor createFrameworkInterceptor() {
        return (PipelineInterceptor<IFViewFrame>) (pipeline, s) -> {
            ViewFrame subject = (ViewFrame) s;
            final Map<UUID, View> views = subject.getAllRegisteredViews();

            for (final View view : views.values()) {
                handleOpen(view);
                handleClose(view);
                handleClick(view);
            }
        };
    }

    private AnvilInput getAnvilInput(IFContext context) {
        if (context.getConfig().getType() != ViewType.ANVIL) return null;

        final Optional<ViewConfig.Modifier> optional = context.getConfig().getModifiers().stream()
                .filter(modifier -> modifier instanceof AnvilInput)
                .findFirst();

        //noinspection OptionalIsPresent
        if (optional.isEmpty()) return null;

        return (AnvilInput) optional.get();
    }

    private void updatePhysicalResult(String newText, ViewContainer container) {
        final Inventory inventory = ((MinestomViewContainer) container).inventory();
        ItemStack.Builder ingredientItem = requireNonNull(inventory.getItemStack(INGREDIENT_SLOT)).builder();
        ingredientItem.set(DataComponents.ITEM_NAME, Component.text(newText));
        inventory.setItemStack(INGREDIENT_SLOT, ingredientItem.build());
    }

    private void handleClick(PlatformView view) {
        view.getPipeline().intercept(StandardPipelinePhases.CLICK, (pipeline, subject) -> {
            if (!(subject instanceof IFSlotClickContext)) return;

            final SlotClickContext context = (SlotClickContext) subject;
            final AnvilInput anvilInput = getAnvilInput(context);
            if (anvilInput == null) return;

            final int resultSlot = context.getContainer().getType().getResultSlots()[0];
            if (context.getClickedSlot() != resultSlot) return;
            IFDebug.debug("context clicked slot: %d", context.getClickedSlot());

            final ItemStack resultItem = context.getItem();
            IFDebug.debug("result item: %s", resultItem);
            if (resultItem.material() == Material.AIR) return;

            final String text = PlainTextComponentSerializer.plainText().serialize(requireNonNull(resultItem.get(DataComponents.ITEM_NAME)));
            final Inventory clickedInventory =
                    (Inventory) requireNonNull(context.getClickOrigin().getInventory(), "Clicked inventory cannot be null");
            ItemStack ingredientItem = requireNonNull(clickedInventory.getItemStack(INGREDIENT_SLOT));

            ingredientItem = ingredientItem.with(DataComponents.ITEM_NAME, Component.text(text));
            context.updateState(anvilInput, text);
            clickedInventory.setItemStack(INGREDIENT_SLOT, ingredientItem);

            if (config.closeOnSelect) {
                context.closeForPlayer();
            }
        });
    }

    private void handleOpen(PlatformView view) {
        view.getPipeline().intercept(StandardPipelinePhases.OPEN, (pipeline, subject) -> {
            if (!(subject instanceof IFOpenContext)) return;

            final OpenContext context = (OpenContext) subject;
            final AnvilInput anvilInput = getAnvilInput(context);
            if (anvilInput == null) return;

            // Forces internal state initialization
            context.getInternalStateValue(anvilInput);
            IFDebug.debug("handle open");
            IFDebug.debug("watching state %d", anvilInput.internalId());
            context.watchState(anvilInput.internalId(), new StateWatcher() {
                @Override
                public void stateRegistered(@NotNull State<?> state, Object caller) {
                    IFDebug.debug("State registered");
                }

                @Override
                public void stateUnregistered(@NotNull State<?> state, Object caller) {
                    IFDebug.debug("State unregistered");
                }

                @Override
                public void stateValueGet(
                        @NotNull State<?> state,
                        @NotNull StateValueHost host,
                        @NotNull StateValue internalValue,
                        Object rawValue) {
                    IFDebug.debug("State value get: %s", rawValue);
                }

                @Override
                public void stateValueSet(
                        @NotNull StateValueHost host,
                        @NotNull StateValue value,
                        Object rawOldValue,
                        Object rawNewValue) {
                    IFDebug.debug("State value set: %s -> %s", rawOldValue, rawNewValue);
                    updatePhysicalResult((String) rawNewValue, ((IFRenderContext) host).getContainer());
                }
            });

            final String globalInitialInput = config.initialInput;
            final String scopedInitialInput = anvilInput.get(context);

            final Inventory inventory = openInventory(
                    context.getPlayer(),
                    context.getConfig().getTitle(),
                    scopedInitialInput.isEmpty() ? globalInitialInput : scopedInitialInput);
            final ViewContainer container =
                    new MinestomViewContainer(inventory, context.isShared(), ViewType.ANVIL, true);

            context.setContainer(container);
        });
    }

    private void handleClose(PlatformView view) {
        view.getPipeline().intercept(StandardPipelinePhases.CLOSE, (pipeline, subject) -> {
            if (!(subject instanceof IFCloseContext)) return;
            final CloseContext context = (CloseContext) subject;
            final AnvilInput anvilInput = getAnvilInput(context);
            if (anvilInput == null) return;

            final MinestomViewContainer container = (MinestomViewContainer) context.getContainer();
            final int slot = container.getType().getResultSlots()[0];
            final ItemStack item = container.inventory().getItemStack(slot);

            if (item.material() == Material.AIR) return;

            final String input = PlainTextComponentSerializer.plainText().serialize(requireNonNull(item.get(DataComponents.ITEM_NAME)));
            context.updateState(anvilInput, input);
        });
    }

    private Inventory openInventory(Player player, Object title, String initialInput) {
        try {
            Component component = Component.text(initialInput);
            if (title instanceof String string) {
                component = Component.text(string);
            } else if (title instanceof Component textComponent) {
                component = textComponent;
            }
            final AnvilInventory inventory = new AnvilInventory(component);

            ItemStack item = ItemStack.builder(Material.PAPER)
                    .set(DataComponents.DAMAGE, 0)
                    .set(DataComponents.ITEM_NAME, Component.text(initialInput)).build();
            inventory.setItemStack(0, item);
            player.openInventory(inventory);

            return inventory;
        } catch (Throwable throwable) {
            throw new RuntimeException("Something went wrong while opening Anvil Input inventory.", throwable);
        }
    }
}
