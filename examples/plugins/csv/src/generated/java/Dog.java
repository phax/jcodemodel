import java.time.Instant;
import java.util.List;

public class Dog
    extends Animal
{
    private final String species;
    private String master;

    public Dog(Instant dob,
        long id,
        Animal[] parents,
        List<Animal> children,
        String species) {
        super(dob, id, parents, children);
        this.species = species;
    }

    /**
     * @return the {@link #species}
     */
    public String getSpecies() {
        return species;
    }

    /**
     * set the {@link #master}
     */
    public void setMaster(String master) {
        this.master = master;
    }

    /**
     * @return the {@link #master}
     */
    public String getMaster() {
        return master;
    }
}
