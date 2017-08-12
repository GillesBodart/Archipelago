package org.archipelago.test.main;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.archipelago.core.framework.Archipelago;
import org.archipelago.test.domain.FriendOf;
import org.archipelago.test.domain.Road;
import org.archipelago.test.domain.got.City;
import org.archipelago.test.domain.library.Author;
import org.archipelago.test.domain.library.Book;
import org.archipelago.test.domain.library.Librarian;
import org.archipelago.test.domain.library.Library;
import org.archipelago.test.domain.school.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Created by Gilles Bodart on 19/08/2016.
 */
public class MainTest {

    protected final static Logger LOGGER = LogManager.getLogger(MainTest.class);

    public static void main(String[] args) throws Exception {
        Archipelago a = Archipelago.getInstance();

        a.persist(school());

        //a.persist(school());

        /*ArchipelagoQuery aq = a.getQueryBuilder()
                .of(Student.class)
                .where(of("lastName", "Bodart"), ConditionQualifier.EQUAL)
                .and(of("firstName", "gilles"), ConditionQualifier.EQUAL)
                .or(of("firstName", "Thomas"), ConditionQualifier.EQUAL)
                .build();

        List<Object> nodes = a.execute(aq);*/
        //nodes.stream().forEach(System.out::println);
    }

    private static void testFeeder(String testCase) throws ClassNotFoundException, IOException {
        switch (testCase.toUpperCase()) {
            case "SCHOOL":
                school();
                break;
            case "LIBRARY":
                library();
                break;
            default:
                break;
        }
    }


    private static Library library() throws ClassNotFoundException, IOException {
        Book book = new Book();
        book.setAmountPages(200);
        book.setPublishDate(new Date());
        book.setTitle("La nuit des enfants roi");
        Author author = new Author();
        author.setName("Bernard");
        author.setLastName("Lanternic");
        author.setBooks(Lists.newArrayList(book));
        Librarian librarian = new Librarian();
        librarian.setLastName("Bodart");
        librarian.setName("Gilles");
        Library library = new Library();
        library.setBooks(Lists.newArrayList(book));
        librarian.setLibrary(library);
        book.setAuthor(author);
        return library;
    }

    private static School school() throws ClassNotFoundException, IOException {

        Lesson math8 = new Lesson("Math", 8l);
        Lesson math6 = new Lesson("Math", 6l);
        Lesson math4 = new Lesson("Math", 4l);
        Lesson science6 = new Lesson("Science", 6l);
        Lesson science3 = new Lesson("Science", 3l);
        Lesson frans5 = new Lesson("Frans", 5l);
        Lesson frans6 = new Lesson("Frans", 6l);
        Lesson dutch2 = new Lesson("Dutch", 2l);
        Lesson dutch4 = new Lesson("Dutch", 4l);
        Lesson english2 = new Lesson("English", 2l);
        Lesson english4 = new Lesson("English", 4l);
        Lesson history = new Lesson("History", 2l);
        Lesson geography = new Lesson("Geography", 2l);
        Lesson religion = new Lesson("Religion", 2l);
        Lesson pE = new Lesson("Physical Education", 2l);
        Lesson greek = new Lesson("Greek ancient", 4l);

        Promotion p2011 = new Promotion(2011);
        Promotion p2002 = new Promotion(2002);
        Promotion p2010 = new Promotion(2010);

        Teacher gys = new Teacher("Hans", "Gys", null, "M", Lists.newArrayList(dutch2, dutch4, english2, english4), Lists.newArrayList(dutch2, dutch4,
                english2, english4), "Master");
        Teacher goffin = new Teacher("Michel", "Goffin", null, "M", Lists.newArrayList(math8, math6, math4), Lists.newArrayList(math8, math6, math4), "Master");
        Teacher massart = new Teacher("Gabriel", "Massart", null, "M", Lists.newArrayList(math8, math6, math4), Lists.newArrayList(math8, math6, math4), "Master");
        Teacher gouthers = new Teacher("Yves", "Gouthers", null, "M", Lists.newArrayList(frans5, frans6, religion), Lists.newArrayList(frans5, frans6, religion), "Master");
        Teacher jacques = new Teacher("Christian", "Jacques", null, "M", Lists.newArrayList(geography), Lists.newArrayList(geography), "Master");

        Student gilles = new Student("Gilles", "Bodart", LocalDate.of(1992, 4, 14), "M", Lists.newArrayList(math8, science6, dutch2, english4, history,
                geography, religion, frans5, pE), null, null, p2011);
        Student thomasB = new Student("Thomas", "Blondiau", LocalDate.of(1992, 1, 5), "M", Lists.newArrayList(math8, science3, dutch2, english4, history,
                geography, religion, frans5, pE, greek), null, null, p2010);
        Student thomasR = new Student("Thomas", "Reynders", LocalDate.of(1992, 1, 22), "M", Lists.newArrayList(math8, science6, dutch2, english4, history,
                geography, religion, frans5, pE), null, null, p2010);
        Student charly = new Student("Charles-Antoine", "Van Beers", LocalDate.of(1992, 4, 28), "M", Lists.newArrayList(math8, science3, dutch2, english4,
                history, geography, religion, frans5, pE, greek), null, null, p2010);
        Student antoine = new Student("Antoine", "Dumont", LocalDate.of(1992, 12, 28), "M", Lists.newArrayList(math6, science3, dutch4, english4, history,
                geography, religion, frans5, pE, greek), null, null, p2010);
        Student martin = new Student("Martin", "Périlleux", LocalDate.of(1992, 2, 28), "M", Lists.newArrayList(math6, science3, dutch4, english4, history,
                geography, religion, frans5, pE, greek), null, null, p2010);
        Student benjamin = new Student("Benjamin", "Leroy", LocalDate.of(1992, 10, 31), "M", Lists.newArrayList(math8, science3, dutch2, english4, history,
                geography, religion, frans5, pE, greek), null, null, p2010);
        Student antoineBo = new Student("Antoine", "Bodart", LocalDate.of(1985, 10, 18), "M", Lists.newArrayList(math6, science6, dutch2, english4, history,
                geography, religion, frans5, pE), null, null, p2002);
        Worker cassart = new Worker("", "Cassart", null, "M", null, null);
        List<Room> rooms;
        Room library = new org.archipelago.test.domain.school.Library("Library", Lists.newArrayList("Le livre du voyage", "Le tour du monde en 80 jours",
                "La nuit des enfants roi", "Le joueur d'échecs"), cassart);
        cassart.setInChargeOf(Lists.newArrayList(library));
        Room l003 = new ClassRoom("L003", 30, true, false, true);
        Room c203 = new ClassRoom("C203", 30, true, false, true);
        Room l306 = new ClassRoom("L306", 50, true, true, false);

        gilles.setFriends(Lists.newArrayList(thomasB, thomasR, charly, antoine, martin, benjamin, antoineBo));
        gilles.setFamilyMember(Lists.newArrayList(antoineBo));
        thomasB.setFriends(Lists.newArrayList(gilles, thomasR, charly, antoine, martin, benjamin));
        thomasR.setFriends(Lists.newArrayList(thomasB, gilles, charly, antoine, martin, benjamin));
        charly.setFriends(Lists.newArrayList(thomasB, thomasR, gilles, antoine, martin, benjamin));
        antoine.setFriends(Lists.newArrayList(thomasB, thomasR, charly, gilles, martin, benjamin));
        martin.setFriends(Lists.newArrayList(thomasB, thomasR, charly, antoine, gilles, benjamin));
        benjamin.setFriends(Lists.newArrayList(thomasB, thomasR, charly, antoine, martin, gilles));

        School school = new School();
        school.setName("Saint Louis Namur");
        school.setDirector(gys);
        school.setTeachers(Lists.newArrayList(goffin, gouthers, jacques, gys, massart));
        school.setStudents(Lists.newArrayList(gilles, thomasB, thomasR, charly, antoine, martin, benjamin, antoineBo));
        school.setRooms(Lists.newArrayList(library, l003, l306, c203));
        school.setWorkers(Lists.newArrayList(cassart));
        return school;

    }
}
