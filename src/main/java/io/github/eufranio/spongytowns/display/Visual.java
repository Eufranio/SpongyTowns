package io.github.eufranio.spongytowns.display;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.util.Util;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Frani on 08/03/2018.
 */
public class Visual {

    @Getter
    public static List<Visual> visuals = Lists.newArrayList();

    private List<UUID> viewers = Lists.newArrayList();
    private List<BlockLocation> blocks = Lists.newArrayList();
    private Task task;
    private Claim representedClaim;

    public Visual(Claim claim, Player... viewers) {
        for (Player p : viewers) {
            this.viewers.add(p.getUniqueId());
        }
        claim.getBlocks().forEach(b -> {

            // from minecraft, ChunkPos.getStart/End
            int[] xPos = new int[] { b.getLocation().getX() << 4, (b.getLocation().getX() << 4) + 15 };
            int[] zPos = new int[] { b.getLocation().getZ() << 4, (b.getLocation().getZ() << 4) + 15 };
            int startX = Math.min(xPos[0], xPos[1]);
            int startZ = Math.min(zPos[0], zPos[1]);
            int endX = Math.max(xPos[0], xPos[1]);
            int endZ = Math.max(zPos[0], zPos[1]);
            for (int x = startX; x <= endX; x++) {

                zLoop:
                for (int z = startZ; z <= endZ; z++) {
                    int y = viewers[0].getLocation().getBlockY();
                    Location<World> loc = Util.getVisibleLocation(b.getChunk().getWorld(), x, y, z, false);

                    int claimed = this.claimedNeighbours(loc, claim, Util.ORDINAL_SET);
                    if (claimed == 8) continue zLoop;

                    BlockLocation block = new BlockLocation(BlockTypes.REDSTONE_BLOCK,
                            loc.getBlockPosition(),
                            b.getChunk().getWorld());
                    this.blocks.add(block);
                }
            }
        });
        this.representedClaim = claim;
    }

    public void add(Visual other) {
        this.blocks.addAll(other.blocks);
        this.viewers.addAll(other.viewers);
    }

    public void start() {
        this.task = Task.builder()
                .intervalTicks(10)
                .execute(this::tick)
                .submit(SpongyTowns.getInstance());

        Task.builder()
                .delay(15, TimeUnit.SECONDS) //TODO: change this
                .execute(this::stop)
                .submit(SpongyTowns.getInstance());

        visuals.add(this);
    }

    public static void startOrMerge(Claim claim, Player viewer) {
        Visual pVisual = visuals.stream().filter(v -> v.viewers.contains(viewer.getUniqueId())).findFirst().orElse(null);
        if (pVisual != null) {
            if (!pVisual.representedClaim.equals(claim)) {
                pVisual.add(new Visual(claim, viewer));
            }
        } else {
            new Visual(claim, viewer).start();
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class BlockLocation {
        private final BlockType block;
        private final Vector3i location;
        private final World world;
    }

    public void tick() {
        this.viewers.stream()
                .map(Sponge.getServer()::getPlayer)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(p -> this.blocks.forEach(block -> {
                        if (p.getWorld().getUniqueId().equals(block.getWorld().getUniqueId())) {
                            p.sendBlockChange(block.getLocation(), block.getBlock().getDefaultState());
                        }
                    })
                );
    }

    public void stop() {
        this.task.cancel();
        this.task = null;
        this.viewers.stream()
                .map(Sponge.getServer()::getPlayer)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(p ->
                    this.blocks.forEach(block -> p.resetBlockChange(block.getLocation()))
                );
        visuals.remove(this);
    }

    public static Visual getVisual(UUID viewer) {
        return visuals.stream().filter(v -> v.viewers.contains(viewer)).findFirst().orElse(null);
    }

    private int claimedNeighbours(Location<World> actualBlock, Claim claim, Direction[] set) {
        int claimed = 0;
        for (Direction d : set) {
            if (claim.hasClaimed(actualBlock.getBlockRelative(d))) claimed++;
        }
        return claimed;
    }

}
