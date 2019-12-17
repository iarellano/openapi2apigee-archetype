@Grapes([
        @Grab(group='org.openapitools', module='openapi-generator', version='4.2.1'),
        @Grab(group='org.apache.velocity', module='velocity', version='1.7'),
        @Grab(group='org.apache.commons', module='commons-lang3', version='3.4')
])

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.sun.org.apache.xml.internal.serialize.OutputFormat
import com.sun.org.apache.xml.internal.serialize.XMLSerializer
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.parser.ObjectMapperFactory
import io.swagger.v3.parser.OpenAPIV3Parser
import io.swagger.v3.parser.core.models.AuthorizationValue
import io.swagger.v3.parser.core.models.SwaggerParseResult
import io.swagger.v3.parser.util.ClasspathHelper
import io.swagger.v3.parser.util.RemoteUrl
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.apache.velocity.Template
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.w3c.dom.Document

import javax.net.ssl.SSLHandshakeException
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.Paths


class OpenAPIV3ParserData extends OpenAPIV3Parser {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAPIV3Parser.class);
    private static ObjectMapper JSON_MAPPER = ObjectMapperFactory.createJson();
    private static ObjectMapper YAML_MAPPER = ObjectMapperFactory.createYaml();
    private static String encoding = "UTF-8";
    private String data;

    private ObjectMapper getRightMapper(String data) {
        ObjectMapper mapper;
        if (data.trim().startsWith("{")) {
            mapper = JSON_MAPPER;
        } else {
            mapper = YAML_MAPPER;
        }

        return mapper;
    }

    SwaggerParseResult readWithInfo(String location, List<AuthorizationValue> auths) {
        SwaggerParseResult output;
        try {
            location = location.replaceAll("\\\\", "/");
            if (location.toLowerCase().startsWith("http")) {
                data = RemoteUrl.urlToString(location, auths);
            } else {
                String fileScheme = "file:";
                Path path;
                if (location.toLowerCase().startsWith("file:")) {
                    path = Paths.get(URI.create(location));
                } else {
                    path = Paths.get(location);
                }

                if (Files.exists(path, new LinkOption[0])) {
                    data = FileUtils.readFileToString(path.toFile(), encoding);
                } else {
                    data = ClasspathHelper.loadFileFromClasspath(location);
                }
            }
            LOGGER.debug("Loaded raw data: {}", data);
            ObjectMapper mapper = this.getRightMapper(data);
            JsonNode rootNode = mapper.readTree(data);
            LOGGER.debug("Parsed rootNode: {}", rootNode);
            return this.readWithInfo(location, rootNode);
        } catch (SSLHandshakeException var6) {
            output = new SwaggerParseResult();
            output.setMessages(Arrays.asList("unable to read location `" + location + "` due to a SSL configuration error.  It is possible that the server SSL certificate is invalid, self-signed, or has an untrusted Certificate Authority."));
            return output;
        } catch (Exception var7) {
            LOGGER.warn("Exception while reading:", var7);
            output = new SwaggerParseResult();
            output.setMessages(Arrays.asList("unable to read location `" + location + "`"));
            return output;
        }
    }
    String getSpecData() {
        return data;
    }
}

class APIProxyFlow {

    String name;

    String desc;

    String path;

    String verb;
}

interface FileExecutor {
    void execute(File file)
}

class FileWalker {

    private final FileExecutor executor

    FileWalker(FileExecutor executor) {
        this.executor = executor
    }

    void walk(File folder) {
        for (File file: folder.listFiles()) {
            System.out.println(file.path);
            if (file.isDirectory()) {
                walk(file)
            } else {
                executor.execute(file)
            }
        }
    }
}

class TemplateExecutor implements FileExecutor {

    private final VelocityEngine velocityEngine

    private final VelocityContext context

    private final String basepath

    TemplateExecutor(File rootTemplateFolder, Properties templateVariables) {
        velocityEngine = new VelocityEngine()
        context = new VelocityContext()
        basepath = rootTemplateFolder.path
        context.put("file.resource.loader.path", basepath)
        for (String key: templateVariables.keySet()) {
            context.put(key, templateVariables.get(key))
        }
    }

    @Override
    void execute(File file) {
        if (file.name.endsWith(".vm")) {
            Template template = velocityEngine
                    .getTemplate(file.path.substring(basepath.length() + 1))
            FileWriter fileWriter = new FileWriter(file)
            template.merge(context, fileWriter)
            fileWriter.close()
        }
    }
}

class FileRenamer implements FileExecutor {
    @Override
    void execute(File file) {
        if (file.name.endsWith(".vm")) {
            file.renameTo(new File(file.path.substring(0,file.path.length()-3)))
        }
    }
}

class RootFilesMover implements FileExecutor {
    @Override
    void execute(File file) {
        System.out.println("Moving file " + file.path)
        FileUtils.moveFile(file, new File(file.parentFile.parentFile.path + "/" + file.name))
    }
}


class XmlFormater implements FileExecutor {

    final DocumentBuilderFactory dbFactory;

    final DocumentBuilder dBuilder;

    XmlFormater() {
        dbFactory = DocumentBuilderFactory.newInstance()
        dBuilder = dbFactory.newDocumentBuilder()
    }

    @Override
    void execute(File file) {
        if (file.name.endsWith(".xml")) {
            Document doc = dBuilder.parse(file)
            OutputFormat format = new OutputFormat(doc)
            format.setIndenting(true)
            format.setLineWidth(255)
            format.setIndent(4)
            Writer out = new StringWriter()
            XMLSerializer serializer = new XMLSerializer(out, format)
            serializer.serialize(doc)
            FileWriter fileWriter = new FileWriter(file)
            fileWriter.write(out.toString().replaceAll("&quot;", "\""))
            fileWriter.close()
        }
    }
}

String specLocation = request.getProperties().get("spec")
String specAuthName = request.getProperties().get("spec.auth.name")
String specAuthValue = request.getProperties().get("spec.auth.value")
String specAuthType = request.getProperties().get("spec.auth.type")
String[] envs = request.getProperties().get("envs").split(",")

final OpenAPI openAPI;
final String data;

if (specLocation.matches("^https?://.*") && StringUtils.isNotBlank(specAuthType)) {
    AuthorizationValue authorizationValue = new AuthorizationValue(specAuthName, specAuthValue, specAuthType);
    OpenAPIV3Parser parser = new OpenAPIV3ParserData();
    openAPI = parser.readWithInfo(spec, Arrays.asList(authorizationValue)).getOpenAPI();
    data = ( (OpenAPIV3ParserData) parser ).getSpecData();
} else {
    OpenAPIV3Parser parser = new OpenAPIV3ParserData();
    openAPI = parser.readWithInfo(spec, (List)null).getOpenAPI();
    data = ( (OpenAPIV3ParserData) parser ).getSpecData();
}

Properties properties = new Properties()
properties.putAll(request.getProperties())
properties.put("envs", envs)


List<APIProxyFlow> flows = new ArrayList<>()
for (String path: openAPI.getPaths().keySet()) {
    for (PathItem.HttpMethod verb: openAPI.getPaths().get(path).readOperationsMap().keySet()) {
        Operation operation = openAPI.getPaths().get(path).readOperationsMap().get(verb)
        APIProxyFlow flow = new APIProxyFlow()
        flow.setName(operation.getOperationId())
        flow.setDesc(operation.getSummary())
        flow.setPath(path)
        flow.setVerb(verb.toString())
        flows.add(flow)
    }
}

properties.put("flows", flows)


File outputDirectory = new File(request.outputDirectory)

System.out.println("Spec: " + specLocation)
System.out.println("Output dir: " + outputDirectory.path)

System.out.println("Files moved 1")
new FileWalker(new RootFilesMover()).walk(new File(outputDirectory, request.artifactId + "/root"))
new FileWalker(new TemplateExecutor(outputDirectory, properties)).walk(outputDirectory)
System.out.println("Files moved 2")
new FileWalker(new FileRenamer()).walk(outputDirectory)
new FileWalker(new XmlFormater()).walk(outputDirectory)

new File(outputDirectory, request.artifactId + "/root").delete()




