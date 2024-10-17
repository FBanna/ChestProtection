package fbanna.chestprotection.trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SaveItem {

    private final boolean isItem;
    private ItemStack stack;

    public static final Codec<SaveItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("isItem").forGetter(SaveItem::getIsItem),
            ItemStack.CODEC.fieldOf("stack").forGetter(SaveItem::getStack)
    ).apply(instance, SaveItem::new));

    public SaveItem(boolean isItem, ItemStack stack){
        this.isItem = isItem;
        this.stack = stack;

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
