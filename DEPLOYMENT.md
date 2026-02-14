# Deployment Guide

## Pre-publish checklist

- [ ] Update `pom.xml`: set `url`, `scm`, `developers` to match your GitHub repo
- [ ] Change version from `1.0-SNAPSHOT` to `1.0.0` for release
- [ ] Run `mvn clean package` and ensure all tests pass

---

## Option A: Local install (fastest for personal use)

Use when you only need the library in your own projects.

### Step 1: Install to local Maven repo

```bash
cd /path/to/empty-to-null-core
mvn clean install
```

### Step 2: Use in another project

**Maven (`pom.xml`):**
```xml
<dependency>
    <groupId>io.github.bibek</groupId>
    <artifactId>spring-request-normalizer</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

**Gradle:**
```groovy
implementation 'io.github.bibek:spring-request-normalizer:1.0-SNAPSHOT'
```

### Step 3: Add the annotation to your DTOs

```java
@NormalizeInput
public class CreateUserRequest {
    private String email;
    private String name;
}
```

---

## Option B: Deploy to Maven Central (for public use)

### Prerequisites

1. **GitHub** – create repo `spring-request-normalizer` under your account
2. **Sonatype JIRA** – account at https://issues.sonatype.com
3. **GPG** – installed for signing artifacts

### Step 1: Claim groupId on Sonatype

1. Create JIRA account at https://issues.sonatype.com
2. Create a new project ticket to publish `io.github.bibek`
3. Prove you own the domain (GitHub `bibek` user → `io.github.bibek`)
4. Wait for approval

### Step 2: Configure GPG signing

```bash
# Generate key
gpg --gen-key

# List keys and get key ID
gpg --list-keys

# Publish public key
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
```

### Step 3: Add deployment config to `pom.xml`

Add this before `</project>`:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-gpg-plugin</artifactId>
            <version>3.2.6</version>
            <executions>
                <execution>
                    <id>sign-artifacts</id>
                    <phase>verify</phase>
                    <goals>
                        <goal>sign</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
        <plugin>
            <groupId>org.sonatype.central</groupId>
            <artifactId>central-publishing-maven-plugin</artifactId>
            <version>0.8.0</version>
            <extensions>true</extensions>
            <configuration>
                <publishingServerId>central</publishingServerId>
                <autoPublish>true</autoPublish>
            </configuration>
        </plugin>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-deploy-plugin</artifactId>
            <version>3.1.1</version>
            <configuration>
                <skip>true</skip>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Step 4: Create `~/.m2/settings.xml` with credentials

```xml
<settings>
  <servers>
    <server>
      <id>central</id>
      <username>YOUR_SONATYPE_JIRA_USERNAME</username>
      <password>YOUR_SONATYPE_TOKEN</password>
    </server>
  </servers>
</settings>
```

Use a [Central Portal token](https://central.sonatype.com/) instead of password when possible.

### Step 5: Release

```bash
# 1. Update version to release (e.g. 1.0.0)
# Edit pom.xml: <version>1.0.0</version>

# 2. Deploy (will prompt for GPG passphrase)
mvn clean deploy

# 3. After success, publish on Central Portal
# Go to https://central.sonatype.com/ → Staging → Promote
```

### Step 6: Use from Maven Central

After sync (~15 min to a few hours):

```xml
<dependency>
    <groupId>io.github.bibek</groupId>
    <artifactId>spring-request-normalizer</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## Option C: GitHub Packages (easier than Maven Central)

### Step 1: Add to `pom.xml` (before `</project>`)

```xml
<distributionManagement>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/bibek/spring-request-normalizer</url>
    </repository>
</distributionManagement>
```

### Step 2: Create GitHub Personal Access Token

1. GitHub → Settings → Developer settings → Personal access tokens
2. Create token with `write:packages` scope

### Step 3: Add to `~/.m2/settings.xml`

```xml
<servers>
  <server>
    <id>github</id>
    <username>YOUR_GITHUB_USERNAME</username>
    <password>YOUR_GITHUB_TOKEN</password>
  </server>
</servers>
```

### Step 4: Deploy

```bash
mvn clean deploy
```

### Step 5: Use in another project

Add to consuming project's `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/bibek/spring-request-normalizer</url>
    </repository>
</repositories>

<dependency>
    <groupId>io.github.bibek</groupId>
    <artifactId>spring-request-normalizer</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## Quick reference: Using in your project

1. **Add dependency** (see examples above)
2. **Ensure** `spring-boot-starter-web` is on the classpath
3. **Annotate** your DTO: `@NormalizeInput`
4. **No extra config** – auto-configuration applies automatically
