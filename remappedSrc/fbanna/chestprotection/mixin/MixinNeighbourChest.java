package fbanna.chestprotection.mixin;

import fbanna.chestprotection.check.CheckChest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Objects;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.properties.ChestType;

@Mixin(ChestBlock.class)
public class MixinNeighbourChest {

    @ModifyVariable(method = "getStateForPlacement", at = @At(value = "STORE", ordinal = 3))
    private ChestType doubleChest(ChestType type, BlockPlaceContext ctx){


        CheckChest book = new CheckChest(ctx.getClickedPos().relative(ctx.getHorizontalDirection().getOpposite().getCounterClockWise(),1), ctx.getLevel());

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
