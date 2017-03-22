package Flokscha.playground.world;

import net.minecraft.world.DimensionType;

public class MyWorldProviderSurface extends MyWorldProvider{

    public void WorldProviderSurface() {
    }


    @Override
    public DimensionType getDimensionType() {
        for (DimensionType d: DimensionType.values()){
            if (d.getName() == "AdventureTime"){
                return d;
            }
        }
        return  DimensionType.OVERWORLD;
    }

    public boolean canDropChunk(int p_canDropChunk_1_, int p_canDropChunk_2_) {
        return !this.world.isSpawnChunk(p_canDropChunk_1_, p_canDropChunk_2_) || !this.world.provider.getDimensionType().shouldLoadSpawn();
    }
}