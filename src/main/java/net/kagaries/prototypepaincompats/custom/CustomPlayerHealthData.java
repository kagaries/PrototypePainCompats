package net.kagaries.prototypepaincompats.custom;

public class CustomPlayerHealthData {
    //SANITY
    public float mood = 100.0F; //Custom internal sanity value to be applied across all mods that could benefit from it
    public float panic = 0.0F;

    //MOD SPECIFIC
    //MEKANISM
    public float radiation_sickness = 0.0F;

    //ZOMBIE
    public float zombieInfection = 0.0F;
    public float craving = 0.0F;
    public boolean brainDecay = false;

    //WITHER_STORM
    public float wither_sickness = 0.0F;
}
