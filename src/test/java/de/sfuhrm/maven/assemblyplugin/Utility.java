package de.sfuhrm.maven.assemblyplugin;

import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.ResourceIterator;

import java.util.Iterator;
import java.util.Objects;

public class Utility {

    private Utility() {
        // no instance allowed
    }

    static ResourceIterator iterator(Iterator<ArchiveEntry> entryIterator) {
        Objects.requireNonNull(entryIterator);
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
}
