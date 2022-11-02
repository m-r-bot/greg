package org.example.types;

public enum FederalState {
    DE("Deutschland"),
    BW("Baden-Württemberg"),
    BY("Bayern"),
    ST("Sachsen-Anhalt"),
    BB("Brandenburg"),
    HE("Hessen"),
    NW("Nordrhein-Westfalen"),
    RP("Rheinland-Pfalz"),
    SL("Saarland"),
    BE("Berlin"),
    HB("Bremen"),
    HH("Hamburg"),
    SN("Sachsen"),
    TH("Thüringen"),
    SH("Schleswig-Holstein"),
    MV("Mecklenburg-Vorpommern"),
    NV("Nordrhein-Westfalen"),
    NI("Niedersachsen");

    private final String federalStateName;

    FederalState(final String federalStateName) {
        this.federalStateName = federalStateName;
    }

    public String getFederalStateName() {
        return this.federalStateName;
    }
}
