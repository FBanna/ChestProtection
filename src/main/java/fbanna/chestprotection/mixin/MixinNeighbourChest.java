package fbanna.chestprotection.mixin;

import fbanna.chestprotection.check.CheckChest;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.item.ItemPlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Objects;

@Mixin(ChestBlock.class)
public class MixinNeighbourChest {

    @ModifyVariable(method = "getPlacementState", at = @At(value = "STORE", ordinal = 3))
    private ChestType doubleChest(ChestType type, ItemPlacementContext ctx){


        CheckChest book = new CheckChest(ctx.getBlockPos().offset(ctx.getHorizontalPlayerFacing().getOpposite().rotateYCounterclockwise(),1), ctx.getWorld());

        if(book.chestStatus != CheckChest.status.CLEAR ){

            if(Objects.equals(book.author, Objects.requireNonNull(ctx.getPlayer()).getName().getString())){
                return ChestType.RIGHT;
            } else {
                return ChestType.SINGLE;
            }

        } else {
            return ChestType.RIGHT;
        }


    }

}
