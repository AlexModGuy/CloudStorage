package com.github.alexthe668.cloudstorage.entity;

public interface BalloonFlyer {

    default boolean stopFlying(){
        return false;
    }
}
