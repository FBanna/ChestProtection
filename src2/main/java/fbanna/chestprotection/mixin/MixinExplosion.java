package fbanna.chestprotection.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import fbanna.chestprotection.protect.CheckProtected;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class MixinExplosion {

    @Inject(method = "shouldBlockExplode", at = @At("HEAD"), cancellable = true)
    private void prevent_explosion(Explosion explosion, BlockGetter world, BlockPos pos, BlockState state, float power, CallbackInfoReturnable<Boolean> cir) {
        if (state.getBlock() instanceof ChestBlock) {
            CheckProtected book = new CheckProtected(pos, explosion.level());
            if (book.chestStatus != CheckProtected.ProtectedStatus.CLEAR) {
                cir.setReturnValue(false);

            }
        }
    }
}