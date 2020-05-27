package gr.netmechanics.cuba.afs.blob;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.testsupport.TestContainer;
import gr.netmechanics.cuba.afs.AzureFileStorageTestContainer;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Panos Bariamis (pbaris)
 */
//@Ignore
public class AzureFileStorageTest {
    private static final String FILE_CONTENT = "This text is for Azure Storage Blobe.";

    @ClassRule
    public static TestContainer cont = AzureFileStorageTestContainer.Common.INSTANCE;

    private AzureFileStorage fileStorageAPI;

    private FileDescriptor fileDescr;
    private FileDescriptor fileDescr2;

    @Before
    public void setUp() throws Exception {
        fileDescr = new FileDescriptor();
        fileDescr.setCreateDate(new Date());
        fileDescr.setSize((long) FILE_CONTENT.length());
        fileDescr.setName("AmazonFileStorageTest");
        fileDescr.setExtension("txt");

        fileDescr2 = new FileDescriptor();
        fileDescr2.setCreateDate(new Date());
        fileDescr2.setSize((long) FILE_CONTENT.length());
        fileDescr2.setName("AmazonFileStorageTest");

        fileStorageAPI = new AzureFileStorage();
        fileStorageAPI.config = AppBeans.get(Configuration.class).getConfig(AzureFileStorageConfig.class);
    }

    @Test
    public void testWithExtension() throws Exception {
        fileStorageAPI.saveFile(fileDescr, FILE_CONTENT.getBytes());

        InputStream inputStream = fileStorageAPI.openStream(fileDescr);
        Assert.assertEquals(FILE_CONTENT, IOUtils.toString(inputStream, StandardCharsets.UTF_8));

        boolean fileExists = fileStorageAPI.fileExists(fileDescr);
        Assert.assertTrue(fileExists);

//        fileStorageAPI.removeFile(fileDescr);
    }

    @Test
    public void testWithoutExtension() throws Exception {
        fileStorageAPI.saveFile(fileDescr2, FILE_CONTENT.getBytes());

        InputStream inputStream = fileStorageAPI.openStream(fileDescr2);
        Assert.assertEquals(FILE_CONTENT, IOUtils.toString(inputStream, StandardCharsets.UTF_8));

        boolean fileExists = fileStorageAPI.fileExists(fileDescr2);
        Assert.assertTrue(fileExists);

//        fileStorageAPI.removeFile(fileDescr2);
    }
}
