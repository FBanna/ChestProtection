package fbanna.chestprotection.protect.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fbanna.chestprotection.protect.data.sell.SellData;

import java.util.Optional;

public class CPdata {

    // Stores & provides CODEC for:
    // Authorised players, profit inventory, trade items
    // aquired no matter the type but has options for sell specific fields

//    public Optional<ItemContainerContents> profitInventory;
    public Authorised authorised;
    public Optional<SellData> sellData;

    public static final Codec<CPdata> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Authorised.CODEC.fieldOf("authorised").forGetter(CPdata::getAuthorised),
            SellData.CODEC.optionalFieldOf("selldata").forGetter(CPdata::getSellData)
            //ItemContainerContents.CODEC.optionalFieldOf("profitInventory").forGetter(CPdata::getProfitInventory)

    ).apply(instance, CPdata::new));

    public CPdata(Authorised authorised, Optional<SellData> sellData){
        this.sellData = sellData;
        this.authorised = authorised;
    }

    public Optional<SellData> getSellData() {
        return this.sellData;
    }

    public Authorised getAuthorised() {
        return this.authorised;
    }

    public boolean isDataCorrect() {

        if (authorised.getAuthor() == null) {
            return false;
        }
        return true;
    }

    public boolean isSellDataCorrect() {

        if (this.sellData.isEmpty()) {
            return false;
        }

        SellData data = this.sellData.get();

        if(data.getCost().isEmpty() || data.getProduct().isEmpty()) {
            return false;
        }

//        if (data.getProfitInventory().allItemsCopyStream().count() != SellData.PROFIT_INVENTORY_SIZE) {
//            ChestProtection.LOGGER.error("incorrect size of profit inventory");
//            return false;
//        }

        return true;


    }

    @Override
    public boolean equals(Object o) {

        if(o == this) {
            return true;
        }

        if (!(o instanceof CPdata)) {
            return false;
        }

        CPdata other = (CPdata) o;

        if (!this.authorised.equals(other.authorised)) {
            return false;
        }

        if(!this.sellData.equals(other.sellData)) {
            return false;
        }

        return true;

    }


}
