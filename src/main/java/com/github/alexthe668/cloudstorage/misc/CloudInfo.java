package com.github.alexthe668.cloudstorage.misc;

public class CloudInfo {

    private int balloonColor;
    private int usedSlots;
    private int slotCount;
    private int usedStaticSlots;
    private int staticSlotCount;

    public CloudInfo(int balloonColor, int usedSlots, int slotCount, int usedStaticSlots, int staticSlotCount) {
        this.balloonColor = balloonColor;
        this.usedSlots = usedSlots;
        this.slotCount = slotCount;
        this.usedStaticSlots = usedStaticSlots;
        this.staticSlotCount = staticSlotCount;
    }

    public int getBalloonColor() {
        return balloonColor;
    }

    public int getUsedSlots() {
        return usedSlots;
    }

    public int getSlotCount() {
        return slotCount;
    }

    public int getUsedStaticSlots() {
        return usedStaticSlots;
    }

    public int getStaticSlotCount() {
        return staticSlotCount;
    }
}
