package gr.netmechanics.cuba.afs.blob;

import static com.haulmont.bali.util.Preconditions.checkNotNullArgument;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import javax.inject.Inject;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobErrorCode;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.BlobStorageException;
import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.core.sys.events.AppContextStartedEvent;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;

/**
 * @author Panos Bariamis (pbaris)
 */
public class AzureFileStorage implements FileStorageAPI {
    private static Logger log = LoggerFactory.getLogger(AzureFileStorage.class);

    @Inject
    protected AzureFileStorageConfig config;

    private AtomicReference<BlobContainerClient> clientReference = new AtomicReference<>();
    private Tika tika = new Tika();

    @EventListener
    protected void initClient(AppContextStartedEvent event) {
        refreshBlobContainerClient();
    }

    void refreshBlobContainerClient() {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
            .connectionString(config.getConnectionString())
            .buildClient();

        try {
            clientReference.set(blobServiceClient.createBlobContainer(config.getContainerName()));

        } catch (BlobStorageException e) {
            // The container may already exist, so don't throw an error
            if (!e.getErrorCode().equals(BlobErrorCode.CONTAINER_ALREADY_EXISTS)) {
                log.warn(e.getErrorCode().toString());

            } else {
                clientReference.set(blobServiceClient.getBlobContainerClient(config.getContainerName()));
            }
        }
    }

    @Override
    public long saveStream(FileDescriptor fileDescr, InputStream inputStream) throws FileStorageException {
        Preconditions.checkNotNullArgument(fileDescr.getSize());
        try {
            saveFile(fileDescr, IOUtils.toByteArray(inputStream));

        } catch (IOException e) {
            String message = String.format("Could not save file %s.", getFileName(fileDescr));
            throw new FileStorageException(FileStorageException.Type.IO_EXCEPTION, message);
        }

        return fileDescr.getSize();
    }

    @Override
    public void saveFile(FileDescriptor fileDescr, byte[] data) throws FileStorageException {
        checkNotNullArgument(data, "File content is null");

        try {
            BlobClient blobClient = clientReference.get().getBlobClient(resolveFileName(fileDescr));
            blobClient.upload(new ByteArrayInputStream(data), fileDescr.getSize(), true);

            Optional.ofNullable(getMimeType(fileDescr)).ifPresent(mimeType ->
                blobClient.setHttpHeaders(new BlobHttpHeaders().setContentType(mimeType)));

        } catch (NullPointerException e) {
            String message = String.format("Could not save file %s.", getFileName(fileDescr));
            throw new FileStorageException(FileStorageException.Type.IO_EXCEPTION, message);
        }
    }

    @Override
    public void removeFile(FileDescriptor fileDescr) throws FileStorageException {
        try {
            clientReference.get()
                .getBlobClient(resolveFileName(fileDescr))
                .delete();

        } catch (NullPointerException e) {
            String message = String.format("Could not delete file %s.", getFileName(fileDescr));
            throw new FileStorageException(FileStorageException.Type.IO_EXCEPTION, message);
        }
    }

    @Override
    public InputStream openStream(FileDescriptor fileDescr) throws FileStorageException {
        return new ByteArrayInputStream(loadFile(fileDescr));
    }

    @Override
    public byte[] loadFile(FileDescriptor fileDescr) throws FileStorageException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            clientReference.get()
                .getBlobClient(resolveFileName(fileDescr))
                .download(out);
        } catch (NullPointerException e) {
            String message = String.format("Could not load file %s.", getFileName(fileDescr));
            throw new FileStorageException(FileStorageException.Type.IO_EXCEPTION, message);
        }
        return out.toByteArray();
    }

    @Override
    public boolean fileExists(FileDescriptor fileDescr) throws FileStorageException {
        BlobContainerClient client = clientReference.get();
        return client != null && client.getBlobClient(resolveFileName(fileDescr)).exists();
    }

    private String getFileName(FileDescriptor fileDescriptor) {
        if (StringUtils.isNotBlank(fileDescriptor.getExtension())) {
            return fileDescriptor.getId().toString() + "." + fileDescriptor.getExtension();
        }

        return fileDescriptor.getId().toString();
    }

    private String resolveFileName(FileDescriptor fileDescr) {
        return getStorageDir(fileDescr.getCreateDate()) + "/" + getFileName(fileDescr);
    }

    private String getStorageDir(Date createDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return String.format("%d/%s/%s", year,
            StringUtils.leftPad(String.valueOf(month), 2, '0'),
            StringUtils.leftPad(String.valueOf(day), 2, '0'));
    }

    private String getMimeType(FileDescriptor fileDescr) {
        if (StringUtils.isNotBlank(fileDescr.getExtension())) {
            return tika.detect("." + fileDescr.getExtension());
        }

        return null;
    }
}
