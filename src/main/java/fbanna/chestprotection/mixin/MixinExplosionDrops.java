package fbanna.chestprotection.mixin;

import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.CheckProtected;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.BiConsumer;

@Mixin(BlockBehaviour.class)
public class MixinExplosionDrops {



    /// Prevents block drops from spawning
    @Inject(method = "onExplosionHit", at = @At(value = "HEAD"), cancellable = true)
    private void inject(final BlockState state, final ServerLevel level, final BlockPos pos, final Explosion explosion, final BiConsumer<ItemStack, BlockPos> onHit, CallbackInfo ci) {

        CheckProtected cp = CheckProtected.createContainerOpen(pos, level);

        if (!cp.isClear()){
            ci.cancel();
        }

    }

}
