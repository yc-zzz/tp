package seedu.duke.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import seedu.duke.MoneyBagProMaxException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextFileExporterTest {

    private static final String DATA_FILE = "data/transactions.txt";
    private static final String OUTPUT_FILE = "data/test_exported.txt";

    private TextFileExporter exporter;

    @BeforeEach
    void setUp() {
        exporter = new TextFileExporter();
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(DATA_FILE));
        Files.deleteIfExists(Paths.get(OUTPUT_FILE));
    }

    @Test
    void export_noSourceFile_throwsMoneyBagProMaxException() throws IOException {
        Files.deleteIfExists(Paths.get(DATA_FILE));
        assertThrows(MoneyBagProMaxException.class,
                     () -> exporter.export(OUTPUT_FILE));
    }

    @Test
    void export_validSourceFile_createsOutputFile() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        Files.writeString(Paths.get(DATA_FILE), "some data");

        exporter.export(OUTPUT_FILE);

        assertTrue(Files.exists(Paths.get(OUTPUT_FILE)));
    }

    @Test
    void export_validSourceFile_outputMatchesSource() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        String content = "[TXN] | type=expense | category=food | amount=10.0 | description=lunch | date=2026-03-23";
        Files.writeString(Paths.get(DATA_FILE), content);

        exporter.export(OUTPUT_FILE);

        String exported = Files.readString(Paths.get(OUTPUT_FILE));
        assertEquals(content, exported);
    }

    @Test
    void export_emptySourceFile_producesEmptyOutputFile() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        Files.createFile(Paths.get(DATA_FILE));

        exporter.export(OUTPUT_FILE);

        assertEquals(0, Files.size(Paths.get(OUTPUT_FILE)));
    }

    @Test
    void export_multilineSourceFile_allLinesPreserved() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        String content = "[TXN] | type=expense | category=food | amount=10.0 | description=lunch | date=2026-03-23\n"
                + "[TXN] | type=income | category=salary | amount=3000.0 | description=pay | date=2026-03-01\n";
        Files.writeString(Paths.get(DATA_FILE), content);

        exporter.export(OUTPUT_FILE);

        assertEquals(2, Files.readAllLines(Paths.get(OUTPUT_FILE)).size());
    }

    @Test
    void export_overwritesExistingOutputFile() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        Files.writeString(Paths.get(OUTPUT_FILE), "old content");
        Files.writeString(Paths.get(DATA_FILE), "new content");

        exporter.export(OUTPUT_FILE);

        assertEquals("new content", Files.readString(Paths.get(OUTPUT_FILE)));
    }

    @Test
    void export_invalidOutputPath_throwsMoneyBagProMaxException() throws IOException {
        Files.createDirectories(Paths.get("data"));
        Files.writeString(Paths.get(DATA_FILE), "some data");

        assertThrows(MoneyBagProMaxException.class,
                     () -> exporter.export("/nonexistent_directory/output.txt"));
    }

    @Test
    void export_sourceFileUnchangedAfterExport() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        String original = "original source content";
        Files.writeString(Paths.get(DATA_FILE), original);

        exporter.export(OUTPUT_FILE);

        assertEquals(original, Files.readString(Paths.get(DATA_FILE)));
    }

    @Test
    void export_veryLargeSourceFile_copiedCorrectly() throws MoneyBagProMaxException, IOException {
        Files.createDirectories(Paths.get("data"));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("[TXN] | type=expense | category=food | amount=10.0 | description=lunch | date=2026-03-23\n");
        }
        Files.writeString(Paths.get(DATA_FILE), sb.toString());

        exporter.export(OUTPUT_FILE);

        assertEquals(1000, Files.readAllLines(Paths.get(OUTPUT_FILE)).size());
    }
}
