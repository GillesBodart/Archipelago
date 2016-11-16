package org.archipelago.test.main;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.archipelago.core.domain.GeneratedScript;
import org.archipelago.core.domain.types.ArchipelagoBuilderType;
import org.archipelago.core.domain.types.ArchipelagoFeederType;
import org.archipelago.core.runtime.ArchipelagoBuilderFactory;
import org.archipelago.core.runtime.ArchipelagoFeederFactory;
import org.archipelago.test.domain.library.Author;
import org.archipelago.test.domain.library.Book;
import org.archipelago.test.domain.library.Librarian;
import org.archipelago.test.domain.library.Library;
import org.archipelago.test.domain.school.Person;
import org.archipelago.test.domain.school.School;
import org.archipelago.test.domain.school.Student;
import org.archipelago.test.domain.school.Teacher;

import com.google.common.collect.Lists;

/**
 * Created by Gilles Bodart on 19/08/2016.
 */
public class MainTest {

    protected final static Logger LOGGER = LogManager.getLogger(MainTest.class);

    private final static ArchipelagoBuilderType TEST_TYPE = ArchipelagoBuilderType.ORIENT_DB;
    private final static String TEST_SCHOOL_PATH = "C:\\Sand\\IdeaProjects\\Archipelago\\src\\main\\java\\org\\archipelago\\test\\domain\\school";
    private final static String TEST_LIBRARY_PATH = "C:\\Sand\\IdeaProjects\\Archipelago\\src\\main\\java\\org\\archipelago\\test\\domain\\library";

    private final static String TEST_CASE = "School";

    public static void main(String[] args) throws ClassNotFoundException, IOException {

        testBuilder(TEST_CASE);
        testFeeder(TEST_CASE);
    }

    private static void testFeeder(String testCase) throws ClassNotFoundException, IOException {
        switch (testCase.toUpperCase()){
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
        Person director = new Teacher();
        director.setFirstName("Hans");
        director.setLastName("Gys");
        director.setSexe("M");
        Person stu1 = new Student();
        stu1.setFirstName("Gilles");
        stu1.setLastName("Bodart");
        stu1.setSexe("M");
        Person stu2 = new Student();
        stu2.setFirstName("Thomas");
        stu2.setLastName("Blondiau");
        stu2.setSexe("M");
        Person stu3 = new Student();
        stu3.setFirstName("Thomas");
        stu3.setLastName("Reynders");
        stu3.setSexe("M");
        Person stu4 = new Student();
        stu4.setFirstName("Charles-Antoine");
        stu4.setLastName("Van Beers");
        stu4.setSexe("M");
        Person stu5 = new Student();
        stu5.setFirstName("Antoine");
        stu5.setLastName("Dumont");
        stu5.setSexe("M");
        Person stu6 = new Student();
        stu6.setFirstName("Martin");
        stu6.setLastName("Périlleux");
        stu6.setSexe("M");
        Person stu7 = new Student();
        director.setFirstName("Benjamin");
        director.setLastName("Leroy");
        director.setSexe("M");
        Person teach1 = new Teacher();
        teach1.setFirstName("Martin");
        teach1.setLastName("Périlleux");
        teach1.setSexe("M");

        School school = new School();
        school.setName("Saint Louis Namur");
        school.setDirector(director);

        
        List<Object> objects = Lists.newArrayList(director, school, stu1, stu2, stu3, stu4, stu5, stu6, stu7);
        List<GeneratedScript> scripts = ArchipelagoFeederFactory.generate(Paths.get(TEST_SCHOOL_PATH).resolve("archipelago"), objects,
                ArchipelagoFeederType.ORIENT_DB);
        for (GeneratedScript script : scripts) {
            LOGGER.info(script);
        }
    }
}
