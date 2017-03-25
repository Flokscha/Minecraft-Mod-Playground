package Flokscha.playground.items;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItems {
    public static Flokscha.playground.items.stockupgrade stockupgrade;

    public static void init() {
        stockupgrade = new stockupgrade();
    }
    @SideOnly(Side.CLIENT)
    public static void initModels() {
        stockupgrade.initModel();
    }
}
