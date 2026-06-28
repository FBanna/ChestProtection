package fbanna.chestprotection.util;

import eu.pb4.sgui.api.gui.SimpleGui;
import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.types.Sell.Sell;
import fbanna.chestprotection.protect.types.Sell.TradeItem;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TradeInventoryUtils {

    public static final HashSet<DataComponentType<?>> COMPONENT_BLACK_LIST = new HashSet<>(
            Set.of(
                    DataComponents.ENCHANTABLE,
                    DataComponents.ITEM_MODEL,
                    DataComponents.ITEM_NAME,
                    DataComponents.MAX_DAMAGE,
                    DataComponents.MAX_STACK_SIZE,
                    DataComponents.USE_COOLDOWN,
                    DataComponents.USE_EFFECTS,
                    DataComponents.BREAK_SOUND,
                    DataComponents.REPAIR_COST,
                    DataComponents.REPAIRABLE,
                    DataComponents.SWING_ANIMATION,
                    DataComponents.RARITY,
                    DataComponents.LORE,
                    DataComponents.ATTRIBUTE_MODIFIERS,
                    DataComponents.TOOLTIP_DISPLAY,
                    DataComponents.ENCHANTMENT_GLINT_OVERRIDE

            )
    );


    /// isPresent (TradeItem) -> (boolean)
    public static boolean isPresent(Container container, TradeItem trade) {

        int count = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {

            ItemStack stack = container.getItem(i);

            if (trade.isItem && !stack.is(trade.stack.getItem())) {
                //ChestProtection.LOGGER.info("items dont match -> continuing");
                continue;
            }

            if(!stackMatches(trade, stack)) {
                continue;
            }

            count = count + stack.count();

        }

        if (count >= trade.getCount()) {
            return true;
        }

        return false;
    }

    private static boolean stackMatches(TradeItem trade, ItemStack stack) {


        for (TypedDataComponent<?> component: trade.stack.getComponents().filter(c -> !COMPONENT_BLACK_LIST.contains(c))) {

            if(!stack.getComponents().has(component.type())) {
                return false;
            }

            if (!stack.getComponents().get(component.type()).equals(component.value())) {
                //ChestProtection.LOGGER.info(component.type().toString() + stack.getItem().toString());
                return false;
            }



        }

        return true;
    }



    /// TradeInto (TradeItem, TradeInventory) -> ArrayList<ItemStack>
    /// returned array are the items that could not fit
    /// ensure proper canFit & isPresent checks are done before calling function

    public static ArrayList<ItemStack> tradeInto(Container container, TradeItem trade, Container into) {

        ArrayList<ItemStack> remainingStacks = new ArrayList<>();

        if (!isPresent(container, trade)) {
            return remainingStacks;
        }

        SimpleContainer simplified = (SimpleContainer) into;


//        if (!simplified.canAddItem(trade.stack)) {
//            return;
//        }

        int count = trade.getCount();

        for (int i = 0; i < container.getContainerSize(); i++) {

            ItemStack stack = container.getItem(i).copy();

            if (trade.isItem && !stack.is(trade.stack.getItem())) {
                continue;
            }

            if(!stackMatches(trade, stack)) {
                continue;
            }

            int attemptToMove = Math.min(count, stack.getCount());

            ItemStack remaining = simplified.addItem(stack.copyWithCount(attemptToMove));
            container.setItem(i, stack.copyWithCount(stack.getCount() - attemptToMove));

            count = count - attemptToMove;

            if(!remaining.isEmpty()) {
                remainingStacks.add(remaining);
            }

            if(count == 0) {
                break;
            }

        }

        assert count == 0;

        return remainingStacks;

    }
}
