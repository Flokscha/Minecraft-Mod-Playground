package Flokscha.playground.world;

import net.minecraft.world.WorldProvider;

public abstract class MyWorldProvider extends WorldProvider{
    private long MAX_DAYTIME_LONG = 24000L*3;
    private float MAX_DAYTIME_FLOAT = 24000F*3;

    public void WorldProvider() {
    }

    public boolean canDropChunk(int p_canDropChunk_1_, int p_canDropChunk_2_) {
        return !this.world.isSpawnChunk(p_canDropChunk_1_, p_canDropChunk_2_) || !this.world.provider.getDimensionType().shouldLoadSpawn();
    }


    @Override
    public float calculateCelestialAngle(long p_calculateCelestialAngle_1_, float p_calculateCelestialAngle_3_) {
        int i = (int)(p_calculateCelestialAngle_1_ % MAX_DAYTIME_LONG);
        float f = ((float)i + p_calculateCelestialAngle_3_) / MAX_DAYTIME_FLOAT - 0.25F;
        if(f < 0.0F) {
            ++f;
        }

        if(f > 1.0F) {
            --f;
        }

        float f1 = 1.0F - (float)((Math.cos((double)f * 3.141592653589793D) + 1.0D) / 2.0D);
        f += (f1 - f) / 3.0F;
        return f;
    }

    @Override
    public int getMoonPhase(long p_getMoonPhase_1_) {
        return (int)(p_getMoonPhase_1_ / MAX_DAYTIME_LONG % 8L + 8L) % 8;
    }

}