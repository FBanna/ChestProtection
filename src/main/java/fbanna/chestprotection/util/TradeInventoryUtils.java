package fbanna.chestprotection.util;

import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.types.Sell.TradeItem;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class TradeInventoryUtils {

//    public TradeInventory(int size) {
//        super(size);
//    }
//
//    public TradeInventory(Container container) {
//
//        this(container.getContainerSize());
//
//        for(int i = 0; i < container.getContainerSize(); i++) {
//            this.setItem(i, container.getItem(i));
//        }
//
//    }
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
        for (TypedDataComponent<?> component: trade.stack.getComponents()) {

            if (!stack.getComponents().get(component.type()).equals(component.value())) {
                return false;
            }
        }

        return true;
    }

//    public void writeTo(){
//
//        TagValueOutput output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
//        ContainerHelper.saveAllItems(
//                output,
//                this.items
//        );
//
//        ChestProtection.LOGGER.info(output.buildResult().toString());
//
//
//    }


    /// TradeInto (TradeItem, TradeInventory) -> (boolean)
}
