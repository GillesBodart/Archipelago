package org.archipelago.test.main;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.archipelago.core.connection.GraphConnect;
import org.archipelago.core.domain.GeneratedScript;
import org.archipelago.core.domain.types.ArchipelagoBuilderType;
import org.archipelago.core.domain.types.ArchipelagoFeederType;
import org.archipelago.core.runtime.ArchipelagoBuilderFactory;
import org.archipelago.core.runtime.ArchipelagoFeederFactory;
import org.archipelago.test.domain.library.Author;
import org.archipelago.test.domain.library.Book;
import org.archipelago.test.domain.library.Librarian;
import org.archipelago.test.domain.library.Library;
import org.archipelago.test.domain.school.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Gilles Bodart on 19/08/2016.
 */
public class MainTest {

    protected final static Logger LOGGER = LogManager.getLogger(MainTest.class);

    private final static ArchipelagoBuilderType TEST_TYPE = ArchipelagoBuilderType.RELATIONAL_SQL;
    private final static String TEST_SCHOOL_PATH = "C:\\Sand\\IdeaProjects\\Archipelago\\src\\main\\java\\org\\archipelago\\test\\domain\\school";
    private final static String TEST_LIBRARY_PATH = "C:\\Sand\\IdeaProjects\\Archipelago\\src\\main\\java\\org\\archipelago\\test\\domain\\library";

    private final static String TEST_CASE = "School";

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        GraphConnect gc = GraphConnect.getInstance();
        //Session session = driver.session();
        ClassRoom cr = new ClassRoom("l003", 23, true, false, false);
        Teacher m = new Teacher();
        m.setDateOfBirth(LocalDate.of(2017, 07, 10));
        m.setFirstName("Marie");
        m.setLastName("Van Cutsem");
        m.setSexe("F");
        m.setDiploma("Logopède");
        Teacher g = new Teacher();
        g.setDateOfBirth(LocalDate.of(2017, 07, 10));
        g.setFirstName("Gilles");
        g.setLastName("Bodart");
        g.setSexe("M");
        g.setDiploma("Master");
        gc.link(m, cr,  ,false);
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

    private static void testBuilder(String testCase) throws ClassNotFoundException, IOException {
        List<GeneratedScript> scripts = new ArrayList<>();
        switch (testCase.toUpperCase()) {
            case "SCHOOL":
                scripts = ArchipelagoBuilderFactory.generate(Paths.get(TEST_SCHOOL_PATH), TEST_TYPE);
                break;
            case "LIBRARY":
                scripts = ArchipelagoBuilderFactory.generate(Paths.get(TEST_LIBRARY_PATH), TEST_TYPE);
                break;
            default:
                break;
        }
        for (GeneratedScript script : scripts) {
            LOGGER.info(script);
        }
    }

    private static void library() throws ClassNotFoundException, IOException {
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
        List<GeneratedScript> scripts = ArchipelagoFeederFactory.generate(Paths.get(TEST_LIBRARY_PATH).resolve("archipelago"), Lists.newArrayList(book, author,
                librarian, library), ArchipelagoFeederType.ORIENT_DB);
        for (GeneratedScript script : scripts) {
            LOGGER.info(script);
        }
    }

    private static void school() throws ClassNotFoundException, IOException {

        Lesson math8 = new Lesson("Math", 8);
        Lesson math6 = new Lesson("Math", 6);
        Lesson math4 = new Lesson("Math", 4);
        Lesson science6 = new Lesson("Science", 6);
        Lesson science3 = new Lesson("Science", 3);
        Lesson frans5 = new Lesson("Frans", 5);
        Lesson frans6 = new Lesson("Frans", 6);
        Lesson deutch2 = new Lesson("Deutch", 2);
        Lesson deutch4 = new Lesson("Deutch", 4);
        Lesson english2 = new Lesson("English", 2);
        Lesson english4 = new Lesson("English", 4);
        Lesson history = new Lesson("History", 2);
        Lesson geography = new Lesson("Geography", 2);
        Lesson religion = new Lesson("Religion", 2);
        Lesson pE = new Lesson("Physical Education", 2);
        Lesson greek = new Lesson("Greek ancient", 4);

        Promotion p2011 = new Promotion(2011);
        Promotion p2002 = new Promotion(2002);
        Promotion p2010 = new Promotion(2010);

        Teacher gys = new Teacher("Hans", "Gys", null, "M", Lists.newArrayList(deutch2, deutch4, english2, english4), Lists.newArrayList(deutch2, deutch4,
                english2, english4), "Master");
        Teacher goffin = new Teacher("Michel", "Goffin", null, "M", Lists.newArrayList(math8, math6, math4), Lists.newArrayList(math8, math6, math4), "Master");
        Teacher massart = new Teacher("", "Massart", null, "M", Lists.newArrayList(math8, math6, math4), Lists.newArrayList(math8, math6, math4), "Master");
        Teacher gouthers = new Teacher("Pierre-Yve", "Gouthers", null, "M", Lists.newArrayList(frans5, frans6, religion), Lists.newArrayList(math8, math6,
                math4), "Master");
        Teacher jacques = new Teacher("Christian", "Jacques", null, "M", Lists.newArrayList(geography), Lists.newArrayList(geography), "Master");

        Student gilles = new Student("Gilles", "Bodart", LocalDate.of(1992, 4, 14), "M", Lists.newArrayList(math8, science6, deutch2, english4, history,
                geography, religion, frans5, pE), null, null, p2011);
        Student thomasB = new Student("Thomas", "Blondiau", LocalDate.of(1992, 1, 5), "M", Lists.newArrayList(math8, science3, deutch2, english4, history,
                geography, religion, frans5, pE, greek), null, null, p2010);
        Student thomasR = new Student("Thomas", "Reynders", LocalDate.of(1992, 1, 22), "M", Lists.newArrayList(math8, science6, deutch2, english4, history,
                geography, religion, frans5, pE), null, null, p2010);
        Student charly = new Student("Charles-Antoine", "Van Beers", LocalDate.of(1992, 4, 28), "M", Lists.newArrayList(math8, science3, deutch2, english4,
                history, geography, religion, frans5, pE, greek), null, null, p2010);
        Student antoine = new Student("Antoine", "Dumont", LocalDate.of(1992, 12, 28), "M", Lists.newArrayList(math6, science3, deutch4, english4, history,
                geography, religion, frans5, pE, greek), null, null, p2010);
        Student martin = new Student("Martin", "P�rilleux", LocalDate.of(1992, 2, 28), "M", Lists.newArrayList(math6, science3, deutch4, english4, history,
                geography, religion, frans5, pE, greek), null, null, p2010);
        Student benjamin = new Student("Benjamin", "Leroy", LocalDate.of(1992, 10, 31), "M", Lists.newArrayList(math8, science3, deutch2, english4, history,
                geography, religion, frans5, pE, greek), null, null, p2010);
        Student antoineBo = new Student("Antoine", "Bodart", LocalDate.of(1985, 10, 18), "M", Lists.newArrayList(math6, science6, deutch2, english4, history,
                geography, religion, frans5, pE), null, null, p2002);
        Worker cassart = new Worker("", "Cassart", null, "M", null, null);
        List<Room> rooms;
        Room library = new org.archipelago.test.domain.school.Library("Library", Lists.newArrayList("Le livre du voyage", "Le tour du monde ne 80 jours",
                "La nuit des enfants roi", "Le joueur d'�checs"), cassart);
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

        List<Object> objects = Lists.newArrayList();
        objects.addAll(Lists.newArrayList(math8, math6, math4, science6, science3, deutch4, deutch2, english4, english2, history, geography, frans5, frans6,
                religion, pE, greek));
        objects.addAll(Lists.newArrayList(gilles, thomasB, thomasR, charly, antoine, martin, benjamin, antoineBo));
        objects.addAll(Lists.newArrayList(goffin, gouthers, jacques, gys, cassart, massart));
        objects.addAll(Lists.newArrayList(library, l003, l306, c203));
        objects.addAll(Lists.newArrayList(p2010, p2011, p2002));
        objects.add(school);
        List<GeneratedScript> scripts = ArchipelagoFeederFactory.generate(Paths.get(TEST_SCHOOL_PATH).resolve("archipelago"), objects,
                ArchipelagoFeederType.ORIENT_DB);
        List<GeneratedScript> scripts2 = ArchipelagoFeederFactory.generate(Paths.get(TEST_SCHOOL_PATH).resolve("archipelago"), school,
                ArchipelagoFeederType.ORIENT_DB);
        for (GeneratedScript script : scripts) {
            LOGGER.info(script);
        }
        for (GeneratedScript script : scripts2) {
            LOGGER.info(script);
        }
    }
}
