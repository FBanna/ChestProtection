package fbanna.chestprotection.protect.types.Sell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fbanna.chestprotection.ChestProtection;
import net.minecraft.world.Container;
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

    public void setTradeItems(TradeItem[] tradeItems) {

        if (tradeItems.length != 2) {
            return;
        }

        this.cost = Optional.of(tradeItems[0]);
        this.product = Optional.of(tradeItems[1]);


    }

}
