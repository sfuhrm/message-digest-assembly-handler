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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractMessageDigestTest {

    protected abstract String getAbcSum();
    protected abstract String getTestSum();
    protected abstract String getChecksumFile();
    protected abstract MessageDigestContainerDescriptorHandler newInstance();

    @Test
    public void getVirtualFiles() {
        MessageDigestContainerDescriptorHandler instance = newInstance();
        List<String> list = instance.getVirtualFiles();
        assertEquals(Collections.singletonList(getChecksumFile()), list);
    }

    @Test
    public void getVirtualFilesWithDifferentChecksum() {
        MessageDigestContainerDescriptorHandler instance = newInstance();
        instance.setChecksumFile("FOO");
        List<String> list = instance.getVirtualFiles();
        assertEquals(Collections.singletonList("FOO"), list);
    }

    @Test
    public void finalizeArchiveCreationWithNoFiles(@Mocked Archiver archiver) throws IOException {
        List<ArchiveEntry> archiveEntries = Collections.emptyList();

        new Expectations() {{
            archiver.getResources(); result = Utility.iterator(archiveEntries.iterator());
        }};

        MessageDigestContainerDescriptorHandler instance = newInstance();
        instance.finalizeArchiveCreation(archiver);

        new Verifications() {{
            File file;
            archiver.addFile(file = withCapture(), getChecksumFile());
            assertEquals(Collections.emptyList(),
                    Files.readAllLines(file.toPath()));
        }};
    }

    @Test
    public void finalizeArchiveCreationWithAlphabeticInput(@Mocked Archiver archiver, @Mocked ArchiveEntry archiveEntryA, @Mocked ArchiveEntry archiveEntryB) throws IOException {
        List<ArchiveEntry> archiveEntries = Arrays.asList(archiveEntryB, archiveEntryA);

        new Expectations() {{
            archiver.getResources(); result = Utility.iterator(archiveEntries.iterator());
            archiveEntryB.getName(); result = "abc";
            archiveEntryB.getType(); result = ArchiveEntry.FILE;
            archiveEntryB.getInputStream(); result = new ByteArrayInputStream("abc".getBytes(StandardCharsets.US_ASCII));
            archiveEntryA.getName(); result = "test";
            archiveEntryA.getType(); result = ArchiveEntry.FILE;
            archiveEntryA.getInputStream(); result = new ByteArrayInputStream(new byte[0]);
        }};

        MessageDigestContainerDescriptorHandler instance = newInstance();
        instance.finalizeArchiveCreation(archiver);

        new Verifications() {{
            File file;
            archiver.addFile(file = withCapture(), getChecksumFile());
            assertEquals(Arrays.asList(
                    getAbcSum()+" *abc",
                    getTestSum()+" *test"),
                    Files.readAllLines(file.toPath()));
        }};
    }

    @Test
    public void finalizeArchiveCreationWithInverseAlphabeticInput(@Mocked Archiver archiver, @Mocked ArchiveEntry archiveEntryA, @Mocked ArchiveEntry archiveEntryB) throws IOException {
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

        MessageDigestContainerDescriptorHandler instance = newInstance();
        instance.finalizeArchiveCreation(archiver);

        new Verifications() {{
            File file;
            archiver.addFile(file = withCapture(), getChecksumFile());
            assertEquals(Arrays.asList(
                    getAbcSum()+" *abc",
                    getTestSum()+" *test"),
                    Files.readAllLines(file.toPath()));
        }};
    }

    @Test
    public void finalizeArchiveCreationWithExclusion(@Mocked Archiver archiver, @Mocked ArchiveEntry archiveEntry) {
        List<ArchiveEntry> archiveEntries = Collections.singletonList(archiveEntry);

        new Expectations() {{
            archiver.getResources(); result = Utility.iterator(archiveEntries.iterator());
            archiveEntry.getName(); result = "test";
            archiveEntry.getType(); result = ArchiveEntry.FILE;
        }};

        MessageDigestContainerDescriptorHandler instance = newInstance();
        instance.setExclusionRegex("test");
        instance.finalizeArchiveCreation(archiver);

        new Verifications() {{
            File file;
            archiver.addFile(file = withCapture(), getChecksumFile());
            assertEquals(0, file.length()); // nothing in file, no matches
        }};
    }
}
