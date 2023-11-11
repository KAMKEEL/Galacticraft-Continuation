package micdoodle8.mods.galacticraft.core.entities.player;

public enum EnumGravity {

    down(0, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F),
    up(1, 1.0F, 0.0F, 0.0F, -1.0F, 0.0F, -1.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 0.0F),
    west(2, 0.0F, -1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.5F, 1.0F, 0.0F, 0.0F, -1.0F, 1.0F, 0.0F),
    east(3, 0.0F, 1.0F, -1.0F, 0.0F, 0.0F, 0.0F, -0.5F, -1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F),
    south(4, 1.0F, 0.0F, 0.0F, 0.0F, -1.0F, 0.5F, 0.0F, 0.0F, 0.0F, -1.0F, 0.0F, 1.0F, 1.0F),
    north(5, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F, -0.5F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, -1.0F);

    private final int intValue;
    private final float pitchGravityX;
    private final float pitchGravityY;
    private final float yawGravityX;
    private final float yawGravityY;
    private final float yawGravityZ;
    private final float thetaX;
    private final float thetaZ;
    private final float sneakVecX;
    private final float sneakVecY;
    private final float sneakVecZ;
    private final float eyeVecX;
    private final float eyeVecY;
    private final float eyeVecZ;
    private static final EnumGravity[] GDirections = { down, up, west, east, south, north };

    EnumGravity(int value, float pitchX, float pitchY, float yawX, float yawY, float yawZ, float thetaX, float thetaZ,
            float sneakX, float sneakY, float sneakZ, float eyeX, float eyeY, float eyeZ) {
        this.intValue = value;
        this.pitchGravityX = pitchX;
        this.pitchGravityY = pitchY;
        this.yawGravityX = yawX;
        this.yawGravityY = yawY;
        this.yawGravityZ = yawZ;
        this.thetaX = thetaX;
        this.thetaZ = thetaZ;
        this.sneakVecX = sneakX;
        this.sneakVecY = sneakY;
        this.sneakVecZ = sneakZ;
        this.eyeVecX = eyeX;
        this.eyeVecY = eyeY;
        this.eyeVecZ = eyeZ;
    }

    public int getIntValue() {
        return this.intValue;
    }

    public float getPitchGravityX() {
        return this.pitchGravityX;
    }

    public float getPitchGravityY() {
        return this.pitchGravityY;
    }

    public float getYawGravityX() {
        return this.yawGravityX;
    }

    public float getYawGravityY() {
        return this.yawGravityY;
    }

    public float getYawGravityZ() {
        return this.yawGravityZ;
    }

    public float getThetaX() {
        return this.thetaX;
    }

    public float getThetaZ() {
        return this.thetaZ;
    }

    public float getSneakVecX() {
        return this.sneakVecX;
    }

    public float getSneakVecY() {
        return this.sneakVecY;
    }

    public float getSneakVecZ() {
        return this.sneakVecZ;
    }

    public float getEyeVecX() {
        return this.eyeVecX;
    }

    public float getEyeVecY() {
        return this.eyeVecY;
    }

    public float getEyeVecZ() {
        return this.eyeVecZ;
    }

    public static EnumGravity[] getGDirections() {
        return GDirections;
    }
}
