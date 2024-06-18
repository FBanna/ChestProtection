package fbanna.chestprotection.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import fbanna.chestprotection.check.CheckChest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WitherEntity.class)
public abstract class MixinBreak {

    @ModifyExpressionValue(method = "mobTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/WitherEntity;canDestroy(Lnet/minecraft/block/BlockState;)Z"))

    private boolean canDestroy(boolean original, @Local BlockPos blockPos) {

        World world = ((Entity) (Object) this).getWorld();

        CheckChest book = new CheckChest(blockPos, world);

        return original && (book.chestStatus == CheckChest.status.CLEAR);

    }
}
