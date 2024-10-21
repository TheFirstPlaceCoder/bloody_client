package mixin;

import api.main.EventUtils;
import com.client.event.events.AttackEntityEvent;
import com.client.event.events.InteractItemEvent;
import com.client.event.events.ReachEvent;
import com.client.event.events.StartBreakingBlockEvent;
import com.client.impl.function.misc.NoBreakDelay;
import com.client.impl.function.misc.NoInteract;
import com.client.interfaces.IClickSlotC2SPacket;
import com.client.interfaces.IClientPlayerInteractionManager;
import com.client.system.friend.FriendManager;
import com.client.system.function.FunctionManager;
import net.minecraft.block.Block;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.client.BloodyClient.mc;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin implements IClientPlayerInteractionManager {
    @Shadow
    private int blockBreakingCooldown;

    @Redirect(method = "updateBlockBreakingProgress", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I", opcode = Opcodes.PUTFIELD))
    private void onMethod_2902SetField_3716Proxy(ClientPlayerInteractionManager interactionManager, int value) {
        if (FunctionManager.get(NoBreakDelay.class).isEnabled()) value = 0;
        blockBreakingCooldown = value;
    }

    @Redirect(method = "attackBlock", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I", opcode = Opcodes.PUTFIELD))
    private void onAttackBlockSetField_3719Proxy(ClientPlayerInteractionManager interactionManager, int value) {
        blockBreakingCooldown = value;
    }

    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    private void attackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        AttackEntityEvent.Pre event = new AttackEntityEvent.Pre(target);
        event.post();
        if (event.isCancelled() || !FriendManager.isAttackable(player)) {
            ci.cancel();
        }
    }

    @Inject(method = "attackEntity", at = @At("TAIL"), cancellable = true)
    private void attackEntityPost(PlayerEntity player, Entity target, CallbackInfo ci) {
        AttackEntityEvent.Post event = new AttackEntityEvent.Post(target);
        event.post();
        if (event.isCancelled() || !FriendManager.isAttackable(player)) {
            ci.cancel();
        }
    }

    @Inject(method = "attackBlock", at = @At("HEAD"), cancellable = true)
    private void onAttackBlock(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> info) {
        StartBreakingBlockEvent event = StartBreakingBlockEvent.get(blockPos, direction);
        event.post();
        if (event.isCancelled()) info.cancel();
    }

    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    private void interactBlock(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        Block bs = mc.world.getBlockState(hitResult.getBlockPos()).getBlock();
        if (FunctionManager.get(NoInteract.class).isEnabled() && FunctionManager.get(NoInteract.class).isValid(bs)) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }

    @Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
    private void onInteractItem(PlayerEntity player, World world, Hand hand, CallbackInfoReturnable<ActionResult> info) {
        InteractItemEvent event = InteractItemEvent.get(hand);
        event.post();
        if (event.toReturn != null) info.setReturnValue(event.toReturn);
    }

    @Inject(method = "getReachDistance", at = @At("HEAD"), cancellable = true)
    private void getReachDistanceHook(CallbackInfoReturnable<Float> cir) {
        ReachEvent event = new ReachEvent();
        EventUtils.post(event);
        if (event.isCancelled()) {
            cir.setReturnValue(event.distance);
        }
    }

    @Inject(method = "hasExtendedReach", at = @At("HEAD"), cancellable = true)
    private void hasExtendedReachHook(CallbackInfoReturnable<Boolean> cir) {
        ReachEvent event = new ReachEvent();
        EventUtils.post(event);
        if (event.isCancelled()) {
            cir.setReturnValue(true);
        }
    }

    @Shadow @Final
    private ClientPlayNetworkHandler networkHandler;

    @Override
    public void click(int syncId, int slotId, int clickData, SlotActionType actionType, PlayerEntity player, int id) {
        short s = player.currentScreenHandler.getNextActionId(player.inventory);
        ItemStack itemStack = player.currentScreenHandler.onSlotClick(slotId, clickData, actionType, player);
        ClickSlotC2SPacket packet = new ClickSlotC2SPacket(syncId, slotId, clickData, actionType, itemStack, s);
        ((IClickSlotC2SPacket) packet).setId(id);
        networkHandler.sendPacket(packet);
    }
}