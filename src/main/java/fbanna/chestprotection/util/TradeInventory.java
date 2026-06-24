package fbanna.chestprotection.util;

import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.types.Sell.TradeItem;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueOutput;

public class TradeInventory extends SimpleContainer {

    public TradeInventory(int size) {
        super(size);
    }

    public TradeInventory(Container container) {

        this(container.getContainerSize());

        for(int i = 0; i < container.getContainerSize(); i++) {
            this.setItem(i, container.getItem(i));
        }

    }
    /// isPresent (TradeItem) -> (boolean)
    public boolean isPresent(TradeItem trade) {

        int count = 0;

        for (ItemStack stack: this.items) {

            if (trade.isItem && !stack.is(trade.stack.getItem())) {
                ChestProtection.LOGGER.info("items dont match -> continuing");
                continue;
            }

            if(!stackMatches(trade, stack)) {
                continue;
            }

            count = count + stack.count();

        }

        if (trade.getCount() >= count) {
            return true;
        }

        return false;
    }

    private boolean stackMatches(TradeItem trade, ItemStack stack) {
        for (TypedDataComponent<?> component: trade.stack.getComponents()) {

            if (!stack.getComponents().get(component.type()).equals(component.value())) {
                return false;
            }

        }

        return true;
    }

    public void writeTo(){

        TagValueOutput output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        ContainerHelper.saveAllItems(
                output,
                this.items
        );

        ChestProtection.LOGGER.info(output.buildResult().toString());


    }


    /// TradeInto (TradeItem, TradeInventory) -> (boolean)
}
