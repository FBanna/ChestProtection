package fbanna.chestprotection.protect.data.sell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fbanna.chestprotection.ChestProtection;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.Optional;

public class SellData {

    public static final int PROFIT_INVENTORY_SIZE = 27;


    private Optional<TradeItem> cost;
    private Optional<TradeItem> product;
    private ItemContainerContents profitInventory;


    public static final Codec<SellData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TradeItem.CODEC.optionalFieldOf("cost").forGetter(SellData::getCost),
            TradeItem.CODEC.optionalFieldOf("product").forGetter(SellData::getProduct),
            ItemContainerContents.CODEC.fieldOf("profitInventory").forGetter(SellData::getProfitInventory)

    ).apply(instance, SellData::new));

    public SellData(Optional<TradeItem> cost, Optional<TradeItem> product, ItemContainerContents profitInventory){
        this.cost = cost;
        this.product = product;
        this.profitInventory = profitInventory;



//        NonNullList<ItemStack> out = NonNullList.withSize(PROFIT_INVENTORY_SIZE, ItemStack.EMPTY);
//
//        profitInventory.copyInto(out);
//        this.profitInventory = new ProfitInventory( out.toArray(ItemStack[]::new));


    }

    public Optional<TradeItem> getCost() {
        return cost;
    }

    public Optional<TradeItem> getProduct() {
        return product;
    }

    public ItemContainerContents getProfitInventory() {
        return profitInventory;
    }

    public void setProfitInventory(ItemContainerContents profitInventory) {
        this.profitInventory = profitInventory;
    }

    //    private ItemContainerContents getProfitInventoryContents() {
//        return ItemContainerContents.fromItems(this.profitInventory.items);
//    }
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


    @Override
    public boolean equals(Object o) {

        if(this == o) {
            return true;
        }

        if(!(o instanceof SellData)) {
            return false;
        }

        SellData other = (SellData) o;

        if (!this.cost.equals(other.cost)) {
            return false;
        }

        if(!this.product.equals(other.product)) {
            return false;
        }

        if(!this.profitInventory.equals(other.profitInventory)) {
            return false;
        }

        return true;
    }

}
