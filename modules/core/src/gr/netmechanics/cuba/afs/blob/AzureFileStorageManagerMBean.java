package gr.netmechanics.cuba.afs.blob;

import com.haulmont.cuba.core.sys.jmx.JmxBean;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * @author Panos Bariamis (pbaris)
 */
@JmxBean(module = "cubaafs", alias = "AzureFileStorageManager")
@ManagedResource(description = "JMX interface for managing Azure storage")
public interface AzureFileStorageManagerMBean {

    @ManagedOperation(description = "Refreshes Azure blob container client")
    String refreshBlobContainerClient();
}
