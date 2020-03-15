package de.sfuhrm.maven.assemblyplugin;

import org.apache.commons.codec.binary.Hex;
import org.apache.maven.plugins.assembly.filter.ContainerDescriptorHandler;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.ResourceIterator;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.components.io.fileselectors.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@Component(role = ContainerDescriptorHandler.class, hint = "message-digest")
public class MessageDigestContainerDescriptorHandler implements ContainerDescriptorHandler {

    private final Logger logger = LoggerFactory.getLogger(MessageDigestContainerDescriptorHandler.class);

    private String messageDigest = "MD5";
    private String checksumFile = "MD5SUM";
    private Pattern exclusionPattern;

    public void setExclusionRegex(String inPattern) {
        this.exclusionPattern = Pattern.compile(inPattern);
        logger.debug("Exclusion regex: {}", exclusionPattern);
    }

    public void setMessageDigest(String inDigest) {
        this.messageDigest = inDigest;
        logger.debug("Message digest: {}", messageDigest);
    }

    public void setChecksumFile(String inChecksumFile) {
        this.checksumFile = Objects.requireNonNull(inChecksumFile);
        logger.debug("Checksum file: {}", checksumFile);
    }

    @Override
    public void finalizeArchiveCreation(Archiver archiver) {
        Map<String, byte[]> messageDigests = new HashMap<>();
        ResourceIterator resourceIterator = archiver.getResources();
        while (resourceIterator.hasNext()) {
            boolean skip;
            ArchiveEntry archiveEntry = resourceIterator.next();

            logger.debug("ArchiveEntry {} is type {}", archiveEntry.getName(), archiveEntry.getType());
            skip = ArchiveEntry.FILE != archiveEntry.getType();
            skip |= exclusionPattern != null && exclusionPattern.matcher(archiveEntry.getName()).matches();

            if (skip) {
                logger.info("Skipping ArchiveEntry {}", archiveEntry.getName());
                continue;
            }
            try (InputStream inputStream = archiveEntry.getInputStream()) {
                messageDigests.put(archiveEntry.getName(), digest(inputStream));
            } catch (IOException | NoSuchAlgorithmException e) { //NOSONAR
                logger.warn("Error processing " + archiveEntry.getName(), e);
                throw new ArchiverException("Error processing " + archiveEntry.getName(), e);
            }
        }

        Path checksumFilePath = createChecksumFile(messageDigests);

        File file = checksumFilePath.toFile();
        file.deleteOnExit();

        archiver.addFile(file, checksumFile);
    }

    private Path createChecksumFile(final Map<String, byte[]> fileCatalog) {
        Path path;
        try {
            path = Files.createTempFile("assembly-" + messageDigest, ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                List<String> keys = new ArrayList<>(fileCatalog.keySet());
                keys.sort(String.CASE_INSENSITIVE_ORDER);

                for (String key : keys) {
                    writer.append(new String(Hex.encodeHex(fileCatalog.get(key))))
                            .append(" *")
                            .append(key)
                            .append("\n");
                }
            }
            return path;
        } catch (IOException e) { //NOSONAR
            logger.warn("Error creating temporary file", e);
            throw new ArchiverException("Cannot create temporary file to finalize archive creation", e);
        }
    }

    @Override
    public void finalizeArchiveExtraction(final UnArchiver unarchiver) {
        // this plugin does not do extraction
    }

    @Override
    public List<String> getVirtualFiles() {
        return Collections.singletonList(checksumFile);
    }

    @Override
    public boolean isSelected(final FileInfo fileInfo) {
        return true;
    }

    private MessageDigest newMessageDigest() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(messageDigest);
    }

    private byte[] digest(InputStream inputStream) throws IOException, NoSuchAlgorithmException {
        MessageDigest messageDigestInstance = newMessageDigest();
        byte[] buffer = new byte[4096];
            int length;

            while (-1 != (length = inputStream.read(buffer))) {
                messageDigestInstance.update(buffer, 0, length);
            }
        return messageDigestInstance.digest();
    }
}
