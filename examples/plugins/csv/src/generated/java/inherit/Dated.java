package inherit;

import java.io.Serializable;
import java.time.Instant;

public class Dated
    implements Serializable
{
    private final Instant created;

    public Dated(Instant created) {
        this.created = created;
    }

    /**
     * @return the {@link #created}
     */
    public Instant getCreated() {
        return created;
    }
}
