package io.github.eufranio.spongytowns.display.visual;

import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.towns.Town;
import io.github.eufranio.spongytowns.util.Util;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Created by Frani on 13/04/2018.
 */
public class BorderVisual extends Visual {

    private Claim representedClaim;
    private int claimBlocksCount;

    public BorderVisual(Claim claim, Player... viewers) {
        super(viewers);
        this.representedClaim = claim;
    }

    @Override
    public boolean isVisualValid() {
        return this.representedClaim.getBlocks().size() == this.claimBlocksCount;
    }

    @Override
    public void populateBlocks(Player firstViewer) {
        this.claimBlocksCount = this.representedClaim.getBlocks().size();
        this.representedClaim.getBlocks().forEach(b -> {
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
                    int y = firstViewer.getLocation().getBlockY();
                    Location<World> loc = Util.getVisibleLocation(b.getChunk().getWorld(), x, y, z, false);

                    int claimed = this.claimedNeighbours(loc, this.representedClaim, Util.ORDINAL_SET);
                    if (claimed == 8) continue zLoop;

                    BlockLocation block = new BlockLocation(this.representedClaim instanceof Town ? BlockTypes.LAPIS_BLOCK : BlockTypes.REDSTONE_BLOCK,
                            loc.getBlockPosition(),
                            b.getChunk().getWorld());
                    blocks.add(block);
                }
            }
        });
    }

    private int claimedNeighbours(Location<World> actualBlock, Claim claim, Direction[] set) {
        int claimed = 0;
        for (Direction d : set) {
            if (claim.hasClaimed(actualBlock.getBlockRelative(d))) claimed++;
        }
        return claimed;
    }

}
