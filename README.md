Vaadin in Maven:

`mvn archetype:generate \

    -DarchetypeGroupId=com.vaadin \

    -DarchetypeArtifactId=vaadin-archetype-touchkit \

    -DarchetypeVersion=4.0.0 \

    -DgroupId=edu.uwm -DartifactId=ClinicalTrials\

    -Dversion=0.1.0 \

    -DApplicationName=ClinicalTrials -Dpackaging=war`

Tools:

- Maven

- Vaadin

- TouchKit

compile:
`mvn vaadin:compile`

package:
`mvn package`
