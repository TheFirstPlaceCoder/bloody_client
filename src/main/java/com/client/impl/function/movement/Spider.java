package com.client.impl.function.movement;

import api.interfaces.EventHandler;
import com.client.event.events.PacketEvent;
import com.client.event.events.SendMovementPacketsEvent;
import com.client.event.events.TickEvent;
import com.client.system.command.Command;
import com.client.system.function.Category;
import com.client.system.function.Function;
import com.client.system.notification.Notification;
import com.client.system.notification.NotificationManager;
import com.client.system.notification.NotificationType;
import com.client.system.setting.settings.ListSetting;
import com.client.utils.game.chat.ChatUtils;
import com.client.utils.game.inventory.FindItemResult;
import com.client.utils.game.inventory.InvUtils;
import com.client.utils.game.rotate.Rotations;
import mixin.accessor.PlayerMoveC2SPacketAccessor;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class Spider extends Function {
    private final ListSetting mode = List().name("Режим").list(List.of("Блоки", "Matrix", "Vulcan", "FunTime")).defaultValue("Matrix").build();

    public Spider() {
        super("Spider", Category.MOVEMENT);
    }

    private int tick = 0;
    private boolean modify = false;
    private boolean start = false;

    private double startY = 0;
    private double lastY = 0;

    private final double coff = 0.0000000000326;
    private int ticks = 0;
    private int slot = -1;
    private double blocks = 0;

    @Override
    public void onEnable() {
        tick = 0;
        start = false;
        modify = false;

        assert mc.player != null;
        startY = mc.player.getPos().y;
    }

    private boolean YGround(double height, double min, double max) {
        String yString = String.valueOf(height);
        yString = yString.substring(yString.indexOf("."));
        double y = Double.parseDouble(yString);
        return y >= min && y <= max;
    }

    private double RGround(double height) {
        String yString = String.valueOf(height);
        yString = yString.substring(yString.indexOf("."));
        return Double.parseDouble(yString);
    }

    @EventHandler
    public void onSendPacket(PacketEvent.Send event) {
        if (!mode.get().equals("Блоки") && !mode.get().equals("FunTime")) {
            work(event.packet);
        }
    }

    @EventHandler
    public void onSentPacket(PacketEvent.Sent event) {
        if (!mode.get().equals("Блоки") && !mode.get().equals("FunTime")) {
            work(event.packet);
        }
    }

    private void work(Packet<?> packet) {
        if (modify) {
            if (packet instanceof PlayerMoveC2SPacket move) {
                assert mc.player != null;
                double y = mc.player.getY();
                y = move.getY(y);

                if (YGround(y, RGround(startY) - 0.1, RGround(startY) + 0.1)) {
                    ((PlayerMoveC2SPacketAccessor) packet).setOnGround(true);
                }
                if (mc.player.isOnGround() && block) {
                    block = false;
                    startY = mc.player.getPos().y;
                    start = false;
                }
            }
        } else {
            assert mc.player != null;
            if (mc.player.isOnGround() && block) {
                block = false;
                startY = mc.player.getPos().y;
                start = false;
            }
        }
    }

    private boolean work1() {
        ClientPlayerEntity player = mc.player;
        assert player != null;
        FindItemResult elytra = InvUtils.find(Items.ELYTRA);
        if (elytra.found()) {
            return true;
        }
        else {
            return false;
        }
    }
    private void clip() {
        if (blocks != 0) {
            ClientPlayerEntity player = mc.player;
            assert player != null;
            switch (ticks) {
                case 0: {
                    FindItemResult elytra = InvUtils.find(Items.ELYTRA);
                    slot = elytra.slot();
                    InvUtils.move().from(slot).toArmor(2);
                    ticks++;
                }
                case 1: {
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(mc.player.yaw, mc.player.pitch, false));
                    ticks++;
                }
                case 2: {
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(mc.player.yaw, mc.player.pitch, false));
                    ticks++;
                }
                case 3: {
                    mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                    ticks++;
                }
                case 4: {
                    player.setPosition(player.getX(), player.getY() + blocks, player.getZ());
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(player.getX(), player.getY() + blocks, player.getZ(), false));
                    ticks++;
                }
                case 5: {
                    mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                    ticks++;
                }
                case 6: {
                    ticks = 0;
                    blocks = 0;
                    InvUtils.move().fromArmor(2).to(slot);
                }
            }
        }
    }

    private boolean block = false;

    @EventHandler
    public void onTickEventPre(TickEvent.Pre event) {
        if (!mode.get().equals("Блоки")) {
            if (mode.get().equals("FunTime")) {
                if (mc.player.age % 2 == 0 && mc.options.keyJump.isPressed() && mc.player.horizontalCollision) {
                    float pitch = mc.player.pitch;
                    mc.player.pitch = (82);
                    FindItemResult waterBucket = InvUtils.findInHotbar(Items.WATER_BUCKET);
                    if (waterBucket.found()) {
                        int slot = waterBucket.slot();

                        int originalSlot = mc.player.inventory.selectedSlot;
                        mc.player.inventory.selectedSlot = slot;
                        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));

                        mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
                        mc.player.swingHand(Hand.MAIN_HAND);

                        mc.player.inventory.selectedSlot = originalSlot;
                        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(originalSlot));
                    }

                    mc.player.pitch = (pitch);
                }
            } else {
                if (modify) {
                    ClientPlayerEntity player = mc.player;
                    double y = player.getPos().y;
                    if (lastY == y && tick > 1) {
                        block = true;
                    } else {
                        lastY = y;
                    }
                }
            }
        }
    }

    private TypeStarted getType(double startY) {
        TypeStarted temp = TypeStarted.Air;
        double y = RGround(startY);
        assert mc.player != null;
        if (mc.player.isOnGround()) {
            temp = TypeStarted.Block;
            assert mc.world != null;
            if (mc.world.getBlockState(mc.player.getBlockPos()).getBlock() instanceof SlabBlock) {
                temp = TypeStarted.Slab;
            }
        }
        return temp;
    }

    private enum TypeStarted
    {
        Block,
        Slab,
        Air,
    }

    private TypeStarted typeStarted = TypeStarted.Air;

    @EventHandler
    public void onTickEventPost(TickEvent.Post event) {
        if (mode.get().equals("Matrix")) {
            ClientPlayerEntity player = mc.player;
            assert player != null;
            Vec3d pl_velocity = player.getVelocity();
            modify = player.horizontalCollision;
            if (mc.player.isOnGround()) {
                block = false;
                startY = mc.player.getPos().y;
                start = false;
            }
            if (player.horizontalCollision) {
                if (!start) {
                    start = true;
                    startY = mc.player.getPos().y;
                    lastY = mc.player.getY();
                }
                if (!block) {
                    if (tick == 0) {
                        mc.player.setVelocity(pl_velocity.x, 0.41999998688698, pl_velocity.z);
                        tick = 1;
                    } else if (tick == 1) {
                        mc.player.setVelocity(pl_velocity.x, 0.41999998688698 - 0.08679999325 - coff, pl_velocity.z);
                        tick = 2;
                    } else if (tick == 2) {
                        mc.player.setVelocity(pl_velocity.x, 0.41999998688698 - 0.17186398826 - coff, pl_velocity.z);
                        tick = 0;
                    }
                }
            } else {
                modify = false;
                tick = 0;
            }
        } else if (mode.get().equals("Vulcan")) {
            ClientPlayerEntity player = mc.player;
            assert player != null;
            Vec3d pl_velocity = player.getVelocity();
            Vec3d pos = player.getPos();
            ClientPlayNetworkHandler h = mc.getNetworkHandler();
            modify = player.horizontalCollision;
            if (mc.player.isOnGround()) {
                block = false;
                startY = mc.player.getPos().y;
                start = false;
                typeStarted = getType(startY);
            }
            if (player.horizontalCollision) {
                if (!start) {
                    start = true;
                    startY = mc.player.getPos().y;
                    lastY = mc.player.getY();
                }
                if (!block) {
                    if (tick == 0) {
                        mc.player.setVelocity(pl_velocity.x, 0.41999998688698, pl_velocity.z);
                        tick = 1;
                    } else if (tick == 1) {
                        mc.player.setVelocity(pl_velocity.x, 0.41999998688698 - 0.08679999325 - coff, pl_velocity.z);
                        tick = 2;
                    } else if (tick == 2) {
                        mc.player.setVelocity(pl_velocity.x, 0.41999998688698 - 0.17186398826 - coff, pl_velocity.z);
                        tick = 0;
                    }
                    switch (typeStarted) {
                        case Air -> {
                            if (mc.player.getPos().y >= startY + 1.5) {
                                block = true;
                            }
                        }
                        case Slab -> {
                            if (mc.player.getPos().y >= startY + 2.5) {
                                block = true;
                            }
                        }
                        case Block -> {
                            if (mc.player.getPos().y >= startY + 2) {
                                block = true;
                            }
                        }
                    }
                }
            }
            else {
                modify = false;
                tick = 0;
            }
        }
    }

    @EventHandler
    private void onSendMovementPacketsEvent(SendMovementPacketsEvent event) {
        if (!mode.get().equals("Блоки")) return;

        FindItemResult block = InvUtils.findInHotbar(item -> item.getItem() instanceof BlockItem);

        if (!block.found()) {
            NotificationManager.add(new Notification(NotificationType.CLIENT, "Вам нужны блоки чтобы использовать Spider", 1500L), NotificationManager.NotifType.Error);
            toggle();
            return;
        }

        Pair<Boolean, Direction> neighbor = getNeighbor();
        if (mc.player.fallDistance > 0 && neighbor.getLeft() && mc.player.getY() <= mc.player.getBlockPos().getY() + 0.150d) {
            boolean swap = false;
            if (block.slot() != mc.player.inventory.selectedSlot) {
                InvUtils.swap(block.slot());
                swap = true;
            }
            BlockPos pos = mc.player.getBlockPos().down();
            BlockPos neighborPos = pos.offset(neighbor.getRight());
            Direction opposite = neighbor.getRight().getOpposite();
            Vec3d hit = Vec3d.ofCenter(neighborPos).add(opposite.getOffsetX() * 0.5f, opposite.getOffsetY() * 0.5f, opposite.getOffsetZ() * 0.5f);
            event.pitch = MathHelper.clamp((float) Rotations.getPitch(hit), -89f, 89f);
            event.yaw = (float) Rotations.getYaw(hit);
            mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(hit, opposite, neighborPos, false));
            mc.player.swingHand(Hand.MAIN_HAND);
            if (swap) {
                event.post = InvUtils::swapBack;
            }
            mc.player.fallDistance = 0;
        }
    }

    private Pair<Boolean, Direction> getNeighbor() {
        for (Direction direction : Direction.values()) {
            if (direction == Direction.DOWN || direction == Direction.UP) continue;
            if (!mc.world.getBlockState(mc.player.getBlockPos().down().offset(direction)).isAir() && !(mc.world.getBlockState(mc.player.getBlockPos().down().offset(direction)).getBlock() instanceof FluidBlock)) {
                return new Pair<>(true, direction);
            }
        }
        return new Pair<>(false, Direction.DOWN);
    }
}