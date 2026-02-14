# Publish to Maven Central — Step by Step

## Before you start

- **Release version**: Change `1.0-SNAPSHOT` to `1.0.0` in `pom.xml` — Maven Central does not accept SNAPSHOT for first publish. *(Already set to 1.0.0)*

---

## Step 1: Claim namespace on Sonatype Central

1. Go to **https://central.sonatype.com/**
2. Sign in with your **GitHub** account (BIBEK7932)
3. **Create publisher** → choose **io.github.bibek7932**
4. Prove ownership: Sonatype will check that you control the GitHub user **BIBEK7932**
5. Namespace is approved (often immediately)

---

## Step 2: Generate a user token

1. In **https://central.sonatype.com/** → **Your profile** (top right)
2. **Generate user token**
3. Copy **Username** and **Token** (password)
4. Store them securely; the token is shown only once

---

## Step 3: Configure Maven `settings.xml`

Create or edit `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>central</id>
      <username>YOUR_CENTRAL_USERNAME</username>
      <password>YOUR_CENTRAL_TOKEN</password>
    </server>
  </servers>
</settings>
```

Replace `YOUR_CENTRAL_USERNAME` and `YOUR_CENTRAL_TOKEN` with the values from Step 2.

---

## Step 4: Set up GPG signing

### Install GPG

- **macOS**: `brew install gnupg`
- **Windows**: https://www.gnupg.org/download/
- **Linux**: `sudo apt install gnupg` or `sudo yum install gnupg`

### Generate a key

```bash
gpg --gen-key
```

Use your name and email, and choose a strong passphrase.

### Get your key ID

```bash
gpg --list-keys
```

Use the part after `pub` (e.g. `ABC12345`).

### Publish the public key

```bash
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
```

### (Optional) Configure Maven to use GPG

Add to `~/.m2/settings.xml` inside `<settings>`:

```xml
<profiles>
  <profile>
    <id>gpg</id>
    <properties>
      <gpg.keyname>YOUR_KEY_ID</gpg.keyname>
    </properties>
  </profile>
</profiles>
<activeProfiles>
  <activeProfile>gpg</activeProfile>
</activeProfiles>
```

---

## Step 5: Set release version in `pom.xml`

```xml
<version>1.0.0</version>
```

(Remove `-SNAPSHOT`.)

---

## Step 6: Deploy

```bash
cd /path/to/spring-request-normalizer
mvn clean deploy
```

- Maven will ask for your **GPG passphrase**
- Build runs, artifacts are signed and uploaded
- Plugin uploads to Central and publishes

---

## Step 7: Verify on Maven Central

1. Wait 5–30 minutes for indexing
2. Open: https://central.sonatype.com/artifact/io.github.bibek7932/spring-request-normalizer

---

## Step 8: Use in another project

```xml
<dependency>
    <groupId>io.github.bibek7932</groupId>
    <artifactId>spring-request-normalizer</artifactId>
    <version>1.0.0</version>
</dependency>
```

No extra repository configuration is needed; Maven Central is used by default.

---

## Troubleshooting

| Issue | Action |
|-------|--------|
| GPG passphrase prompt fails | Ensure `gpg` is in `PATH`; on macOS you may need `gpg2` |
| "Namespace not found" | Finish Step 1 and wait for approval |
| "Authentication failed" | Check `settings.xml` and token in Central Portal |
| Javadoc errors | `doclint=none` is set to skip doc errors |
| "Invalid POM" | Confirm `url`, `licenses`, `scm`, `developers` in `pom.xml` |
