package fbanna.chestprotection.protect.data.sell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
        return this.count;
    }

    public boolean isItem() {
        return this.isItem;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public ItemStack copyStackWithCount() {
        return this.stack.copyWithCount(this.count);
    }


    @Override
    public boolean equals(Object o) {

        if(this == o) {
            return true;
        }

        if(!(o instanceof TradeItem)) {
            return false;
        }

        TradeItem other = (TradeItem) o;

        if (!ItemStack.isSameItemSameComponents(this.stack, other.stack)) {
            return false;
        }

        if(this.count != other.count) {
            return false;
        }

        if(this.isItem != other.isItem) {
            return false;
        }

        return true;
    }


}
