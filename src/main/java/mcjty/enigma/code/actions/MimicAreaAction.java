package mcjty.enigma.code.actions;

import mcjty.enigma.blocks.MimicTE;
import mcjty.enigma.blocks.ModBlocks;
import mcjty.enigma.code.Action;
import mcjty.enigma.code.EnigmaFunctionContext;
import mcjty.enigma.code.ExecutionException;
import mcjty.enigma.parser.Expression;
import mcjty.enigma.progress.Progress;
import mcjty.enigma.progress.ProgressHolder;
import mcjty.enigma.varia.AreaTools;
import mcjty.enigma.varia.IAreaIterator;
import mcjty.enigma.varia.IPositional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.apache.commons.lang3.StringUtils;

public class MimicAreaAction extends Action {
    private final Expression<EnigmaFunctionContext> area;
    private final Expression<EnigmaFunctionContext> destination;

    public MimicAreaAction(Expression<EnigmaFunctionContext> area, Expression<EnigmaFunctionContext> destination) {
        this.area = area;
        this.destination = destination;
    }

    @Override
    public void dump(int indent) {
        System.out.println(StringUtils.repeat(' ', indent) + "MimicArea: " + area + " to " + destination);

    }

    private static class Remembered {
        IBlockState state;
        NBTTagCompound tc;

        public Remembered(IBlockState state, NBTTagCompound tc) {
            this.state = state;
            this.tc = tc;
        }
    }

    @Override
    public void execute(EnigmaFunctionContext context) throws ExecutionException {
        Progress progress = ProgressHolder.getProgress(context.getWorld());

        IAreaIterator iterator = AreaTools.getAreaIterator(progress, this.area.eval(context));
        IPositional destination = AreaTools.getPositional(progress, this.destination.eval(context));

        World wsrc = iterator.getWorld();
        WorldServer wdest = destination.getWorld();
        int dx = destination.getPos().getX() - iterator.getBottomLeft().getX();
        int dy = destination.getPos().getY() - iterator.getBottomLeft().getY();
        int dz = destination.getPos().getZ() - iterator.getBottomLeft().getZ();
        BlockPos.MutableBlockPos dest = new BlockPos.MutableBlockPos();
        while (iterator.advance()) {
            BlockPos current = iterator.current();
            dest.setPos(current.getX() + dx, current.getY() + dy, current.getZ() + dz);
            IBlockState blockState = wsrc.getBlockState(current);
            wdest.setBlockState(dest, ModBlocks.mimic.getDefaultState(), 3);
            TileEntity te = wdest.getTileEntity(dest);
            if (te instanceof MimicTE) {
                MimicTE mimic = (MimicTE) te;
                mimic.setToMimic(blockState);
            }
        }
    }
}
