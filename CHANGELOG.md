# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [1.0.0] - 2026-02-15

### Added

- Initial release
- `@NormalizeInput` annotation for request body DTOs
- Trim whitespace from string fields
- Convert blank/empty strings to `null`
- Optional collapse of multiple spaces to single space
- Support for Spring MVC `@RequestBody`
- Support for RestClient / HTTP Interface (request and response)
- Auto-configuration (no manual setup required)
