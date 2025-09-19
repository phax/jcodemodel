import java.time.Instant;
import java.util.List;

public class Animal {
    private final Instant dob;
    private final long id;
    private String name;
    private final Animal[] parents;
    private final List<Animal> children;

    public Animal(Instant dob,
        long id,
        Animal[] parents,
        List<Animal> children) {
        this.dob = dob;
        this.id = id;
        this.parents = parents;
        this.children = children;
    }

    /**
     * @return the {@link #dob}
     */
    public Instant getDob() {
        return dob;
    }

    /**
     * @return the {@link #id}
     */
    public long getId() {
        return id;
    }

    /**
     * set the {@link #name}
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the {@link #name}
     */
    public String getName() {
        return name;
    }

    /**
     * @return the {@link #parents}
     */
    public Animal[] getParents() {
        return parents;
    }

    /**
     * @return the {@link #children}
     */
    public List<Animal> getChildren() {
        return children;
    }
}
