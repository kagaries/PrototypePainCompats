package net.kagaries.prototypepaincompats.custom.moodles;

import static net.adinvas.prototype_pain.client.moodles.MoodleController.registerMoodle;

public class CustomMoodles {
    public static void init() {

    }

    static {
        registerMoodle(new PanicMoodle());
        registerMoodle(new WitherSicknessMoodle());
    }
}
