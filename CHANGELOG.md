# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog][], and this project adheres to
[Semantic Versioning][].

## 1.0.0 - 2025-04-11

### Added

  - Initial release of AutoHibernate
  - Automatic server hibernation when no players are connected
  - Instant resume when a player begins connecting
  - JSON-based configuration (`config/autohibernate.json`)
  - `/autohibernate` command with `status`, `autopause`, `sleep`, and
    `wake` subcommands
  - Console command support while hibernating
  - Watchdog timer spoofing to prevent false crash detection


[Keep a Changelog]: https://keepachangelog.com/en/1.1.0/
[Semantic Versioning]: https://semver.org/spec/v2.0.0.html
