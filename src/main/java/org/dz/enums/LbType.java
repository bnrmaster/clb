package org.dz.enums;

public enum LbType {
    ROUND("round", 1),
    RANDOM("random", 2);

    private String name;
    private int index;

    private LbType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public static LbType getByName(String name) {
        for (LbType c : LbType.values()) {
            if (c.name.equals(name)) {
                return c;
            }
        }
        return null;
    }
}
