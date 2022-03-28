package com.github.alexthe668.cloudstorage.entity;

public enum GloveGesture {
    IDLE(true, false),
    GRAB(false, false),
    PUNCH(false, true),
    WAVE(false, true),
    POINT(false, true),
    FLIPOFF(false, true);

    private boolean applyRot;
    private boolean holdInFront;

    GloveGesture(boolean applyRot, boolean holdInFront){
        this.applyRot = applyRot;
        this.holdInFront = holdInFront;
    }

    public boolean holdsInFront(){
        return holdInFront;
    }

    public boolean appliesXRot(){
        return applyRot;
    }
}
