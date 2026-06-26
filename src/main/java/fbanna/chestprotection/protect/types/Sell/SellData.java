package fbanna.chestprotection.protect.types.Sell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fbanna.chestprotection.ChestProtection;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.NonNullList;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.List;
import java.util.Optional;

public class SellData {

    public static final int PROFIT_INVENTORY_SIZE = 27;


    private Optional<TradeItem> cost;
    private Optional<TradeItem> product;
    private ProfitInventory profitInventory;

    public static final Codec<SellData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TradeItem.CODEC.optionalFieldOf("cost").forGetter(SellData::getCost),
            TradeItem.CODEC.optionalFieldOf("product").forGetter(SellData::getProduct),
            ItemContainerContents.CODEC.fieldOf("profitInventory").forGetter(SellData::getProfitInventoryContents)

    ).apply(instance, SellData::new));

    public SellData(Optional<TradeItem> cost, Optional<TradeItem> product, ItemContainerContents profitInventory){
        this.cost = cost;
        this.product = product;

        NonNullList<ItemStack> out = NonNullList.withSize(PROFIT_INVENTORY_SIZE, ItemStack.EMPTY);

        profitInventory.copyInto(out);
        this.profitInventory = new ProfitInventory( out.toArray(ItemStack[]::new));

    }

    public Optional<TradeItem> getCost() {
        return cost;
    }

    public Optional<TradeItem> getProduct() {
        return product;
    }

    public SimpleContainer getProfitInventory() {
        return profitInventory;
    }

    private ItemContainerContents getProfitInventoryContents() {
        return ItemContainerContents.fromItems(this.profitInventory.items);
    }
    public void setTradeItems(TradeItem[] tradeItems) {

        if (tradeItems.length != 2) {
            return;
        }

        this.cost = Optional.of(tradeItems[0]);
        this.product = Optional.of(tradeItems[1]);

//        SimpleContainer container = new SimpleContainer(5);
//
//        container.getSlotsFromRange(IntList.of(0,1,2,3,4));
//
//        ItemContainerContents.fromItems(container.items);
//
//        ValueOutput.TypedOutputList<ItemStack> output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
//
//        container.storeAsItemList(output);


    }

}
