[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-5-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

# Quarkus - Jackson Jq

[`jq`](https://stedolan.github.io/jq/) is a very popular JSON processor.
[Jackson JQ](https://github.com/eiiches/jackson-jq) is an implementation of this processor in Java, and now you can
integrate it in your Quarkus application!

> **⚠️** Version 3.x.x of this extension (`main` branch) supports Quarkus 4 and Jackson Jq 2.x.x.

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

jackson-jq ships a set of extension modules (`net.thisptr.jackson.jq.v2:jackson-jq-ext-module-*`). This extension
manages them for you: it detects which ones are part of your project and makes them available in the jq `Environment`.

| Module id | Artifact (`net.thisptr.jackson.jq.v2`) | Default path           | Included by default |
|-----------|----------------------------------------|------------------------|---------------------|
| `uuid`    | `jackson-jq-ext-module-uuid`           | `jackson-jq/uuid`      | yes                 |
| `time`    | `jackson-jq-ext-module-time`           | `jackson-jq/time`      | yes                 |
| `uri`     | `jackson-jq-ext-module-uri`            | `jackson-jq/uri`       | yes                 |
| `random`  | `jackson-jq-ext-module-random`         | `jackson-jq/random`    | yes                 |
| `joni`    | `jackson-jq-ext-module-joni`           | `jackson-jq/joni`      | yes                 |
| `binary`  | `jackson-jq-ext-module-binary`         | `jackson-jq/binary`    | no                  |
| `debug`   | `jackson-jq-ext-module-debug`          | `jackson-jq/debug`     | no                  |
| `fs`      | `jackson-jq-ext-module-fs`             | `jackson-jq/fs`        | no                  |
| `gzip`    | `jackson-jq-ext-module-gzip`           | `jackson-jq/gzip`      | no                  |
| `http`    | `jackson-jq-ext-module-http`           | `jackson-jq/http`      | no                  |
| `os`      | `jackson-jq-ext-module-os`             | `jackson-jq/os`        | no                  |
| `re2`     | `jackson-jq-ext-module-re2`            | `jackson-jq/re2`       | no                  |
| `zstd`    | `jackson-jq-ext-module-zstd`           | `jackson-jq/zstd`      | no                  |

The modules included by default are the ones that run safely in memory. The rest are activated by adding the
corresponding dependency to your project, e.g.:

```xml
<dependency>
  <groupId>net.thisptr.jackson.jq.v2</groupId>
  <artifactId>jackson-jq-ext-module-os</artifactId>
  <version>${jackson-jq.version}</version>
</dependency>
```

Notes:

- Only the modules listed above are recognized by the extension. The `<version>` must match the jackson-jq version used by the extension.
- The `joni` and `re2` modules are two alternative regular expression engines and cannot be active at the same time.
  To use `re2` you must disable the bundled one with `quarkus.jackson.jq.modules.joni.enabled=false`, otherwise the build fails.
- A module can be turned off with `quarkus.jackson.jq.modules.<id>.enabled=false`.
- Configuring a module (`quarkus.jackson.jq.modules.<id>.*`) that is not in the project dependencies only logs a warning.
  Likewise for an unknown module id.

You can find more information on the modules in: [extra module](https://github.com/eiiches/jackson-jq/blob/develop/2.x/docs/modules/README.md).

### How a module is made available: the strategy

Each active module is incorporated into the `Environment` with one of three strategies, selected with
`quarkus.jackson.jq.modules.<id>.strategy`:

| Strategy             | How the module functions are used in an expression                                       | Extra properties                                        |
|----------------------|------------------------------------------------------------------------------------------|---------------------------------------------------------|
| `register` (default) | The expression must `import` or `include` the module by its path.                        | `path` (optional). Defaults to `jackson-jq/<id>`.       |
| `import`             | Available as `alias::function(...)`, no `import` needed in the expression.               | `alias` (**mandatory**).                                |
| `include`            | Available by the bare function name, e.g. `function(...)`, no `import` needed.          | none                                                    |

Examples, with the corresponding properties in `application.properties`:

```properties
# 'register' (default). The 'uuid' module is registered under "jackson-jq/uuid".
# Expression: import "jackson-jq/uuid" as uuid; uuid::uuid4
#         or: include "jackson-jq/uuid"; uuid4

# 'register' with a custom path.
quarkus.jackson.jq.modules.time.path=jackson-jq/time-example-path
# Expression: import "jackson-jq/time-example-path" as time; 1792152626431 | time::strftime("yyyy-MM-dd HH:mm:ss.SSSXXX"; "UTC")

# 'import'. The alias is mandatory and is used as the prefix of the module functions.
quarkus.jackson.jq.modules.uri.strategy=import
quarkus.jackson.jq.modules.uri.alias=uriexamplealias
# Expression: "http://bob@example.com:8080/pets" | uriexamplealias::uriparse

# 'include'. The functions are available by name.
quarkus.jackson.jq.modules.random.strategy=include
# Expression: 123 | random
```

The `alias` is used as `alias::function(...)` in the expressions, so it must be a plain identifier (letters, digits and `_`),
e.g. `uriexamplealias` and not `uri-example-alias`. The `alias` property is only used by `import`, and `path` only by `register`.
A module configured with `import` and without an `alias` makes the build fail.

#### Regular expressions

The regular expression functions (`test`, `match`, `capture`, `scan`, `splits`, `sub`, `gsub`...) are provided by the
`joni` module. With the default strategy you must reference the module in your expression:

```
import "jackson-jq/joni" as joni; "aba" | [joni::splits("b")]
```

If you prefer to call them by their bare name, as in plain `jq`, configure the module with the `include` strategy:

```properties
quarkus.jackson.jq.modules.joni.strategy=include
```

```
"aba" | [splits("b")]
```

## Configuration

| Property                                           | Default    | Description                                                                                                       |
|----------------------------------------------------|------------|-------------------------------------------------------------------------------------------------------------------|
| `quarkus.jackson.jq.functions.version`             | `1.8.2`    | The supported jq version.                                                                                         |
| `quarkus.jackson.jq.functions.excludes`            |            | List of function names to exclude. See [Excluding functions](#excluding-functions).                               |
| `quarkus.jackson.jq.modules."<id>".enabled`        | `true`     | Enables/disables a module that is part of the project dependencies. Including the modules shipped by default.     |
| `quarkus.jackson.jq.modules."<id>".strategy`       | `register` | How the module is incorporated into the `Environment`: `register`, `import` or `include`.                         |
| `quarkus.jackson.jq.modules."<id>".alias`          |            | Mandatory if the strategy is `import`. Ignored otherwise.                                                         |
| `quarkus.jackson.jq.modules."<id>".path`           |            | Only applies if the strategy is `register`. The path to register the module under, defaults to `jackson-jq/<id>`. |

All the properties are fixed at build time.

## Excluding functions

Functions can be removed from the jq `Environment` with the `quarkus.jackson.jq.functions.excludes` property, e.g.:

```properties
quarkus.jackson.jq.functions.excludes=now,min_by,@text,uuid3
```

- The functions are excluded by name and the exclusion applies to every arity, e.g. `now` excludes `now/0` and any other `now/N`.
  Functions starting with `@` are excluded by the full name, e.g. `@text`.
- It applies to the built-in functions, to your own custom functions, and to the functions that the extension modules implement in Java (e.g. `uuid3` of the `uuid` module).
- A query that invokes an excluded function fails with a `Function ... does not exist` error.
- It does **not** apply to the functions that a module defines in jq source code. For example, the regular expression
  functions provided by the `joni` module (`test`, `match`, `splits`, `sub`, `gsub`...) are not removed by excluding their names.
  If you want to get rid of those, disable the module with `quarkus.jackson.jq.modules.joni.enabled=false`, or by exluding any the lower level primitives functions used internally to buidl them, e.g. `_match_impl` and `_sub_impl`.

## Adding Custom functions

The extension support adding custom functions to the jq engine, as example, to add a function that accept one or two parameters:

```java
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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

    private static <JsonNode> String text(JsonProvider<JsonNode> json, JsonNode value) {
        return json.isString(value) ? json.getString(value) : Objects.toString(value);
    }
}
```

The function is discovered at build time, no additional configuration is needed: any class of your application that
implements `net.thisptr.jackson.jq.v2.spi.Function` and is annotated with `@FunctionRegistration` is registered
(repeat the annotation to register different arities).

The function will then be available to any jq expression i.e.

```shell
.items[] | myGreet("EN"; "Bob")
```

Considerations:

- Only the classes of your application are scanned. Functions packaged in other libraries are not discovered.
- The `version` attribute of `@FunctionRegistration` restricts the jq versions where the function is available, and
  the function is registered only when the configured `quarkus.jackson.jq.functions.version` is in the range:

  ```java
  @FunctionRegistration(name = "myFunc", nargs = 0, version = @VersionRangeSpec(min = @VersionSpec(major = 1, minor = 6, patch = 0)))
  ```

  A bound that is not set is unbounded. The `min` bound is inclusive and the `max` bound is exclusive by default, which
  can be changed with `minInclusive` and `maxInclusive`.
- The functions are also subject to the `quarkus.jackson.jq.functions.excludes` property.

## Migrating from 2.x

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
