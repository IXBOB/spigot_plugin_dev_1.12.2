package com.ixbob.myplugin;

import java.util.Map;

public class GunProperties {

    public enum GunType {
        SHOU_QIANG("shou_qiang","shou_qiang_ammo","shou_qiang_current_magazine_ammo"),
        BU_QIANG("bu_qiang","bu_qiang_ammo","bu_qiang_current_magazine_ammo"),
        XIANDAN_QIANG("xiandan_qiang", "xiandan_qiang_ammo", "xiandan_qiang_current_magazine_ammo"),
        DIANYONG_QIANG("dianyong_qiang", "dianyong_qiang_ammo", "dianyong_qiang_current_magazine_ammo"),
        GUCI("guci", "guci_ammo", "guci_current_magazine_ammo"); //骨刺，狙击枪

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
            GunType.BU_QIANG, 0.5f,
            GunType.XIANDAN_QIANG, 0.5f,
            GunType.DIANYONG_QIANG, 0.1f,
            GunType.GUCI, 1.0f
    );
    public static final Map<GunType, Float> gunDamage = Map.of(
            GunType.SHOU_QIANG, 4.5f,
            GunType.BU_QIANG, 2.5f,
            GunType.XIANDAN_QIANG, 1.5f,
            GunType.DIANYONG_QIANG, 1.5f,
            GunType.GUCI, 9.8f
    );

    public static final Map<GunType, Float> gunReloadAmmoTime = Map.of(
            GunType.SHOU_QIANG, 2.0f,
            GunType.BU_QIANG, 3.0f,
            GunType.XIANDAN_QIANG, 4.0f,
            GunType.DIANYONG_QIANG, 3.0f,
            GunType.GUCI, 3.5f
    );
    public static final Map<GunType, Integer> gunMagazineFullAmmo = Map.of(
            GunType.SHOU_QIANG, 20,
            GunType.BU_QIANG, 30,
            GunType.XIANDAN_QIANG, 20,
            GunType.DIANYONG_QIANG, 45,
            GunType.GUCI, 25
    );
    public static final Map<GunType, Float> gunBulletMoveSpeed = Map.of(
            GunType.SHOU_QIANG, 1.1f,
            GunType.BU_QIANG, 1.3f,
            GunType.XIANDAN_QIANG, 1.2f,
            GunType.DIANYONG_QIANG, 1.3f,
            GunType.GUCI, 1.3f
    );
    public static final Map<GunType, Float> gunBulletMoveDistance = Map.of(
            GunType.SHOU_QIANG, 20.0f,
            GunType.BU_QIANG, 50.0f,
            GunType.XIANDAN_QIANG, 15.0f,
            GunType.DIANYONG_QIANG, 40.0f,
            GunType.GUCI, 50.0f
    );
    public static final Map<GunType, Integer> gunDurabilityLegacy = Map.of(
            GunType.SHOU_QIANG, 59,
            GunType.BU_QIANG, 131,
            GunType.XIANDAN_QIANG, 131,
            GunType.DIANYONG_QIANG, 1561,
            GunType.GUCI, 32
    );
    public static final int swordHitGetCoin = 5;
    public static final Map<GunType, Integer> gunHitHeadGetCoin = Map.of(
            GunType.SHOU_QIANG, 10,
            GunType.BU_QIANG, 7,
            GunType.XIANDAN_QIANG, 3,
            GunType.DIANYONG_QIANG, 3,
            GunType.GUCI, 18
    );
    public static final Map<GunType, Integer> gunHitDefaultGetCoin = Map.of(
            GunType.SHOU_QIANG, 7,
            GunType.BU_QIANG, 4,
            GunType.XIANDAN_QIANG, 2,
            GunType.DIANYONG_QIANG, 2,
            GunType.GUCI, 12
    );

    public static GunType getGunTypeByString(String gunName) {
        switch (gunName) {
            case "shou_qiang": return GunType.SHOU_QIANG;
            case "bu_qiang": return GunType.BU_QIANG;
            case "xiandan_qiang": return GunType.XIANDAN_QIANG;
            case "dianyong_qiang": return GunType.DIANYONG_QIANG;
            case "guci": return GunType.GUCI;
        }
        return null;
    }
}
