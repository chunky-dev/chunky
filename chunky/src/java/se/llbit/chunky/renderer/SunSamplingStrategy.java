package se.llbit.chunky.renderer;

import se.llbit.util.Registerable;

public enum SunSamplingStrategy implements Registerable {
    Off("Off", "Sun is not sampled with next event estimation."),
    Fast("Fast", "Fast sun sampling algorithm. Lower noise but does not correctly model some visual effects."),
    HighQuality("High Quality", "High quality sun sampling. More noise but correctly models visual effects.");

    private final String friendlyName;
    private final String description;

    SunSamplingStrategy(String name, String description) {
        this.friendlyName = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return this.friendlyName;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getId() {
        return this.name();
    }
}
