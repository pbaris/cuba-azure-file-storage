package gr.netmechanics.cuba.afs.blob;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;

/**
 * @author Panos Bariamis (pbaris)
 */
@Source(type = SourceType.DATABASE)
public interface AzureFileStorageConfig extends Config {

    /**
     * @return Azure Storage account name.
     */
    @Property("cuba.azure.storage.connectionString")
    String getConnectionString();

    /**
     * @return Azure Storage container name.
     */
    @Property("cuba.azure.storage.containerName")
    String getContainerName();
}
