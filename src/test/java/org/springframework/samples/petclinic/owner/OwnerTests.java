import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.owner.Owner;

public class OwnerTests {

    @Test
    void testOwner() {
        Owner owner = new Owner();
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setAddress("123 Main St");
        owner.setCity("Anytown");
        owner.setTelephone("123-456-7890");
    }
}
