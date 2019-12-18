import com.fasterxml.jackson.core.JsonParser
@Grapes([
        @Grab(group='org.openapitools', module='openapi-generator', version='4.2.1'),
        @Grab(group='org.apache.velocity', module='velocity', version='1.7'),
        @Grab(group='org.apache.commons', module='commons-lang3', version='3.4'),
        @Grab(group='com.google.code.gson', module='gson', version='2.8.6'),
        @Grab(group='org.yaml', module='snakeyaml', version='1.25')
])

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.yaml.snakeyaml.Yaml
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
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.apache.velocity.Template
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import org.openapitools.codegen.ClientOptInput
import org.openapitools.codegen.CodegenConstants
import org.openapitools.codegen.DefaultGenerator
import org.openapitools.codegen.config.CodegenConfigurator
import org.openapitools.codegen.config.GlobalSettings
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

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser


class OpenAPIV3ParserData extends OpenAPIV3Parser {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAPIV3Parser.class)
    private static ObjectMapper JSON_MAPPER = ObjectMapperFactory.createJson()
    private static ObjectMapper YAML_MAPPER = ObjectMapperFactory.createYaml()
    private static String encoding = "UTF-8"
    private String data
    private ObjectMapper getRightMapper(String data) {
        ObjectMapper mapper
        if (data.trim().startsWith("{")) {
            mapper = JSON_MAPPER
        } else {
            mapper = YAML_MAPPER
        }

        return mapper
    }

    SwaggerParseResult readWithInfo(String location, List<AuthorizationValue> auths) {
        SwaggerParseResult output
        try {
            location = location.replaceAll("\\\\", "/")
            if (location.toLowerCase().startsWith("http")) {
                data = RemoteUrl.urlToString(location, auths)
            } else {
                String fileScheme = "file:"
                Path path
                if (location.toLowerCase().startsWith("file:")) {
                    path = Paths.get(URI.create(location))
                } else {
                    path = Paths.get(location)
                }

                if (Files.exists(path, new LinkOption[0])) {
                    data = FileUtils.readFileToString(path.toFile(), encoding)
                } else {
                    data = ClasspathHelper.loadFileFromClasspath(location)
                }
            }
            LOGGER.debug("Loaded raw data: {}", data)
            ObjectMapper mapper = this.getRightMapper(data)
            JsonNode rootNode = mapper.readTree(data)
            LOGGER.debug("Parsed rootNode: {}", rootNode)
            return this.readWithInfo(location, rootNode)
        } catch (SSLHandshakeException var6) {
            output = new SwaggerParseResult()
            output.setMessages(Arrays.asList("unable to read location `" + location + "` due to a SSL configuration error.  It is possible that the server SSL certificate is invalid, self-signed, or has an untrusted Certificate Authority."))
            return output
        } catch (Exception var7) {
            LOGGER.warn("Exception while reading:", var7)
            output = new SwaggerParseResult()
            output.setMessages(Arrays.asList("unable to read location `" + location + "`"))
            return output
        }
    }
    String getSpecData() {
        return data
    }
}

class TemplateReplacer {

    private final VelocityEngine velocityEngine

    private final VelocityContext context

    private final String basepath

    TemplateReplacer(File rootTemplateFolder, Properties templateVariables) {
        velocityEngine = new VelocityEngine()
        context = new VelocityContext()
        basepath = rootTemplateFolder.path
        context.put("file.resource.loader.path", basepath)
        context.put("file.resource.loader.cache", false)
        context.put("velocimacro.library.autoreload", true)
        for (String key: templateVariables.keySet()) {
            context.put(key, templateVariables.get(key))
        }
    }

    void merge(File file) {
        String resource = file.path.substring(basepath.length() + 1)
        Template template = velocityEngine.getTemplate(resource)
        FileWriter fileWriter = new FileWriter(file)
        template.merge(context, fileWriter)
        fileWriter.close()
    }
}

class APIProxyFlow {

    String name
    String desc
    String path
    String verb
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
//            System.out.println(file.path)
            if (file.isDirectory()) {
                walk(file)
            } else {
                executor.execute(file)
            }
        }
    }
}

class TemplateExecutor implements FileExecutor {

    private final TemplateReplacer templateRunner

    TemplateExecutor(File rootTemplateFolder, Properties templateVariables) {
        templateRunner = new TemplateReplacer(rootTemplateFolder, templateVariables)
    }

    @Override
    void execute(File file) {
        if (file.name.endsWith(".vm") || "pom.xml".equals(file.name)) {
            templateRunner.merge(file)
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
        FileUtils.moveFile(file, new File(file.parentFile.parentFile.path + "/" + file.name))
    }
}


class XmlFormater implements FileExecutor {

    final DocumentBuilderFactory dbFactory
    final DocumentBuilder dBuilder
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

class EnvSetup {

    private final Properties props
    EnvSetup(Properties props) {
        this.props = props
    }

    private Map<String, Map<String, String>> getFromParameters() {
        Map<String, Map<String, String>> setupMap = new HashMap<>()
        for (String env : props.get("envs")) {
            Map<String, String> envMap = new HashMap<>()
            envMap.put("org", props.get("org"))
            envMap.put("env", env)
            envMap.put("authtype", props.get("authtype"))
            envMap.put("virtualhost", props.get("virtualhost"))
            envMap.put("targeturl", props.get("targeturl"))
            envMap.put("options", props.get("options"))
            envMap.put("delay", props.get("delay"))
            envMap.put("tokenurl", props.get("tokenurl"))
            envMap.put("clientid", props.get("clientid"))
            envMap.put("clientsecret", props.get("clientsecret"))
            envMap.put("proxydomain", props.get("proxydomain"))
            envMap.put("proxyprotocol", props.get("proxyprotocol"))
            envMap.put("proxyport", props.get("proxyport"))
            envMap.put("hosturl", props.get("hosturl"))
            envMap.put("apiversion", props.get("apiversion"))
            envMap.put("config-options", props.get("config-options"))
            envMap.put("config-exportdir", props.get("config-exportdir"))
            envMap.put("config-dir", props.get("config-dir"))
            if ("true".equals(props.get("mockserver"))) {
                String targetUrl = props.get("proxyprotocol") + "://" + props.get("proxydomain") + ":" +  props.get("proxyport") + '/mock' +  props.get("basepath")
                envMap.put("targeturl", targetUrl)
            } else {
                envMap.put("targeturl", props.get("targeturl"))
            }
            setupMap.put(env, envMap)
        }
        return setupMap
    }

    Map<String, Map<String, String>> getSetupMap() {
        if (StringUtils.isNotBlank(props.getProperty("edgeSetupFile"))) {

        } else {
            return getFromParameters()
        }
    }
}

class Mocker {
    void genMock(File projectDir, File fileSpec) {
        CodegenConfigurator configurator = new CodegenConfigurator()
        configurator.setInputSpec(fileSpec.path)
        configurator.setGenerateAliasAsModel(true)
        configurator.setGeneratorName("nodejs-express-server")
        configurator.setVerbose(false)
        configurator.setLogToStderr(true)
        configurator.setOutputDir(new File(projectDir, "mock/apiproxy/resources/hosted").getAbsolutePath())
        configurator.setValidateSpec(false)
        GlobalSettings.clearProperty(CodegenConstants.MODELS)
        final ClientOptInput input = configurator.toClientOptInput()
        new DefaultGenerator().opts(input).generate()
        File configFile = new File(projectDir, "mock/apiproxy/resources/hosted/config.js")
        String content = IOUtils.toString(new FileInputStream(configFile))
        FileUtils.write(configFile, content.replaceAll("3000,", "process.env.PORT,"))
        File packageFile = new File(projectDir, "mock/apiproxy/resources/hosted/package.json")
        InputStreamReader is = new InputStreamReader(new FileInputStream(packageFile))
        JsonElement packageJson = JsonParser.parseReader(is)
        is.close()
        packageFile.delete()
        packageJson.getAsJsonObject().get("scripts").getAsJsonObject().remove("prestart")
        String json = new GsonBuilder().setPrettyPrinting().create().toJson(packageJson)
        FileWriter writer = new FileWriter(packageFile)
        writer.write(json)
        writer.close()
        Yaml yaml = new Yaml()
        File apiFile = new File(projectDir, "mock/apiproxy/resources/hosted/api/openapi.yaml")
        InputStream inputStream = new FileInputStream(apiFile)
        Map<String, Object> obj = yaml.load(inputStream)
        inputStream.close()

        for (Map server: (List<Map>) obj.get("servers")) {
            URL url = null
            try {
                url = new URL((String) ((Map) server).get("url"));
                String newUrl = url.getProtocol() + "://" + url.getHost();
                server.put("url", newUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        String yml = yaml.dump(obj)
        FileWriter fileWriter = new FileWriter(apiFile)
        fileWriter.write(yml)
        fileWriter.close()
    }
}

void log(String message) {
    println("--------------------------------------------- BEGIN MESSAGE ---------------------------------------------")
    println(message)
    println("--------------------------------------------- ENd MESSAGE -----------------------------------------------")
}

final OpenAPI openAPI
final String data
final String specLocation = request.getProperties().get("spec")
final String specAuthName = request.getProperties().get("spec.auth.name")
final String specAuthValue = request.getProperties().get("spec.auth.value")
final String specAuthType = request.getProperties().get("spec.auth.type")
final List<String> envs = Arrays.asList(request.getProperties().get("envs").split(","))
final String jsonEnvs = "[\"".concat(String.join("\",\"", envs)).concat("\"]")
URL specUrl = spec.matches("^(https?|file)://.*") ? new URL(spec) : new File(spec).toURI().toURL()
final Properties properties = new Properties()
properties.putAll(request.getProperties())
properties.put("jsonEnvs", jsonEnvs)
properties.put("envs", envs)


List<String> keys = new ArrayList<>()
for (String key: properties.keys()) {
    if ('${empty.property}'.equals(properties.get(key))) {
        keys.add(key)
    }
}
for (String key: keys) {
    properties.remove(key)
}


if (specLocation.matches("^https?://.*") && StringUtils.isNotBlank(specAuthType)) {
    AuthorizationValue authorizationValue = new AuthorizationValue(specAuthName, specAuthValue, specAuthType)
    OpenAPIV3Parser parser = new OpenAPIV3ParserData()
    openAPI = parser.readWithInfo(spec, Arrays.asList(authorizationValue)).getOpenAPI()
    data = ( (OpenAPIV3ParserData) parser ).getSpecData()
} else {
    OpenAPIV3Parser parser = new OpenAPIV3ParserData()
    openAPI = parser.readWithInfo(spec, (List)null).getOpenAPI()
    data = ( (OpenAPIV3ParserData) parser ).getSpecData()
}

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
EnvSetup envSetup = new EnvSetup(properties)
properties.put("setupMap", envSetup.getSetupMap())

File outputDirectory = new File(request.outputDirectory)
File projectDir = new File(outputDirectory, request.artifactId)

File docDir = new File(projectDir, "doc")
docDir.mkdirs()
File specFile = new File(docDir, new File(specUrl.getFile()).getName())
properties.put("specFileName", specFile.getName())
FileWriter fw = new FileWriter(specFile)
fw.write(data)
fw.close()

FileUtils.copyFile(specFile, new File(projectDir, "mock/apiproxy/resources/hosted/api/"+specFile.name))
File profileFile = new File(projectDir, "config/profile-env.yaml.vm")
File _envDir = new File(projectDir, "edge/_env")
for (String env: envs) {

    File targetProfileFile = new File(projectDir, String.format("config/profile-%s.yaml.vm", env))
    FileUtils.copyFile(profileFile, targetProfileFile)
    File envDir = new File(projectDir, "edge/env/".concat(env))
    envDir.mkdirs()
    properties.put("env", env)
    TemplateReplacer templateReplacer = new TemplateReplacer(outputDirectory, properties)
    templateReplacer.merge(targetProfileFile)
    for (File configFile: _envDir.listFiles()) {
        if (configFile.isFile()) {
            FileUtils.copyFileToDirectory(configFile, envDir)
        }
    }
}

profileFile.delete()
FileUtils.forceDelete(_envDir)

new FileWalker(new RootFilesMover()).walk(new File(projectDir, "root"))
new FileWalker(new TemplateExecutor(outputDirectory, properties)).walk(projectDir)
new FileWalker(new FileRenamer()).walk(projectDir)
new FileWalker(new XmlFormater()).walk(projectDir)
new Mocker().genMock(projectDir, specFile)

new File(projectDir, "root").delete()
if (!"true".equals(request.getProperties().get("enable-cors"))) {
    new File(projectDir, "apiproxy/policies/" + request.getProperties().get("cors-policy-name") + ".xml").delete()
}




