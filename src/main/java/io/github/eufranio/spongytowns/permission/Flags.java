package io.github.eufranio.spongytowns.permission;

/**
 * Created by Frani on 14/04/2018.
 */
public enum Flags {

    ENTRY("entry", true),

    BUILD("build", false),

    ATTACK("attack", false),

    INTERACT_BLOCK("interact-block", false),

    INTERACT_ENTITY("interact-entity", false);

    private String id;
    private boolean def;

    Flags(String id, boolean def) {
        this.id = id;
        this.def = def;
    }

    public String getId() {
        return id;
    }

    public boolean getDefault() {
        return this.def;
    }
}
