package com.microsoft.azure.spring.data.cosmosdb.repository.integration;

import com.microsoft.azure.spring.data.cosmosdb.domain.Person;
import com.microsoft.azure.spring.data.cosmosdb.exception.CosmosDBAccessException;
import com.microsoft.azure.spring.data.cosmosdb.repository.TestRepositoryConfig;
import com.microsoft.azure.spring.data.cosmosdb.repository.repository.PersonRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.microsoft.azure.spring.data.cosmosdb.common.TestConstants.ADDRESSES;
import static com.microsoft.azure.spring.data.cosmosdb.common.TestConstants.FIRST_NAME;
import static com.microsoft.azure.spring.data.cosmosdb.common.TestConstants.HOBBIES;
import static com.microsoft.azure.spring.data.cosmosdb.common.TestConstants.ID_1;
import static com.microsoft.azure.spring.data.cosmosdb.common.TestConstants.LAST_NAME;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestRepositoryConfig.class)
public class EtagIT {

    @Autowired
    PersonRepository personRepository;

    @After
    public void cleanup() {
        personRepository.deleteAll();
    }

    private static Person createPerson() {
        return new Person(null, FIRST_NAME, LAST_NAME, HOBBIES, ADDRESSES);
    }

    @Test
    public void testCrudOperationsShouldApplyEtag() {
        final Person insertedPerson = personRepository.save(createPerson());
        Assert.assertNotNull(insertedPerson.getEtag());

        insertedPerson.setFirstName(LAST_NAME);
        final Person updatedPerson = personRepository.save(insertedPerson);
        Assert.assertNotNull(updatedPerson.getEtag());
        Assert.assertNotEquals(insertedPerson.getEtag(), updatedPerson.getEtag());

        final Optional<Person> foundPerson = personRepository.findById(insertedPerson.getId());
        Assert.assertTrue(foundPerson.isPresent());
        Assert.assertNotNull(foundPerson.get().getEtag());
        Assert.assertEquals(updatedPerson.getEtag(), foundPerson.get().getEtag());
    }

    @Test
    public void testCrudListOperationsShouldApplyEtag() {
        final List<Person> people = new ArrayList<>();
        people.add(createPerson());
        people.add(createPerson());

        final List<Person> insertedPeople = toList(personRepository.saveAll(people));
        insertedPeople.forEach(person -> Assert.assertNotNull(person.getEtag()));

        insertedPeople.forEach(person -> person.setFirstName(LAST_NAME));
        final List<Person> updatedPeople = toList(personRepository.saveAll(insertedPeople));
        for (int i = 0; i < updatedPeople.size(); i++) {
            Person insertedPerson = insertedPeople.get(i);
            Person updatedPerson = updatedPeople.get(i);
            Assert.assertEquals(insertedPerson.getId(), updatedPerson.getId());
            Assert.assertNotNull(updatedPerson.getEtag());
            Assert.assertNotEquals(insertedPerson.getEtag(), updatedPerson.getEtag());
        }

        final List<String> peopleIds = updatedPeople.stream()
                .map(Person::getId)
                .collect(Collectors.toList());
        final List<Person> foundPeople = toList(personRepository.findAllById(peopleIds));
        for (int i = 0; i < foundPeople.size(); i++) {
            Person updatedPerson = updatedPeople.get(i);
            Person foundPerson = foundPeople.get(i);
            Assert.assertNotNull(foundPerson.getEtag());
            Assert.assertEquals(updatedPerson.getEtag(), foundPerson.getEtag());
        }
    }

    private List<Person> toList(Iterable<Person> people) {
        return StreamSupport.stream(people.spliterator(), false)
                .collect(Collectors.toList());
    }

    @Test
    public void testShouldFailIfEtagDoesNotMatch() {
        Person insertedPerson = personRepository.save(createPerson());
        insertedPerson.setFirstName(LAST_NAME);

        Person updatedPerson = personRepository.save(insertedPerson);
        updatedPerson.setEtag(insertedPerson.getEtag());

        try {
            personRepository.save(updatedPerson);
            Assert.fail();
        } catch (CosmosDBAccessException ex) {
        }

        try {
            personRepository.delete(updatedPerson);
            Assert.fail();
        } catch (CosmosDBAccessException ex) {
        }
    }

}
