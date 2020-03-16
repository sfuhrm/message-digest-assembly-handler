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

public class SHA512Test {

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

        MessageDigestContainerDescriptorHandler instance = new SHA512();
        instance.finalizeArchiveCreation(archiver);

        new Verifications() {{
            File file;
            archiver.addFile(file = withCapture(), "SHA512SUMS");
            assertEquals(Arrays.asList(
                    "ddaf35a193617abacc417349ae20413112e6fa4e89a97ea20a9eeee64b55d39a2192992a274fc1a836ba3c23a3feebbd454d4423643ce80e2a9ac94fa54ca49f *abc",
                    "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e *test"),
                    Files.readAllLines(file.toPath()));
        }};
    }
}
