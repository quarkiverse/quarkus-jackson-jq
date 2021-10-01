[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-1-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

# Quarkus - Jackson Jq

[`jq`](https://stedolan.github.io/jq/) is a very popular JSON processor.
[Jackson JQ](https://github.com/eiiches/jackson-jq) is an implementation of this processor in Java, and now you can
integrate it in your Quarkus application!

## Getting Started

Simply add the dependency to your Quarkus project:

```xml

<dependency>
  <groupId>io.quarkiverse.jackson-jq</groupId>
  <artifactId>quarkus-jackson-jq</artifactId>
</dependency>
```

Then you can simply use the `jackson-jq`'s
[`Scope`](https://github.com/eiiches/jackson-jq/blob/develop/1.x/jackson-jq/src/test/java/examples/Usage.java) bean in
your services. For example:

```java

@Path("/jackson-jq")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JacksonJqResource {

    @Inject
    Scope scope;

    @POST
    public List<JsonNode> parse(Document document) throws JsonQueryException {
        final JsonQuery query = JsonQuery.compile(document.expression, Versions.JQ_1_6);
        List<JsonNode> out = new ArrayList<>();
        query.apply(this.scope, document.document, out::add);
        return out;
    }
}
```

## Considerations

Underneath, this extension uses `jackson-jq`, so the
same [limitations and capabilities](https://github.com/eiiches/jackson-jq#implementation-status)
of that library also applies here.

If you encounter any bugs or have any questions, please feel free to open an issue.

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="https://ricardozanini.medium.com/"><img src="https://avatars.githubusercontent.com/u/1538000?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Ricardo Zanini</b></sub></a><br /><a href="https://github.com/quarkiverse/quarkus-jackson-jq/commits?author=ricardozanini" title="Code">💻</a> <a href="#maintenance-ricardozanini" title="Maintenance">🚧</a></td>
  </tr>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!