package io.quarkiverse.jackson.jq.it;

import java.util.List;
import java.util.Locale;

import net.thisptr.jackson.jq.v2.json.JsonProvider;
import net.thisptr.jackson.jq.v2.spi.BindContext;
import net.thisptr.jackson.jq.v2.spi.Expression;
import net.thisptr.jackson.jq.v2.spi.Function;
import net.thisptr.jackson.jq.v2.spi.RuntimeContext;
import net.thisptr.jackson.jq.v2.spi.annotations.FunctionRegistration;
import net.thisptr.jackson.jq.v2.spi.exception.JsonQueryException;
import net.thisptr.jackson.jq.v2.spi.path.UntrackedPath;

/**
 * Greets a name in a given language.
 * <ul>
 * <li>{@code myGreet(name)} greets in English.</li>
 * <li>{@code myGreet(lang; name)} greets in the given language: "EN", "ES", or "IT". Unknown languages are
 * treated as "EN".</li>
 * </ul>
 */
@FunctionRegistration(name = "myGreet", nargs = 1)
@FunctionRegistration(name = "myGreet", nargs = 2)
public class MyGreetFunction implements Function {

    @Override
    public <Context extends RuntimeContext, JsonNode> Expression<Context, JsonNode> bind(BindContext<JsonNode> bindCtx,
            List<Expression<Context, JsonNode>> args) throws JsonQueryException {
        JsonProvider<JsonNode> json = bindCtx.getJsonProvider();

        return (ctx, in, ipath, output) -> {
            if (args.size() == 1) {
                args.get(0).apply(ctx, in, UntrackedPath.getInstance(),
                        (name, p) -> output.emit(greet(json, "EN", name), UntrackedPath.getInstance()));
            } else {
                args.get(0).apply(ctx, in, UntrackedPath.getInstance(),
                        (lang, p1) -> args.get(1).apply(ctx, in, UntrackedPath.getInstance(),
                                (name, p2) -> output.emit(greet(json, text(json, lang), name), UntrackedPath.getInstance())));
            }
        };
    }

    private static <JsonNode> JsonNode greet(JsonProvider<JsonNode> json, String lang, JsonNode name) {
        final String greeting = switch (lang.toUpperCase(Locale.ROOT)) {
            case "ES" -> "Hola";
            case "IT" -> "Ciao";
            default -> "Hello"; // "EN" and any unknown language
        };
        return json.createString(greeting + ", " + text(json, name) + "!");
    }

    // strings are used as-is, anything else is rendered as JSON
    private static <JsonNode> String text(JsonProvider<JsonNode> json, JsonNode value) {
        return json.isString(value) ? json.getString(value) : json.format(value);
    }
}
