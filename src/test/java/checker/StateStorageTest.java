/**
 * Този клас тества функционалността на StateStorage.
 * Проверява запазването и зареждането на състоянието на сканирането.
 */
package checker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import checker.storage.ScanMemento;
import checker.storage.StateStorage;

class StateStorageTest {

    @Test
    void shouldSaveAndLoadScanMemento() throws IOException {
        Path tempDir = Files.createTempDirectory("state-storage-test");
        Path stateFile = tempDir.resolve("nested").resolve("scan.state");

        Map<String, String> completedChecksums = new LinkedHashMap<>();
        completedChecksums.put("file1.txt", "hash1");
        completedChecksums.put("sub/file2.txt", "hash2");

        ScanMemento original = new ScanMemento(
                tempDir.toString(),
                "md5",
                true,
                2,
                1,
                100L,
                50L,
                "file1.txt",
                25L,
                completedChecksums);

        StateStorage.save(original, stateFile);

        assertTrue(Files.exists(stateFile), "State file should be created after saving");

        ScanMemento loaded = StateStorage.load(stateFile);

        assertEquals(original.getRootPath(), loaded.getRootPath());
        assertEquals(original.getAlgorithm(), loaded.getAlgorithm());
        assertEquals(original.isFollowLinks(), loaded.isFollowLinks());
        assertEquals(original.getTotalFiles(), loaded.getTotalFiles());
        assertEquals(original.getProcessedFiles(), loaded.getProcessedFiles());
        assertEquals(original.getTotalBytes(), loaded.getTotalBytes());
        assertEquals(original.getTotalBytesProcessed(), loaded.getTotalBytesProcessed());
        assertEquals(original.getCurrentFile(), loaded.getCurrentFile());
        assertEquals(original.getCurrentFileBytes(), loaded.getCurrentFileBytes());
        assertEquals(original.getCompletedChecksums(), loaded.getCompletedChecksums());
    }
}
