package de.sfuhrm.maven.assemblyplugin;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.Archiver;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SHA256Test {

    @Test
    public void finalizeArchiveCreation(@Mocked Archiver archiver, @Mocked ArchiveEntry archiveEntryA, @Mocked ArchiveEntry archiveEntryB) throws IOException {

        List<ArchiveEntry> archiveEntries = Arrays.asList(archiveEntryA, archiveEntryB);

        new Expectations() {{
            archiver.getResources(); result = Utility.iterator(archiveEntries.iterator());
            archiveEntryA.getName(); result = "test";
            archiveEntryA.getType(); result = ArchiveEntry.FILE;
            archiveEntryA.getInputStream(); result = new ByteArrayInputStream(new byte[0]);
            archiveEntryB.getName(); result = "abc";
            archiveEntryB.getType(); result = ArchiveEntry.FILE;
            archiveEntryB.getInputStream(); result = new ByteArrayInputStream("abc".getBytes(StandardCharsets.US_ASCII));
        }};

        MessageDigestContainerDescriptorHandler instance = new SHA256();
        instance.finalizeArchiveCreation(archiver);

        new Verifications() {{
            File file;
            archiver.addFile(file = withCapture(), "SHA256SUMS");
            assertEquals(Arrays.asList(
                    "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad *abc",
                    "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855 *test"),
                    Files.readAllLines(file.toPath()));
        }};
    }
}
