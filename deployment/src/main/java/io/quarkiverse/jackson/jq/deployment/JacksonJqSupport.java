package io.quarkiverse.jackson.jq.deployment;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.deployment.ApplicationArchive;
import io.quarkus.deployment.builditem.ApplicationArchivesBuildItem;
import io.quarkus.deployment.recording.RecorderContext;
import net.thisptr.jackson.jq.Function;
import net.thisptr.jackson.jq.VersionRange;
import net.thisptr.jackson.jq.internal.JqJson;

final class JacksonJqSupport {
    private JacksonJqSupport() {
    }

    static List<JqJson.JqFuncDef> lookupFunctionsFromConfig(
            ApplicationArchivesBuildItem archives,
            JacksonJqConfig config) throws IOException {

        List<JqJson.JqFuncDef> answer = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        for (ApplicationArchive archive : archives.getAllApplicationArchives()) {
            for (Path root : archive.getRootDirectories()) {
                final Path resourcePath = root.resolve("net/thisptr/jackson/jq/jq.json");

                if (!Files.exists(resourcePath)) {
                    continue;
                }
                if (!Files.isRegularFile(resourcePath)) {
                    continue;
                }

                String functions = Files.readAllLines(resourcePath, StandardCharsets.UTF_8)
                        .stream()
                        .filter(l -> !l.isEmpty())
                        .filter(l -> !l.startsWith("#"))
                        .collect(Collectors.joining());

                mapper.readValue(functions, JqJson.class).functions.stream()
                        .filter(f -> f.version == null || f.version.contains(config.functions.version))
                        .forEach(answer::add);
            }
        }

        return answer;

    }

    static List<JacksonJqFunctionBuildItem> lookupFunctions(
            IndexView indexView,
            JacksonJqConfig config,
            RecorderContext context,
            Class<?> annotationType) {

        List<JacksonJqFunctionBuildItem> answer = new ArrayList<>();

        DotName f = DotName.createSimple(Function.class.getName());
        DotName a = DotName.createSimple(annotationType.getName());

        for (ClassInfo ci : indexView.getAllKnownImplementors(f)) {
            AnnotationInstance annotation = ci.declaredAnnotation(a);
            if (annotation == null) {
                continue;
            }

            AnnotationValue values = annotation.value("value");
            AnnotationValue version = annotation.value("version");

            for (String name : values.asStringArray()) {
                var function = JacksonJqFunctionBuildItem.of(
                        name,
                        context.newInstance(ci.name().toString()));

                if (version != null && !version.asString().isEmpty()) {
                    if (!VersionRange.valueOf(version.asString()).contains(config.functions.version)) {
                        continue;
                    }
                }

                answer.add(function);
            }
        }

        return answer;
    }
}
