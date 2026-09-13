package com.eam_simulator.domain.unit;

public enum UnitType {
    SERF(UnitCategory.ECONOMY, "SERF"),
    BUILDER(UnitCategory.ECONOMY, "BUILDER");

    private final UnitCategory category;
    private final String displayName;

    UnitType(UnitCategory category, String displayName) {
        this.category = category;
        this.displayName = displayName;
    }

    public boolean isArmy() {
        return this.category == UnitCategory.ARMY;
    }

    public boolean isEconomy() {
        return this.category == UnitCategory.ECONOMY;
    }

    public String getDisplayName() {
        return displayName;
    }
}
