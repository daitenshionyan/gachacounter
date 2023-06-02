package com.hanyans.gachacounter.wrapper;

public enum ItemType {
    CHARACTER, WEAPON;


    public static ItemType getItemType(String typeName) {
        switch (typeName.toUpperCase()) {
            case "CHARACTER":
                return CHARACTER;
            case "LIGHT CONE":
            case "WEAPON":
                return WEAPON;
            default:
                throw new IllegalArgumentException(String.format("Unknown type name <%s>",
                        typeName));
        }
    }
}
