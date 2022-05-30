[![license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

# CUBA Azure File Storage Add-on

This add-on enables using Azure Storage Blob as File Storage.

## Installation
This add-on's repository is officially linked to the main CUBA repository, so you can install it from the Marketplace
or by adding the repository manually to your project.

The following table shows which version of the add-on is compatible with which version of the platform:

| Platform Version | Add-on Version | Coordinates
| ---------------- | -------------- | ------------
| 7.2.*            | 1.0.0          | gr.netmechanics.cuba.afs:cubaafs-global:1.0.0
| 7.2.*            | 1.0.1          | gr.netmechanics.cuba.afs:cubaafs-global:1.0.1
| 7.2.*            | 1.1.0          | gr.netmechanics.cuba.afs:cubaafs-global:1.1.0

The latest stable version is: `1.1.0`

### From Marketplace
You can use **CUBA Studio** / **IntelliJ IDEA** to add it to your project: choose the `CUBA -> Marketplace...` menu item,
find the *Azure File Storage* add-on, then click on the `Install` button.

### Adding the repository manually
Modify the `build.gradle` of your CUBA application. First add the repository `https://dl.bintray.com/netmechanics/cuba-components` 
to the buildscript.
```gradle
buildscript {
    ...
    repositories {
        ...
        maven {
            url 'https://dl.bintray.com/netmechanics/cuba-components'
        }
        ...
    }
    ...
}
...
```
Then select the version of the add-on which is compatible with the platform version of your project 
and add custom application component to your project
```gradle
...
dependencies {
    ...
    appComponent('gr.netmechanics.cuba.afs:cubaafs-global:1.1.0')
    ...
}
...
```

# Configuration
1. To add Azure File Storage support, you need to register `AzureFileStorage` class in the `spring.xml` file in the `core` module:

 ```xml
     <bean name="cuba_FileStorage" class="gr.netmechanics.cuba.afs.blob.AzureFileStorage"/>
 ```

2. Next, you should define your Azure Storage settings in the `app.properties` file in the `core` module:

 * `cuba.azure.storage.connectionString` - Azure Storage Connection string.

 * `cuba.azure.storage.containerName` - Azure Storage Blob Container name.

 **Example:**
 ```properties
 cuba.azure.storage.connectionString = DefaultEndpointsProtocol=https;AccountName=myAccount;AccountKey=1WE6oxxWosQ745ClyQP/tfRT1H6zGoDKo8FOOtnVFZ3rkPZy+8J71f9vGcGgcQKXWCsA2iER5Pmnop0wBuU3Gg==;EndpointSuffix=core.windows.net
 cuba.azure.storage.containerName = myfiles
 ```