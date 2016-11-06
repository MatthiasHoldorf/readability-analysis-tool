# Basic SEU setup for RAT

## Creating a fresh image from scratch

* Create a new volume with drive letter M:

* Checkout the project to the folder M:\workspace

* Set your credentials (after each command, you are prompted for the actual username/password)

  * gradlew setCredentials --key username
  * gradlew setCredentials --key password
  * gradlew setCredentials --key qawareUsername
  * gradlew setCredentials --key qawarePassword

* Create the basic image and checkout repositories

  * gradlew bootstrapSeu
  * gradlew createAsciiBanner

* Delete the `codebase` and `docbase` directories

* Cleanup the Software directory
	  
## Updating an image incrementally

* Edit `build.gradle`
* Configure credentials as above
* Start the update process:

        gradlew updateSeu

## Rolling out (clean up)

* Delete intelliJ license file (`M:\home\.IntelliJIdea\config\eval\idea15.evaluation.key` or `L:\home\.IntelliJIdea\config\idea15.key`)
* Delete Gradle credentials (M:\workspace\02_seu\gradle)

## Configuration

### Eclipse
* Set the workspace to `M:\home\workspace`
* Install UIMA Eclipse Plugin
    * Add a new site titled "UIMA Plugin" with the address `http://www.apache.org/dist/uima/eclipse-update-site/`
    * Install Apache UIMA Eclipse tooling and runtime support, Apache UIMA Ruta and Apache UIMA-AS
    * Restart Eclipse
