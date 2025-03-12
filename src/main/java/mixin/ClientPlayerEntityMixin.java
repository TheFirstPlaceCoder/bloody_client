package mixin;

import com.client.event.events.*;
import com.client.impl.function.combat.Helper;
import com.client.impl.function.movement.NoPush;
import com.client.impl.function.player.PortalGUI;
import com.client.interfaces.IClientPlayerEntity;
import com.client.system.function.FunctionManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.client.BloodyClient.mc;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity implements IClientPlayerEntity {
    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow @Final protected MinecraftClient client;
    @Shadow private boolean lastSprinting;
    @Shadow @Final public ClientPlayNetworkHandler networkHandler;
    @Shadow private boolean lastSneaking;
    @Shadow private double lastX;
    @Shadow private double lastBaseY;
    @Shadow private double lastZ;
    @Shadow private float lastYaw;
    @Shadow private float lastPitch;
    @Shadow private boolean lastOnGround;
    @Shadow protected abstract boolean isCamera();
    @Shadow private int ticksSinceLastPositionPacketSent;
    @Shadow private boolean autoJumpEnabled;
    @Shadow public int ticksSinceSprintingChanged;
    @Shadow protected int ticksLeftToDoubleTapSprint;
    @Shadow protected abstract void updateNausea();
    @Shadow public Input input;
    @Shadow protected abstract boolean isWalking();
    @Shadow private boolean inSneakingPose;
    @Shadow public abstract boolean isSneaking();
    @Shadow public abstract boolean shouldSlowDown();
    @Shadow public abstract boolean isUsingItem();
    @Shadow private int ticksToNextAutojump;
    @Shadow protected abstract void pushOutOfBlocks(double x, double d);
    @Shadow public abstract boolean isSubmergedInWater();
    @Shadow public abstract void setSprinting(boolean sprinting);
    @Shadow public abstract void sendAbilitiesUpdate();
    @Shadow private boolean field_3939;
    @Shadow private int underwaterVisibilityTicks;
    @Shadow public abstract boolean hasJumpingMount();
    @Shadow private int field_3938;
    @Shadow private float field_3922;
    @Shadow public abstract float method_3151();
    @Shadow protected abstract void startRidingJump();

    @Shadow public abstract void closeScreen();

    @Unique private PortalGUI portalGUI;
    @Unique private NoPush noPush;

    @Inject(method = "closeHandledScreen", at = @At("HEAD"), cancellable = true)
    private void closeHandledScreen(CallbackInfo ci) {
        CloseScreenEvent event = new CloseScreenEvent();
        event.post();
        if (event.isCancelled()) {
            ci.cancel();
            closeScreen();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void preTick(CallbackInfo ci) {
        if (mc.player != null && mc.world != null) {
            PlayerUpdateEvent event = new PlayerUpdateEvent();
            event.post();
        }
    }

    @Redirect(method = "updateNausea", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;"))
    private Screen updateNauseaGetCurrentScreenProxy(MinecraftClient client) {
        if (portalGUI == null) portalGUI = FunctionManager.get(PortalGUI.class);

        if (portalGUI.isEnabled()) return null;
        return client.currentScreen;
    }

    /**
     * @author Artik
     * @reason DEFOLT
     */
    @Overwrite
    private void sendMovementPackets() {
        boolean bl = this.isSprinting();
        if (bl != this.lastSprinting) {
            ClientCommandC2SPacket.Mode mode = bl ? ClientCommandC2SPacket.Mode.START_SPRINTING : ClientCommandC2SPacket.Mode.STOP_SPRINTING;
            this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, mode));
            this.lastSprinting = bl;
        }

        boolean bl2 = this.isSneaking();
        if (bl2 != this.lastSneaking) {
            ClientCommandC2SPacket.Mode mode2 = bl2 ? ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY : ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY;
            this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, mode2));
            this.lastSneaking = bl2;
        }

        if (this.isCamera() && !Helper.sendPacket) {
            SendMovementPacketsEvent event = new SendMovementPacketsEvent(getX(), getY(), getZ(), yaw, pitch, onGround);

            if (this.equals(mc.player)) {
                event.post();
            }

            float packetYaw = event.yaw;
            float packetPitch = event.pitch;

            double d = event.x - this.lastX;
            double e = event.y - this.lastBaseY;
            double f = event.z - this.lastZ;
            double g = packetYaw - this.lastYaw;
            double h = packetPitch - this.lastPitch;

            ++this.ticksSinceLastPositionPacketSent;

            boolean bl3 = event.moving || d * d + e * e + f * f > 9.0E-4 || this.ticksSinceLastPositionPacketSent >= 20;
            boolean bl4 = g != 0.0 || h != 0.0;

            if (this.hasVehicle()) {
                Vec3d vec3d = this.getVelocity();
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Both(vec3d.x, -999.0, vec3d.z, packetYaw, packetPitch, this.onGround));
                bl3 = false;
            } else if (bl3 && bl4 || event.both) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Both(event.x, event.y, event.z, packetYaw, packetPitch, this.onGround));
            } else if (bl3) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(event.x, event.y, event.z, this.onGround));
            } else if (bl4) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(packetYaw, packetPitch, this.onGround));
            } else if (this.lastOnGround != this.onGround) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket(this.onGround));
            }

            if (event.post != null) {
                event.post.run();
            }

            if (bl3) {
                this.lastX = event.x;
                this.lastBaseY = event.y;
                this.lastZ = event.z;
                this.ticksSinceLastPositionPacketSent = 0;
            }

            if (bl4) {
                this.lastYaw = event.yaw;
                this.lastPitch = event.pitch;
            }

            this.lastOnGround = this.onGround;
            this.autoJumpEnabled = this.client.options.autoJump;

            SendMovementPacketsEvent.Post eventPost = new SendMovementPacketsEvent.Post(getX(), getY(), getZ(), yaw, pitch, onGround);
            if (this.equals(mc.player)) {
                eventPost.post();
            }
        }
        Helper.sendPacket = false;
    }

    /**
     * @author Artik
     * @reason DEFOLT
     */
    @Overwrite
    public void tickMovement() {
        ++this.ticksSinceSprintingChanged;
        if (this.ticksLeftToDoubleTapSprint > 0) {
            --this.ticksLeftToDoubleTapSprint;
        }

        this.updateNausea();
        boolean bl = this.input.jumping;
        boolean bl2 = this.input.sneaking;
        boolean bl3 = this.isWalking();
        this.inSneakingPose = !this.abilities.flying && !this.isSwimming() && this.wouldPoseNotCollide(EntityPose.CROUCHING) && (this.isSneaking() || !this.isSleeping() && !this.wouldPoseNotCollide(EntityPose.STANDING));

        this.input.tick(this.shouldSlowDown());
        this.client.getTutorialManager().onMovement(this.input);
        if (this.isUsingItem() && !this.hasVehicle()) {
            NoSlowEvent event = new NoSlowEvent();
            event.post();
            if (!event.isCancelled()) {
                Input var10000 = this.input;
                var10000.movementSideways *= 0.2F;
                var10000.movementForward *= 0.2F;
            }
            this.ticksLeftToDoubleTapSprint = 0;
        }

        boolean bl4 = false;
        if (this.ticksToNextAutojump > 0) {
            --this.ticksToNextAutojump;
            bl4 = true;
            this.input.jumping = true;
        }

        if (!this.noClip) {
            this.pushOutOfBlocks(this.getX() - (double) this.getWidth() * 0.35, this.getZ() + (double) this.getWidth() * 0.35);
            this.pushOutOfBlocks(this.getX() - (double) this.getWidth() * 0.35, this.getZ() - (double) this.getWidth() * 0.35);
            this.pushOutOfBlocks(this.getX() + (double) this.getWidth() * 0.35, this.getZ() - (double) this.getWidth() * 0.35);
            this.pushOutOfBlocks(this.getX() + (double) this.getWidth() * 0.35, this.getZ() + (double) this.getWidth() * 0.35);
        }

        if (bl2) {
            this.ticksLeftToDoubleTapSprint = 0;
        }

        boolean bl5 = (float) this.getHungerManager().getFoodLevel() > 6.0F || this.abilities.allowFlying;
        if ((this.onGround || this.isSubmergedInWater()) && !bl2 && !bl3 && this.isWalking() && !this.isSprinting() && bl5 && !this.isUsingItem() && !this.hasStatusEffect(StatusEffects.BLINDNESS)) {
            if (this.ticksLeftToDoubleTapSprint <= 0 && !this.client.options.keySprint.isPressed()) {
                this.ticksLeftToDoubleTapSprint = 7;
            } else {
                this.setSprinting(true);
            }
        }

        if (!this.isSprinting() && (!this.isTouchingWater() || this.isSubmergedInWater()) && this.isWalking() && bl5 && !this.isUsingItem() && !this.hasStatusEffect(StatusEffects.BLINDNESS) && this.client.options.keySprint.isPressed()) {
            this.setSprinting(true);
        }

        boolean bl8;
        if (this.isSprinting()) {
            bl8 = !this.input.hasForwardMovement() || !bl5;
            boolean bl7 = bl8 || this.horizontalCollision || this.isTouchingWater() && !this.isSubmergedInWater();
            if (this.isSwimming()) {
                if (!this.onGround && !this.input.sneaking && bl8 || !this.isTouchingWater()) {
                    this.setSprinting(false);
                }
            } else if (bl7) {
                this.setSprinting(false);
            }
        }

        bl8 = false;
        if (this.abilities.allowFlying) {
            if (this.client.interactionManager.isFlyingLocked()) {
                if (!this.abilities.flying) {
                    this.abilities.flying = true;
                    bl8 = true;
                    this.sendAbilitiesUpdate();
                }
            } else if (!bl && this.input.jumping && !bl4) {
                if (this.abilityResyncCountdown == 0) {
                    this.abilityResyncCountdown = 7;
                } else if (!this.isSwimming()) {
                    this.abilities.flying = !this.abilities.flying;
                    bl8 = true;
                    this.sendAbilitiesUpdate();
                    this.abilityResyncCountdown = 0;
                }
            }
        }

        if (this.input.jumping && !bl8 && !bl && !this.abilities.flying && !this.hasVehicle() && !this.isClimbing()) {
            ItemStack itemStack = this.getEquippedStack(EquipmentSlot.CHEST);
            if (itemStack.getItem() == Items.ELYTRA && ElytraItem.isUsable(itemStack) && this.checkFallFlying()) {
                this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            }
        }

        this.field_3939 = this.isFallFlying();
        if (this.isTouchingWater() && this.input.sneaking && this.method_29920()) {
            this.knockDownwards();
        }

        int j;
        if (this.isSubmergedIn(FluidTags.WATER)) {
            j = this.isSpectator() ? 10 : 1;
            this.underwaterVisibilityTicks = MathHelper.clamp(this.underwaterVisibilityTicks + j, 0, 600);
        } else if (this.underwaterVisibilityTicks > 0) {
            this.isSubmergedIn(FluidTags.WATER);
            this.underwaterVisibilityTicks = MathHelper.clamp(this.underwaterVisibilityTicks - 10, 0, 600);
        }

        if (this.abilities.flying && this.isCamera()) {
            j = 0;
            if (this.input.sneaking) {
                --j;
            }

            if (this.input.jumping) {
                ++j;
            }

            if (j != 0) {
                this.setVelocity(this.getVelocity().add(0.0, (float) j * this.abilities.getFlySpeed() * 3.0F, 0.0));
            }
        }

        if (this.hasJumpingMount()) {
            JumpingMount jumpingMount = (JumpingMount) this.getVehicle();
            if (this.field_3938 < 0) {
                ++this.field_3938;
                if (this.field_3938 == 0) {
                    this.field_3922 = 0.0F;
                }
            }

            if (bl && !this.input.jumping) {
                this.field_3938 = -10;
                jumpingMount.setJumpStrength(MathHelper.floor(this.method_3151() * 100.0F));
                this.startRidingJump();
            } else if (!bl && this.input.jumping) {
                this.field_3938 = 0;
                this.field_3922 = 0.0F;
            } else if (bl) {
                ++this.field_3938;
                if (this.field_3938 < 10) {
                    this.field_3922 = (float) this.field_3938 * 0.1F;
                } else {
                    this.field_3922 = 0.8F + 2.0F / (float) (this.field_3938 - 9) * 0.1F;
                }
            }
        } else {
            this.field_3922 = 0.0F;
        }

        super.tickMovement();
        if (this.onGround && this.abilities.flying && !this.client.interactionManager.isFlyingLocked()) {
            this.abilities.flying = false;
            this.sendAbilitiesUpdate();
        }
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"), cancellable = true)
    public void onMoveHook(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        PlayerMoveEvent gg = PlayerMoveEvent.get(movementType, movement);
        gg.post();
        if (gg.isCancelled()) {
            super.move(movementType, new Vec3d(gg.movement.x, gg.movement.y, gg.movement.z));
            ci.cancel();
        }
    }

    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    private void onPushOutOfBlocksHook(double x, double d, CallbackInfo info) {
        if (noPush == null) noPush = FunctionManager.get(NoPush.class);

        if (noPush.isEnabled() && noPush.blocks.get()) {
            info.cancel();
        }
    }

    @Override
    public boolean lastSprinting() {
        return this.lastSprinting;
    }

    @Override
    public void setLastSprinting(boolean b) {
        this.lastSprinting = b;
    }

    @Override
    public float getLastYaw() {
        return this.lastYaw;
    }

    @Override
    public float getLastPitch() {
        return this.lastPitch;
    }
}