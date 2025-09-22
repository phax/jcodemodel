package com.helger.jcodemodel.examples.plugin.csv.immutable;

import java.lang.ref.WeakReference;
import java.time.Instant;

public class WeirdReference
    extends WeakReference
{
    private final Instant created;
    private boolean visible;

    public WeirdReference(Object arg0, Instant created) {
        super(arg0);
        this.created = created;
    }

    /**
     * @return the {@link #created}
     */
    public Instant getCreated() {
        return created;
    }

    /**
     * set the {@link #visible}
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * @return the {@link #visible}
     */
    public boolean getVisible() {
        return visible;
    }
}
