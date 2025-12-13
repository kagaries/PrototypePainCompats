package net.kagaries.prototypepaincompats.custom;

public class CustomPlayerHealthData {
    //SANITY
    private float sanity = 100.0F; //Custom internal sanity value to be applied across all mods that could benefit from it
    private boolean panic = false;
    private boolean terrified = false;

    //MEKANISM
    private float radiation_sickness = 0.0F;

    //ZOMBIE
    private float zombieInfection = 0.0F;
    private boolean craving = false;
    private boolean brainDecay = false;

    //WITHER_STORM
    private float wither_sickness = 0.0F;
}
