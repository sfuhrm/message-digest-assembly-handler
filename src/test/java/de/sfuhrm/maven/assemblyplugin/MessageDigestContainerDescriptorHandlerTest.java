package de.sfuhrm.maven.assemblyplugin;

import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ResourceIterator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import mockit.*;

public class MessageDigestContainerDescriptorHandlerTest {

    @Test
    public void getVirtualFiles() {
        MessageDigestContainerDescriptorHandler instance = new MessageDigestContainerDescriptorHandler();
        List<String> list = instance.getVirtualFiles();
        assertEquals(Collections.singletonList("MD5SUM"), list);
    }

    @Test
    public void getVirtualFilesWithDifferentChecksum() {
        MessageDigestContainerDescriptorHandler instance = new MessageDigestContainerDescriptorHandler();
        instance.setChecksumFile("FOO");
        List<String> list = instance.getVirtualFiles();
        assertEquals(Collections.singletonList("FOO"), list);
    }

    private ResourceIterator iterator(Iterator<ArchiveEntry> entryIterator) {
        return new ResourceIterator() {
            @Override
            public boolean hasNext() {
                return entryIterator.hasNext();
            }

            @Override
            public ArchiveEntry next() {
                return entryIterator.next();
            }
        };
    }

    @Test
    public void finalizeArchiveCreation(@Mocked Archiver archiver, @Mocked ArchiveEntry archiveEntryA, @Mocked ArchiveEntry archiveEntryB) throws IOException {

        List<ArchiveEntry> archiveEntries = Arrays.asList(archiveEntryA, archiveEntryB);

        new Expectations() {{
            archiver.getResources(); result = iterator(archiveEntries.iterator());
            archiveEntryA.getName(); result = "test";
            archiveEntryA.getType(); result = ArchiveEntry.FILE;
            archiveEntryA.getInputStream(); result = new ByteArrayInputStream(new byte[0]);
            archiveEntryB.getName(); result = "abc";
            archiveEntryB.getType(); result = ArchiveEntry.FILE;
            archiveEntryB.getInputStream(); result = new ByteArrayInputStream("abc".getBytes(StandardCharsets.US_ASCII));
        }};

        MessageDigestContainerDescriptorHandler instance = new MessageDigestContainerDescriptorHandler();
        instance.finalizeArchiveCreation(archiver);

        new Verifications() {{
            File file;
            archiver.addFile(file = withCapture(), "MD5SUM");
            // see https://tools.ietf.org/html/rfc1321#appendix-A.5
            assertEquals(Arrays.asList(
                    "900150983cd24fb0d6963f7d28e17f72 *abc",
                    "d41d8cd98f00b204e9800998ecf8427e *test"),
                    Files.readAllLines(file.toPath()));
        }};
    }

    @Test
    public void finalizeArchiveCreationWithSha256(@Mocked Archiver archiver, @Mocked ArchiveEntry archiveEntryA, @Mocked ArchiveEntry archiveEntryB) throws IOException {

        List<ArchiveEntry> archiveEntries = Arrays.asList(archiveEntryA, archiveEntryB);

        new Expectations() {{
            archiver.getResources(); result = iterator(archiveEntries.iterator());
            archiveEntryA.getName(); result = "test";
            archiveEntryA.getType(); result = ArchiveEntry.FILE;
            archiveEntryA.getInputStream(); result = new ByteArrayInputStream(new byte[0]);
            archiveEntryB.getName(); result = "abc";
            archiveEntryB.getType(); result = ArchiveEntry.FILE;
            archiveEntryB.getInputStream(); result = new ByteArrayInputStream("abc".getBytes(StandardCharsets.US_ASCII));
        }};

        MessageDigestContainerDescriptorHandler instance = new MessageDigestContainerDescriptorHandler();
        instance.setChecksumFile("SHA256SUM");
        instance.setMessageDigest("SHA-256");
        instance.finalizeArchiveCreation(archiver);

        new Verifications() {{
            File file;
            archiver.addFile(file = withCapture(), "SHA256SUM");
            // see https://tools.ietf.org/html/rfc1321#appendix-A.5
            assertEquals(Arrays.asList(
                    "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad *abc",
                    "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855 *test"),
                    Files.readAllLines(file.toPath()));
        }};
    }

    @Test
    public void finalizeArchiveCreationWithExclusion(@Mocked Archiver archiver, @Mocked ArchiveEntry archiveEntry) {

        List<ArchiveEntry> archiveEntries = Collections.singletonList(archiveEntry);

        new Expectations() {{
            archiver.getResources(); result = iterator(archiveEntries.iterator());
            archiver.getResources(); result = iterator(archiveEntries.iterator());
            archiveEntry.getName(); result = "test";
            archiveEntry.getType(); result = ArchiveEntry.FILE;
        }};

        MessageDigestContainerDescriptorHandler instance = new MessageDigestContainerDescriptorHandler();
        instance.setExclusionRegex("test");
        instance.finalizeArchiveCreation(archiver);

        new Verifications() {{
            File file;
            archiver.addFile(file = withCapture(), "MD5SUM");
            assertEquals(0, file.length()); // nothing in file, no matches
        }};
    }
}
