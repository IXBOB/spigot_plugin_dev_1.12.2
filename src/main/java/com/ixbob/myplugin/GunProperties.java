package com.ixbob.myplugin;

import java.util.Map;

public class GunProperties {

    public enum GunType {
        SHOU_QIANG("shou_qiang","shou_qiang_ammo","shou_qiang_current_magazine_ammo"),
        BU_QIANG("bu_qiang","bu_qiang_ammo","bu_qiang_current_magazine_ammo");

        private final String typeName;
        private final String playerAmmoMetadataKey;
        private final String playerMagazineAmmoMetadataKey;

        GunType(String typeName, String playerAmmoMetadataKey, String playerMagazineAmmoMetadataKey) {
            this.typeName = typeName;
            this.playerAmmoMetadataKey = playerAmmoMetadataKey;
            this.playerMagazineAmmoMetadataKey = playerMagazineAmmoMetadataKey;
        }
        public String getTypeName() {
            return typeName;
        }
        public String getPlayerAmmoMetadataKey() {
            return playerAmmoMetadataKey;
        }
        public String getPlayerMagazineAmmoMetadataKey() {
            return playerMagazineAmmoMetadataKey;
        }
    }

    public static final Map<GunType, Float> gunCoolDownTime = Map.of(
            GunType.SHOU_QIANG, 1.0f,
            GunType.BU_QIANG, 0.5f
    );
    public static final Map<GunType, Float> gunDamage = Map.of(
            GunType.SHOU_QIANG, 4.5f,
            GunType.BU_QIANG, 2.5f
    );

    public static final Map<GunType, Float> gunReloadAmmoTime = Map.of(
            GunType.SHOU_QIANG, 4.0f,
            GunType.BU_QIANG, 5.0f
    );
    public static final Map<GunType, Integer> gunMagazineFullAmmo = Map.of(
            GunType.SHOU_QIANG, 20,
            GunType.BU_QIANG, 30
    );
    public static final Map<GunType, Integer> gunDurabilityLegacy = Map.of(
            GunType.SHOU_QIANG, 59,
            GunType.BU_QIANG, 131
    );
    public static final int swordHitGetCoin = 5;
    public static final Map<GunType, Integer> gunHitHeadGetCoin = Map.of(
            GunType.SHOU_QIANG, 10,
            GunType.BU_QIANG, 7
    );
    public static final Map<GunType, Integer> gunHitDefaultGetCoin = Map.of(
            GunType.SHOU_QIANG, 7,
            GunType.BU_QIANG, 4
    );
}
