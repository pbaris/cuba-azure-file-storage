package gr.netmechanics.cuba.afs.blob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import gr.netmechanics.cuba.afs.AzureFileStorageTestContainer;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * @author Panos Bariamis (pbaris)
 */
public class AzureFileStorageTest {
    private static final String FILE_CONTENT = "This text is for Azure Storage Blob.";

    @RegisterExtension
    static AzureFileStorageTestContainer cont = AzureFileStorageTestContainer.Common.INSTANCE;

    private static AzureFileStorage fileStorageAPI;
    private static FileDescriptor fileDescr;
    private static FileDescriptor fileDescr2;

    @BeforeAll
    static void setUp() {
        fileDescr = cont.metadata().create(FileDescriptor.class);
        fileDescr.setCreateDate(new Date());
        fileDescr.setSize((long) FILE_CONTENT.length());
        fileDescr.setName("AzureFileStorageTest");
        fileDescr.setExtension("txt");

        fileDescr2 = cont.metadata().create(FileDescriptor.class);
        fileDescr2.setCreateDate(new Date());
        fileDescr2.setSize((long) FILE_CONTENT.length());
        fileDescr2.setName("AzureFileStorageTest");

        fileStorageAPI = new AzureFileStorage();
        fileStorageAPI.config = AppBeans.get(Configuration.class).getConfig(AzureFileStorageConfig.class);
        fileStorageAPI.refreshBlobContainerClient();
    }

    @Test
    void testWithExtension() throws Exception {
        fileStorageAPI.saveFile(fileDescr, FILE_CONTENT.getBytes());

        InputStream inputStream = fileStorageAPI.openStream(fileDescr);
        assertEquals(FILE_CONTENT, IOUtils.toString(inputStream, StandardCharsets.UTF_8));

        boolean fileExists = fileStorageAPI.fileExists(fileDescr);
        assertTrue(fileExists);

        fileStorageAPI.removeFile(fileDescr);
    }

    @Test
    void testWithoutExtension() throws Exception {
        fileStorageAPI.saveFile(fileDescr2, FILE_CONTENT.getBytes());

        InputStream inputStream = fileStorageAPI.openStream(fileDescr2);
        assertEquals(FILE_CONTENT, IOUtils.toString(inputStream, StandardCharsets.UTF_8));

        boolean fileExists = fileStorageAPI.fileExists(fileDescr2);
        assertTrue(fileExists);

        fileStorageAPI.removeFile(fileDescr2);
    }
}
