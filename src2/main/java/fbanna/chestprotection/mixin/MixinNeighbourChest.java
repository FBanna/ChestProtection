package fbanna.chestprotection.mixin;

import fbanna.chestprotection.protect.CheckProtected;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Objects;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.properties.ChestType;

@Mixin(ChestBlock.class)
public class MixinNeighbourChest {

    @ModifyVariable(method = "getStateForPlacement", at = @At(value = "STORE", ordinal = 1))
    private ChestType doubleChest(ChestType type, BlockPlaceContext ctx){


        CheckProtected book = new CheckProtected(ctx.getClickedPos().relative(ctx.getHorizontalDirection().getOpposite().getCounterClockWise(),1), ctx.getLevel());

        if(book.chestStatus != CheckProtected.ProtectedStatus.CLEAR ){

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
