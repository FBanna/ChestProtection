package fbanna.chestprotection.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import fbanna.chestprotection.protect.CheckProtected;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WitherBoss.class)
public abstract class MixinWither {

    @ModifyExpressionValue(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/boss/wither/WitherBoss;canDestroy(Lnet/minecraft/world/level/block/state/BlockState;)Z"))

    private boolean canDestroy(boolean original, @Local BlockPos blockPos) {

        //World world = ((Entity) (Object) this).getWorld();
        Level world = ((Entity) (Object) this).level();

        CheckProtected book = new CheckProtected(blockPos, world);

        return original && (book.chestStatus == CheckProtected.ProtectedStatus.CLEAR);

    }
}
