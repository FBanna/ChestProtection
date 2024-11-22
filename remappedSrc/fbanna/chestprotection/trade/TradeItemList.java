package fbanna.chestprotection.trade;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class TradeItemList implements Iterable<TradeItem>{

    private TradeItem[] tradeItems = new TradeItem[2];

    public TradeItemList(TradeItem[] items) {

        if(items.length != 2){
            throw new IllegalArgumentException("Needs to of length 2!");
        }

        this.tradeItems[0] = items[0];
        this.tradeItems[1] = items[1];

    }

    public TradeItem get(int i) {
        return this.tradeItems[i];
    }

    public TradeItem getCost(){
        return tradeItems[0];
    }

    public ItemStack getCostStack(){
        return tradeItems[0].getStack();
    }

    public boolean getCostIsItem(){
        return tradeItems[0].getIsItem();
    }

    public TradeItem getProduct(){
        return tradeItems[1];
    }

    public ItemStack getProductStack(){
        return tradeItems[1].getStack();
    }

    public boolean getProductIsItem(){
        return tradeItems[1].getIsItem();
    }

    @Override
    public @NotNull Iterator<TradeItem> iterator() {

        Iterator<TradeItem> it = new Iterator<TradeItem>() {

            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < 2;
            }

            @Override
            public TradeItem next() {
                return tradeItems[currentIndex++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return it;
    }

}
