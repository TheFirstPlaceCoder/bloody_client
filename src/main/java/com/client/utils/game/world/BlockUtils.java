package com.client.utils.game.world;

import com.client.impl.function.movement.Scaffold;
import com.client.interfaces.IVec3d;
import com.client.utils.game.inventory.FindItemResult;
import com.client.utils.game.inventory.InvUtils;
import com.client.utils.game.rotate.Rotations;
import com.client.utils.math.DistanceUtils;
import mixin.accessor.AbstractBlockAccessor;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static com.client.BloodyClient.mc;

public class BlockUtils {
    public static final Vec3d hitPos = new Vec3d(0, 0, 0);

    public static boolean place(BlockPos blockPos, FindItemResult findItemResult) {
        return place(blockPos, findItemResult, true);
    }

    public static boolean place(BlockPos blockPos, FindItemResult findItemResult, boolean checkEntities) {
        return place(blockPos, findItemResult, true, checkEntities);
    }

    public static boolean place(BlockPos blockPos, FindItemResult findItemResult, boolean swingHand, boolean checkEntities) {
        return place(blockPos, findItemResult, swingHand, checkEntities, true);
    }

    public static boolean place(BlockPos blockPos, FindItemResult findItemResult, boolean swingHand, boolean checkEntities, boolean swapBack) {
        if (findItemResult.isOffhand()) {
            return place(blockPos, Hand.OFF_HAND, mc.player.inventory.selectedSlot, swingHand, checkEntities, swapBack);
        } else if (findItemResult.isHotbar()) {
            return place(blockPos, Hand.MAIN_HAND, findItemResult.slot(), swingHand, checkEntities, swapBack);
        }
        return false;
    }

    public static boolean place(BlockPos blockPos, Hand hand, int slot, boolean swingHand, boolean checkEntities, boolean swapBack) {
        if (slot < 0 || slot > 8) return false;
        if (!canPlace(blockPos, checkEntities)) return false;

        ((IVec3d) hitPos).set(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);

        BlockPos neighbour;
        Direction side = getPlaceSide(blockPos);

        if (side == null) {
            side = Direction.UP;
            neighbour = blockPos;
        } else {
            neighbour = blockPos.offset(side.getOpposite());
            hitPos.add(side.getOffsetX() * 0.5, side.getOffsetY() * 0.5, side.getOffsetZ() * 0.5);
        }

        Direction s = side;

        int prevSlot = mc.player.inventory.selectedSlot;
        InvUtils.swap(slot);

        place(new BlockHitResult(hitPos, s, neighbour, false), hand, swingHand);

        if (swapBack) InvUtils.swap(prevSlot);


        return true;
    }

    @Nullable
    public static Info findSide(BlockPos pos, double maxDistance) {
        AtomicReference<Info> state = new AtomicReference<>();

        try {
            if (mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
                for (Direction direction : Direction.values()) {
                    BlockPos offsetDirection = pos.offset(direction);
                    VoxelShape outlineShape = mc.world.getBlockState(offsetDirection).getOutlineShape(mc.world, pos);
                    if (outlineShape.isEmpty()) continue; // air

                    outlineShape.forEachEdge((minX, minY, minZ, maxX, maxY, maxZ) -> {
                        Vec3d vec = new Vec3d(offsetDirection.getX() + minX, offsetDirection.getY() + minY, offsetDirection.getZ() + minZ);

                        for (Direction directionAngle : Direction.values()) {
                            Vec3d angle = BlockUtils.offset(vec, directionAngle, 0.01);
                            float yaw = (float) Rotations.getYaw(angle);
                            float pitch = (float) Rotations.getPitch(angle);
                            BlockHitResult raycast = raycast(maxDistance, yaw, pitch);

                            if (mc.world.getBlockState(raycast.getBlockPos()).getOutlineShape(mc.world, raycast.getBlockPos()).isEmpty()) continue; // air

                            if (raycast.getBlockPos().offset(raycast.getSide()).equals(pos)) {
                                state.set(new Info(raycast, yaw, pitch));
                                throw new RuntimeException(); //fast exit
                            }
                        }
                    });
                }
            }
        } catch (RuntimeException ex) {}

        return state.get();
    }

    @NotNull
    private static BlockHitResult raycast(double maxDistance, float yaw, float pitch) {
        Vec3d vec3d = mc.player.getPos().add(0, mc.player.getEyeHeight(mc.player.getPose()), 0);
        float f = pitch * 0.017453292F;
        float g = -yaw * 0.017453292F;
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        Vec3d vec3d2 = new Vec3d(i * j, -k, h * j);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);
        return mc.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player));
    }

    @Nullable
    public static BlockHitResult getPlaceResult(BlockPos block, boolean airPlace, boolean strictDirections) {
        BlockPos neighbor = null;
        Direction placeSide = null;
        if (strictDirections) {
            Pair<BlockPos, Direction> pair = getStrictNeighbour(block);
            if (pair != null) {
                neighbor = (BlockPos)pair.getLeft();
                placeSide = (Direction)pair.getRight();
            }
        } else {
            placeSide = getClosestDirection(block, true);
            if (placeSide != null) {
                neighbor = block.offset(placeSide);
                placeSide = placeSide.getOpposite();
            }
        }
        if (neighbor == null || placeSide == null) {
            if (!airPlace) {
                return null;
            }
            neighbor = block;
            placeSide = strictDirections ? getStrictSide(block) : getClosestDirection(block, false);
            if (placeSide == null) {
                return null;
            }
        }
        Vec3d eyesPos = mc.player.getPos().add(0, mc.player.getEyeHeight(mc.player.getPose()), 0);
        Vec3d vec = new Vec3d(MathHelper.clamp(eyesPos.x, neighbor.getX(), (neighbor.getX() + 1)), neighbor.getY() + 0.5, MathHelper.clamp(eyesPos.z, neighbor.getZ(), (neighbor.getZ() + 1)));

        return new BlockHitResult(vec, placeSide, neighbor, false);
    }

    public static void add(Set<BlockPos> set, BlockPos pos) {
        if (DistanceUtils.distanceTo(pos) <= 4) {
            BlockState state = mc.world.getBlockState(pos);
            if (state.getMaterial().isReplaceable()) {
                FindItemResult result = InvUtils.findInHotbar(itemStack -> itemStack.getItem() instanceof BlockItem);
                if (result.found()) {
                    BlockState placeState = ((BlockItem) Items.COBBLESTONE).getBlock().getDefaultState();
                    if (findNeighbour(set, pos, 0, placeState, (int)Math.floor(mc.player.getEyeY()))) {
                        set.add(pos);
                    }
                }
            }
        }
    }

    private static final Direction[] ORDERED_DIRECTIONS_ARRAY = new Direction[]{Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH, Direction.DOWN, Direction.UP};

    public static boolean findNeighbour(Set<BlockPos> set, BlockPos pos, int iteration, BlockState placeState, int playerEyeY) {
        Direction[] var6 = Direction.values();
        int var7 = var6.length;

        int var8;
        Direction direction;
        BlockPos neighbour;
        for(var8 = 0; var8 < var7; ++var8) {
            direction = var6[var8];
            neighbour = pos.offset(direction);
            if ((!notInteractableStrict(mc.world.getBlockState(mc.player.getBlockPos()).isOf(Blocks.ENDER_CHEST) ? mc.player.getBlockPos().up().getX() : mc.player.getBlockPos().getX(), playerEyeY, mc.world.getBlockState(mc.player.getBlockPos()).isOf(Blocks.ENDER_CHEST) ? mc.player.getBlockPos().up().getZ() : mc.player.getBlockPos().getZ(), neighbour, direction.getOpposite())) && (set.contains(neighbour) || !mc.world.getBlockState(neighbour).getMaterial().isReplaceable())) {
                set.add(pos);
                return true;
            }
        }

        if (!((iteration + 1) > Math.ceil(4) * 2.0D)) {
            var6 = ORDERED_DIRECTIONS_ARRAY;
            var7 = var6.length;

            for (var8 = 0; var8 < var7; ++var8) {
                direction = var6[var8];
                neighbour = pos.offset(direction);

                if (DistanceUtils.distanceTo(neighbour) > 4) {
                    return false;
                }

                if (placeState.isFullCube(mc.world, neighbour)) {
                    Set<Direction> strictDirs = getInteractableDirections((mc.world.getBlockState(mc.player.getBlockPos()).isOf(Blocks.ENDER_CHEST) ? mc.player.getBlockPos().up().getX() : mc.player.getBlockPos().getX()) - neighbour.getX(), playerEyeY - neighbour.getY(), (mc.world.getBlockState(mc.player.getBlockPos()).isOf(Blocks.ENDER_CHEST) ? mc.player.getBlockPos().up().getZ() : mc.player.getBlockPos().getZ()) - neighbour.getZ(), true);
                    Direction oppositeDirection = direction.getOpposite();
                    if (!strictDirs.contains(oppositeDirection) || isDirectionBlocked(neighbour, strictDirs, oppositeDirection, true) || set.contains(pos)) {
                        continue;
                    }
                }

                if (!mc.world.canPlace(placeState, neighbour, ShapeContext.absent())) {
                    continue;
                }

                ++iteration;
                if (findNeighbour(set, neighbour, iteration, placeState, playerEyeY)) {
                    return false;
                }
            }

        }
        return false;
    }

    public static boolean notInteractableStrict(int playerX, int playerY, int playerZ, BlockPos blockPos, Direction direction) {
        if (playerX == blockPos.getX() && playerY == blockPos.getY() && playerZ == blockPos.getZ()) {
            return false;
        } else {
            boolean fullBounds = mc.world.getBlockState(blockPos).isFullCube(mc.world, blockPos);
            Set<Direction> interactableDirections = getInteractableDirections(playerX - blockPos.getX(), playerY - blockPos.getY(), playerZ - blockPos.getZ(), fullBounds);
            return !interactableDirections.contains(direction) ? true : isDirectionBlocked(blockPos, interactableDirections, direction, fullBounds);
        }
    }

    @Nullable
    public static Direction getStrictSide(BlockPos pos) {
        Direction bestDirection = null;
        double bestDistance = 420.0;
        Vec3d eyePos = mc.player.getPos().add(0, mc.player.getEyeHeight(mc.player.getPose()), 0);
        int playerEyeY = (int)Math.floor(eyePos.y);
        for (Direction direction : Direction.values()) {
            if (!isInteractableStrict(mc.player.getBlockPos().getX(), playerEyeY, mc.player.getBlockPos().getZ(), pos, direction)) continue;
            if (direction == Direction.DOWN) {
                return direction;
            }
            double distance = eyePos.squaredDistanceTo(sideVec(pos, direction));
            if (!(distance < bestDistance)) continue;
            bestDirection = direction;
            bestDistance = distance;
        }
        return bestDirection;
    }

    public static Direction getClosestDirection(BlockPos pos, boolean withSupport) {
        Direction bestDirection = null;
        double bestDistance = 69.0;
        for (Direction direction : Direction.values()) {
            BlockPos neighbour = pos.offset(direction);
            if (withSupport && mc.world.getBlockState(neighbour).getMaterial().isReplaceable()) continue;
            if (direction == Direction.DOWN) {
                return direction;
            }
            double distance = mc.player.getPos().add(0, mc.player.getEyeHeight(mc.player.getPose()), 0).distanceTo(sideVec(pos, direction));
            if (!(distance < bestDistance)) continue;
            bestDistance = distance;
            bestDirection = direction;
        }
        return bestDirection;
    }

    public static Vec3d sideVec(BlockPos pos, Direction direction) {
        return Vec3d.ofCenter(pos).add(direction.getOffsetX() * 0.5D, direction.getOffsetY() * 0.5D, direction.getOffsetZ() * 0.5D);
    }

    @Nullable
    public static Pair<BlockPos, Direction> getStrictNeighbour(BlockPos pos) {
        BlockPos bestNeighbour = null;
        Direction bestDirection = null;
        double bestDistance = 420.0;
        Vec3d eyePos = mc.player.getPos().add(0, mc.player.getEyeHeight(mc.player.getPose()), 0);
        int playerEyeY = (int)Math.floor(eyePos.y);
        for (Direction direction : Direction.values()) {
            double distance;
            BlockPos neighbour = pos.offset(direction);
            if (mc.world.getBlockState(neighbour).getMaterial().isReplaceable()) continue;
            Direction opposite = direction.getOpposite();
            if (!isInteractableStrict(mc.player.getBlockPos().getX(), playerEyeY, mc.player.getBlockPos().getZ(), neighbour, opposite) || !((distance = eyePos.squaredDistanceTo(sideVec(neighbour, opposite))) < bestDistance)) continue;
            bestNeighbour = neighbour;
            bestDirection = opposite;
            bestDistance = distance;
        }
        if (bestNeighbour != null) {
            return new Pair(bestNeighbour, bestDirection);
        }
        return null;
    }

    public static boolean isInteractableStrict(int playerX, int playerY, int playerZ, BlockPos blockPos, Direction direction) {
        if (playerX == blockPos.getX() && playerY == blockPos.getY() && playerZ == blockPos.getZ()) {
            return true;
        }
        boolean fullBounds = mc.world.getBlockState(blockPos).isFullCube((BlockView)mc.world, blockPos);
        Set<Direction> interactableDirections = getInteractableDirections(playerX - blockPos.getX(), playerY - blockPos.getY(), playerZ - blockPos.getZ(), fullBounds);
        if (!interactableDirections.contains((Object)direction)) {
            return false;
        }
        return !isDirectionBlocked(blockPos, interactableDirections, direction, fullBounds);
    }

    public static boolean isDirectionBlocked(BlockPos block, Set<Direction> interactableDirections, Direction tDirection, boolean hasFullBounds) {
        BlockState offsetState = mc.world.getBlockState(block.offset(tDirection));
        if (hasFullBounds) {
            return ((AbstractBlockAccessor) offsetState.getBlock()).isCollidable() && offsetState.isFullCube((BlockView)mc.world, block);
        }
        for (Direction direction : interactableDirections) {
            offsetState = mc.world.getBlockState(block.offset(direction));
            if (offsetState.isFullCube((BlockView)mc.world, block) && !((AbstractBlockAccessor) offsetState.getBlock()).isCollidable()) continue;
            return false;
        }
        return true;
    }

    public static Set<Direction> getInteractableDirections(int xdiff, int ydiff, int zdiff, boolean fullBounds) {
        HashSet<Direction> directions = new HashSet<Direction>(6);
        if (!fullBounds) {
            if (xdiff == 0) {
                directions.add(Direction.EAST);
                directions.add(Direction.WEST);
            }
            if (zdiff == 0) {
                directions.add(Direction.SOUTH);
                directions.add(Direction.NORTH);
            }
        }
        if (ydiff == 0) {
            directions.add(Direction.UP);
            directions.add(Direction.DOWN);
        } else {
            directions.add(ydiff > 0 ? Direction.UP : Direction.DOWN);
        }
        if (xdiff != 0) {
            directions.add(xdiff > 0 ? Direction.EAST : Direction.WEST);
        }
        if (zdiff != 0) {
            directions.add(zdiff > 0 ? Direction.SOUTH : Direction.NORTH);
        }
        return directions;
    }

    public static Vec3d offset(Vec3d vec3d, Direction direction, double value) {
        Vec3i vec3i = direction.getVector();
        return new Vec3d(vec3d.x + value * (double)vec3i.getX(), vec3d.y + value * (double)vec3i.getY(), vec3d.z + value * (double)vec3i.getZ());
    }

    private static void place(BlockHitResult blockHitResult, Hand hand, boolean swing) {
        boolean wasSneaking = mc.player.input.sneaking;
        mc.player.input.sneaking = false;

        ActionResult result = mc.interactionManager.interactBlock(mc.player, mc.world, hand, blockHitResult);

        if (result.shouldSwingHand()) {
            if (swing) mc.player.swingHand(hand);
            else mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(hand));
        }

        mc.player.input.sneaking = wasSneaking;
    }

    public static boolean canPlace(BlockPos blockPos, boolean checkEntities) {
        if (blockPos == null) return false;

        // Check y level
        if (World.isOutOfBuildLimitVertically(blockPos)) return false;

        // Check if current block is replaceable
        if (!mc.world.getBlockState(blockPos).getMaterial().isReplaceable()) return false;

        // Check if intersects entities
        return !checkEntities || mc.world.canPlace(Blocks.STONE.getDefaultState(), blockPos, ShapeContext.absent());
    }

    public static boolean canPlace(BlockPos blockPos) {
        return canPlace(blockPos, true);
    }

    private static Direction getPlaceSide(BlockPos blockPos) {
        for (Direction side : Direction.values()) {
            BlockPos neighbor = blockPos.offset(side);
            Direction side2 = side.getOpposite();

            BlockState state = mc.world.getBlockState(neighbor);

            // Check if neighbour isn't empty
            if (state.isAir() || isClickable(state.getBlock())) continue;

            // Check if neighbour is a fluid
            if (!state.getFluidState().isEmpty()) continue;

            return side2;
        }

        return null;
    }

    public static boolean isClickable(Block block) {
        return block instanceof CraftingTableBlock
                || block instanceof AnvilBlock
                || block instanceof AbstractButtonBlock
                || block instanceof AbstractPressurePlateBlock
                || block instanceof BlockWithEntity
                || block instanceof BedBlock
                || block instanceof FenceGateBlock
                || block instanceof DoorBlock
                || block instanceof NoteBlock
                || block instanceof TrapdoorBlock;
    }

    public static class Info {
        public final BlockHitResult result;
        public final float yaw;
        public final float pitch;

        private Info(BlockHitResult result, float yaw, float pitch) {
            this.result = result;
            this.yaw = yaw;
            this.pitch = pitch;
        }
    }
}