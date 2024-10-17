package fbanna.chestprotection.trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TradeItem {

    private final boolean isItem;
    private ItemStack stack;

    public static final Codec<TradeItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("isItem").forGetter(TradeItem::getIsItem),
            ItemStack.CODEC.fieldOf("stack").forGetter(TradeItem::getStack)
    ).apply(instance, TradeItem::new));

    public TradeItem(boolean isItem, ItemStack stack){
        this.isItem = isItem;
        this.stack = stack;

    }

    public TradeItem copy(){
        return new TradeItem(this.isItem, this.stack.copy());
    }

    public void setItem(Item item){
        this.stack = this.stack.withItem(item);
    }

    public boolean getIsItem() {
        return isItem;
    }

    public ItemStack getStack() {
        return stack;
    }
}
