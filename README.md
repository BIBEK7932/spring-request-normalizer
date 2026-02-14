# spring-request-normalizer

A Spring Boot library that automatically normalizes string fields in `@RequestBody` DTOs—trim whitespace, convert blank to null, and optionally collapse multiple spaces.

## Features

- **Trim** — leading/trailing whitespace removed (default: `true`)
- **Blank to null** — empty or blank strings converted to `null` (default: `true`)
- **Collapse spaces** — multiple consecutive spaces → single space (default: `false`)
- **Works like Lombok** — one annotation per class or parameter; applies to all `String` fields
- **Auto-configuration** — no manual setup needed

## Requirements

- Java 17+
- Spring Boot 3.x
- `spring-boot-starter-web`

## Installation

### Maven

```xml
<dependency>
    <groupId>io.github.bibek7932</groupId>
    <artifactId>spring-request-normalizer</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.github.bibek7932:spring-request-normalizer:1.0.0'
```

## Usage

### HTTP Interface / RestClient (request + response)

Annotate both your request and response DTOs. Use **mutable classes** (records have final fields and cannot be modified):

```java
@HttpExchange(url = "/webresources")
public interface CerfIvrClient {

    @PostExchange("/OBDCampaign")
    CerfCampaignTriggerResponse triggerBulkIvrCampaign(@RequestBody CerfIvrRequest request);
}

@NormalizeInput
class CerfIvrRequest {
    public String campaignId;
    public String phoneNumber;
}

@NormalizeInput
class CerfCampaignTriggerResponse {
    public String status;
    public String message;
}
```

Both `CerfIvrRequest` (before sending) and `CerfCampaignTriggerResponse` (after receiving) will have their string fields normalized automatically.

### On the DTO class (recommended) — @RestController

```java
@NormalizeInput
public class CreateUserRequest {
    private String email;   // "  " → null, " foo@bar.com " → "foo@bar.com"
    private String name;
}

@RestController
@RequestMapping("/users")
public class UserController {

    @PostMapping
    public void create(@RequestBody CreateUserRequest req) {
        // req.email and req.name are already normalized
    }
}
```

### On the method parameter

Use when the DTO class is from a library you cannot annotate:

```java
@PostMapping("/users")
public void create(@RequestBody @NormalizeInput CreateUserRequest req) {
    // req is normalized even if CreateUserRequest has no annotation
}
```

### With options

```java
@NormalizeInput(trim = true, blankToNull = true, collapseSpaces = true)
public class SearchRequest {
    private String query;   // "  hello    world  " → "hello world"
}
```

## Configuration defaults

| Option         | Default | Description                          |
|----------------|---------|--------------------------------------|
| `trim`         | `true`  | Trim leading/trailing whitespace     |
| `blankToNull`  | `true`  | Convert blank/empty strings to `null`|
| `collapseSpaces` | `false` | Collapse multiple spaces to one   |

## Normalization order

1. Collapse spaces (if enabled)  
2. Trim  
3. Blank to null (if enabled)

## License

MIT License — see [LICENSE](LICENSE) for details.
