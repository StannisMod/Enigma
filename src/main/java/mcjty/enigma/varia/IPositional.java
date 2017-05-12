package mcjty.enigma.varia;

import net.minecraft.world.WorldServer;

public interface IPositional<T extends IPositional> {

    T up(int amount);

    T down(int amount);

    T west(int amount);

    T east(int amount);

    T south(int amount);

    T north(int amount);

    int getDimension();

    public WorldServer getWorld();
}
