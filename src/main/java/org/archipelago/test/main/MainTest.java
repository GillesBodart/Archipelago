package org.archipelago.test.main;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.archipelago.core.domain.GeneratedScript;
import org.archipelago.core.domain.types.ArchipelagoBuilderType;
import org.archipelago.core.domain.types.ArchipelagoFeederType;
import org.archipelago.core.runtime.ArchipelagoBuilderFactory;
import org.archipelago.core.runtime.ArchipelagoFeederFactory;
import org.archipelago.test.domain.Author;
import org.archipelago.test.domain.Book;
import org.archipelago.test.domain.Librarian;
import org.archipelago.test.domain.Library;

import com.google.common.collect.Lists;

/**
 * Created by Gilles Bodart on 19/08/2016.
 */
public class MainTest {

    protected final static Logger LOGGER = LogManager.getLogger(MainTest.class);

    private final static ArchipelagoBuilderType TEST_TYPE = ArchipelagoBuilderType.ORIENT_DB;
    private final static String TEST_PATH = "C:\\Sand\\IdeaProjects\\Archipelago\\src\\main\\java\\org\\archipelago\\test\\domain";

    public static void main(String[] args) throws ClassNotFoundException, IOException {

        testBuilder();
        testFeeder();
    }

    private static void testFeeder() throws ClassNotFoundException, IOException {
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
        List<GeneratedScript> scripts = ArchipelagoFeederFactory.generate(Paths.get(TEST_PATH).resolve("archipelago"), Lists.newArrayList(book, author,
                librarian, library),
                ArchipelagoFeederType.ORIENT_DB);
        for (GeneratedScript script : scripts) {
            LOGGER.info(script);
        }
    }

    private static void testBuilder() throws ClassNotFoundException, IOException {
        List<GeneratedScript> scripts = ArchipelagoBuilderFactory.generate(Paths.get(TEST_PATH), TEST_TYPE);
        // Show scripts
        for (GeneratedScript script : scripts) {
            LOGGER.info(script);
        }
    }

}
