## Sample generation using a setup file

Create a project by executing the archetype as follows:
```bash
mvn archetype:generate \
  -DarchetypeGroupId=com.github.iarellano \
  -DarchetypeArtifactId=openapi2apigee-archetype \
  -DinteractiveMode=false \
  -DgroupId=com.example \
  -DartifactId=petstore-v1 \
  -Dversion=1.0 \
  -Dspec=https://raw.githubusercontent.com/OAI/OpenAPI-Specification/master/examples/v3.0/petstore.yaml \
  -Dbasepath=/petstore/v1 \
  -DedgeSetupFile=./setup.yaml \
  -templateDir=./templates
```