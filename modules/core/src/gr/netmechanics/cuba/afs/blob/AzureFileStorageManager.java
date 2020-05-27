package gr.netmechanics.cuba.afs.blob;

import javax.inject.Inject;

import com.haulmont.cuba.core.app.FileStorageAPI;
import org.springframework.stereotype.Component;

/**
 * @author Panos Bariamis (pbaris)
 */
@Component("cuba_AzureFileStorageManager")
public class AzureFileStorageManager implements AzureFileStorageManagerMBean {

    @Inject
    private FileStorageAPI fileStorageAPI;

    @Override
    public String refreshBlobContainerClient() {
        if (fileStorageAPI instanceof AzureFileStorage) {
            ((AzureFileStorage) fileStorageAPI).refreshBlobContainerClient();
            return "Refreshed successfully";
        }

        return "Not an Azure blob storage - refresh attempt ignored";
    }
}
