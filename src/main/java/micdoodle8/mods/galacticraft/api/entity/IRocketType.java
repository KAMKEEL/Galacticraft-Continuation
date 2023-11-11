package micdoodle8.mods.galacticraft.api.entity;

import net.minecraft.util.StatCollector;

public interface IRocketType {

    enum EnumRocketType {

        DEFAULT(0, "", false, 2),
        INVENTORY27(1, StatCollector.translateToLocal("gui.rocketType.0"), false, 20),
        INVENTORY36(2, StatCollector.translateToLocal("gui.rocketType.1"), false, 38),
        INVENTORY54(3, StatCollector.translateToLocal("gui.rocketType.2"), false, 56),
        PREFUELED(4, StatCollector.translateToLocal("gui.rocketType.3"), true, 2);

        private final int index;
        private final String tooltip;
        private final boolean preFueled;
        private final int inventorySpace;

        EnumRocketType(int index, String tooltip, boolean preFueled, int inventorySpace) {
            this.index = index;
            this.tooltip = tooltip;
            this.preFueled = preFueled;
            this.inventorySpace = inventorySpace;
        }

        public String getTooltip() {
            return this.tooltip;
        }

        public int getIndex() {
            return this.index;
        }

        public int getInventorySpace() {
            return this.inventorySpace;
        }

        public boolean getPreFueled() {
            return this.preFueled;
        }
    }

    EnumRocketType getType();

    int getRocketTier();
}
