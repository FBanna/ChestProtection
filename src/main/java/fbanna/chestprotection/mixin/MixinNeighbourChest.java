package fbanna.chestprotection.mixin;


import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.CheckProtected;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlock.class)
public class MixinNeighbourChest {

    @Inject(method = "getStateForPlacement", at = @At(value = "INVOKE", target = "defaultBlockState", shift = At.Shift.BEFORE))
    private void inject(final BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir, @Local(name = "facingDirection") Direction facingDirection, @Local(name = "type") LocalRef<ChestType> type) {

        CheckProtected cp = null;

        if (type.get() == ChestType.LEFT) {

            cp = CheckProtected.createContainerOpen(context.getClickedPos().relative(facingDirection.getClockWise()), context.getLevel());

        }

        if (type.get() == ChestType.RIGHT) {

            cp = CheckProtected.createContainerOpen(context.getClickedPos().relative(facingDirection.getCounterClockWise()), context.getLevel());

        }

        if(cp != null && !cp.isClear()) {
            type.set(ChestType.SINGLE);
        }

    }
}
