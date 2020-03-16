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

public class SHA1Test {

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

        MessageDigestContainerDescriptorHandler instance = new SHA1();
        instance.finalizeArchiveCreation(archiver);

        new Verifications() {{
            File file;
            archiver.addFile(file = withCapture(), "SHA1SUMS");
            // see https://tools.ietf.org/html/rfc1321#appendix-A.5
            assertEquals(Arrays.asList(
                    "a9993e364706816aba3e25717850c26c9cd0d89d *abc",
                    "da39a3ee5e6b4b0d3255bfef95601890afd80709 *test"),
                    Files.readAllLines(file.toPath()));
        }};
    }
}
