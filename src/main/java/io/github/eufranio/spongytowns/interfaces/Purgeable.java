package io.github.eufranio.spongytowns.interfaces;

import java.time.Instant;

/**
 * Created by Frani on 14/03/2018.
 */
public interface Purgeable {

    String getLastActive();

    default Instant getLastActiveInstant() {
        return Instant.parse(this.getLastActive());
    }

    String getDateCreated();

    default Instant getCreatedInstant() {
        return Instant.parse(this.getDateCreated());
    }

    void setLastActive(String lastActive);

}
