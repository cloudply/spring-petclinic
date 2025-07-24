package org.springframework.samples.petclinic.owner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.testng.annotations.*;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class OwnerRepositoryTestNG {

    @Autowired
    private OwnerRepository ownerRepository;

    private Owner testOwner;

    @BeforeClass
    public void setupClass() {
        System.out.println("Setting up OwnerRepositoryTestNG class");
    }

    @BeforeMethod
    public void setup() {
        testOwner = new Owner();
        testOwner.setFirstName("Test");
        testOwner.setLastName("Owner");
        testOwner.setAddress("123 Test St");
        testOwner.setCity("Test City");
        testOwner.setTelephone("1234567890");
        testOwner.setAge(35);
        ownerRepository.save(testOwner);
    }

    @Test(groups = "repository")
    public void testFindByLastName() {
        Page<Owner> owners = ownerRepository.findByLastName("Owner", PageRequest.of(0, 10));
        assertThat(owners).isNotEmpty();
        assertThat(owners.getContent().get(0).getLastName()).isEqualTo("Owner");
    }

    @Test(groups = "repository", dependsOnMethods = "testFindByLastName")
    public void testFindById() {
        Owner owner = ownerRepository.findById(testOwner.getId());
        assertThat(owner).isNotNull();
        assertThat(owner.getFirstName()).isEqualTo("Test");
        assertThat(owner.getLastName()).isEqualTo("Owner");
    }

    @Test(groups = "repository")
    public void testFindPetTypes() {
        List<PetType> petTypes = ownerRepository.findPetTypes();
        assertThat(petTypes).isNotEmpty();
    }

    @Test(groups = "repository")
    public void testFindOwnersByAge() {
        Page<Owner> owners = ownerRepository.findOwnersByAge(35, PageRequest.of(0, 10));
        assertThat(owners).isNotEmpty();
        assertThat(owners.getContent().get(0).getAge()).isEqualTo(35);
    }

    @Test(groups = "repository", dependsOnMethods = {"testFindById", "testFindByLastName"})
    public void testDeleteById() {
        Integer id = testOwner.getId();
        ownerRepository.deleteById(id);
        
        // Create a new pageable to search for the deleted owner
        Pageable pageable = PageRequest.of(0, 10);
        Page<Owner> owners = ownerRepository.findByLastName("Owner", pageable);
        
        // Check if the owner with the specific ID is no longer in the results
        boolean ownerFound = false;
        for (Owner owner : owners.getContent()) {
            if (owner.getId().equals(id)) {
                ownerFound = true;
                break;
            }
        }
        
        assertThat(ownerFound).isFalse();
    }

    @DataProvider(name = "ownerAgeData")
    public Object[][] ownerAgeData() {
        return new Object[][] {
            {25},
            {30},
            {40},
            {50}
        };
    }

    @Test(groups = "data", dataProvider = "ownerAgeData")
    public void testFindOwnersByDifferentAges(Integer age) {
        Owner owner = new Owner();
        owner.setFirstName("Age");
        owner.setLastName("Test");
        owner.setAddress("123 Age St");
        owner.setCity("Age City");
        owner.setTelephone("1234567890");
        owner.setAge(age);
        ownerRepository.save(owner);
        
        Page<Owner> owners = ownerRepository.findOwnersByAge(age, PageRequest.of(0, 10));
        assertThat(owners).isNotEmpty();
        boolean ageFound = false;
        for (Owner foundOwner : owners.getContent()) {
            if (foundOwner.getAge().equals(age)) {
                ageFound = true;
                break;
            }
        }
        assertThat(ageFound).isTrue();
    }

    @AfterMethod
    public void tearDown() {
        System.out.println("Tearing down after test method");
    }

    @AfterClass
    public void tearDownClass() {
        System.out.println("Tearing down OwnerRepositoryTestNG class");
    }
}
