package fbanna.chestprotection.protect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.Container;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.Optional;

public class CPdata {

    // Stores & provides CODEC for:
    // Authorised players, profit inventory, trade items
    // aquired no matter the type but has options for sell specific fields

    public Optional<ItemContainerContents> profitInventory;
    public Authorised authorised;

    public static final Codec<CPdata> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Authorised.CODEC.fieldOf("authorised").forGetter(CPdata::getAuthorised),
            ItemContainerContents.CODEC.optionalFieldOf("profitInventory").forGetter(CPdata::getProfitInventory)

    ).apply(instance, CPdata::new));

    public CPdata(Authorised authorised, Optional<ItemContainerContents> profitInventory){
        this.profitInventory = profitInventory;
        this.authorised = authorised;
    }

    public Optional<ItemContainerContents> getProfitInventory() {
        return this.profitInventory;
    }

    public Authorised getAuthorised() {
        return this.authorised;
    }


}
