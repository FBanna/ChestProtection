package fbanna.chestprotection.protect.types.Sell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fbanna.chestprotection.protect.Authorised;
import net.minecraft.world.item.ItemStack;

public class TradeItem {

    public ItemStack stack;
    public int count;
    public boolean isItem;

    public static final Codec<TradeItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.fieldOf("stack").forGetter(TradeItem::getStack),
            Codec.INT.fieldOf("count").forGetter(TradeItem::getCount),
            Codec.BOOL.fieldOf("isItem").forGetter(TradeItem::isItem)
    ).apply(instance, TradeItem::new));


    public TradeItem(ItemStack stack, int count, boolean isItem) {
        this.stack = stack;
        this.count = count;
        this.isItem = isItem;
    }

    public int getCount() {
        return count;
    }

    public boolean isItem() {
        return isItem;
    }

    public ItemStack getStack() {
        return stack;
    }

}
