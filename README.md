[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-5-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

# Quarkus - Jackson Jq

[`jq`](https://stedolan.github.io/jq/) is a very popular JSON processor.
[Jackson JQ](https://github.com/eiiches/jackson-jq) is an implementation of this processor in Java, and now you can
integrate it in your Quarkus application!

> **⚠️** Version 2.x.x of this extension (`main` branch) supports Quarkus 4 and Jackson Jq 2.x.x.

## Getting Started

Simply add the dependency to your Quarkus project:

```xml
<dependency>
  <groupId>io.quarkiverse.jackson-jq</groupId>
  <artifactId>quarkus-jackson-jq</artifactId>
</dependency>
```

Then you can simply use the `jackson-jq`'s
[`Environment`](https://github.com/eiiches/jackson-jq/blob/develop/2.x/examples/MinimalTest.java) bean in
your services. For example:

```java
@Path("/jackson-jq")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JacksonJqResource {

    @Inject
    Environment<JsonNode> environment;

    @POST
    public List<JsonNode> parse(Document document) throws JsonQueryException {
        final JsonQuery query = environment.compile(document.expression);
        List<JsonNode> out = new ArrayList<>();
        query.apply(document.document, out::add);
        return out;
    }
}
```

## Jackson Jq Extra Modules

By default, this extension already includes the following extra modules, so there is no need to add them to your project:

- `jackson-jq-ext-module-uuid`
- `jackson-jq-ext-module-time`
- `jackson-jq-ext-module-uri`

You can find more information on additional modules in: [extra module](https://github.com/eiiches/jackson-jq/blob/develop/2.x/docs/modules/README.md).

## Configuration

| Property                                | Default | Description                                                                     |
|-----------------------------------------|---------|---------------------------------------------------------------------------------|
| `quarkus.jackson.jq.functions.version`  | `1.8.2` | The supported jq version.                                                       |
| `quarkus.jackson.jq.functions.excludes` |         | **Deprecated**, it has no effect anymore and will be removed in a future release. |

## Adding Custom functions

The extension support adding custom functions to the jq engine, as example, to add a function that accept one or two parameters:

```java
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
```

You must add the following ServiceLoader configuration file to your project referring the given function.

````
src/main/resources/META-INF/services/net.thisptr.jackson.jq.v2.spi.Function
````
and configure the given function implementation class.
````
io.quarkiverse.jackson.jq.it.MyGreetFunction
````

The function will then be available to any jq expression i.e.

```shell
.items[] | myGreet("EN"; "Bob")
```

## Migrating from 1.x

Custom functions annotated with `@JqFunction` must be migrated to `@FunctionRegistration`.
See the `myGreet` function in the [Adding Custom functions](#adding-custom-functions) section for an example of the new style.

## Considerations

Underneath, this extension uses `jackson-jq`, so the
same [limitations and capabilities](https://github.com/eiiches/jackson-jq#implementation-status)
of that library also applies here.

If you encounter any bugs or have any questions, please feel free to open an issue.

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://ricardozanini.medium.com/"><img src="https://avatars.githubusercontent.com/u/1538000?v=4?s=100" width="100px;" alt="Ricardo Zanini"/><br /><sub><b>Ricardo Zanini</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-jackson-jq/commits?author=ricardozanini" title="Code">💻</a> <a href="#maintenance-ricardozanini" title="Maintenance">🚧</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/eiiches"><img src="https://avatars.githubusercontent.com/u/230747?v=4?s=100" width="100px;" alt="Eiichi Sato"/><br /><sub><b>Eiichi Sato</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-jackson-jq/pulls?q=is%3Apr+reviewed-by%3Aeiiches" title="Reviewed Pull Requests">👀</a></td>
      <td align="center" valign="top" width="14.28%"><a href="http://lburgazzoli.github.io"><img src="https://avatars.githubusercontent.com/u/1868933?v=4?s=100" width="100px;" alt="Luca Burgazzoli"/><br /><sub><b>Luca Burgazzoli</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-jackson-jq/commits?author=lburgazzoli" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://majiehong.com"><img src="https://avatars.githubusercontent.com/u/1061229?v=4?s=100" width="100px;" alt="Jiehong"/><br /><sub><b>Jiehong</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-jackson-jq/commits?author=Jiehong" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="http://thegreatapi.com"><img src="https://avatars.githubusercontent.com/u/11776454?v=4?s=100" width="100px;" alt="Helber Belmiro"/><br /><sub><b>Helber Belmiro</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-jackson-jq/commits?author=hbelmiro" title="Code">💻</a></td>
    </tr>
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!
