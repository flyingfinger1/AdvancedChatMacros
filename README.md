# AdvancedChatMacros

AdvancedChatMacros is a module for AdvancedChat that provides ways to hook into filters, modify
sent messages, and bind chat actions (commands, copies, info messages) to keys.

> **Refurbished fork.** DarkKronicle archived the original project. This fork brings AdvancedChatMacros
> from Minecraft **1.18.2** all the way up to **Minecraft 26.2** and modernises the codebase: ported
> from Yarn to the new Mojang names and the 26.x APIs. Because it skipped so many versions, two
> long-gone libraries were replaced — the old *KommandLib* client-command library and Core's removed
> `CommandsHandler` now use Fabric's client-command API, and chat output uses the 26.x
> `ChatComponent` API. It is a module of the refurbished
> [AdvancedChatCore](https://github.com/flyingfinger1/AdvancedChatCore).

## Requirements

| | Version |
| --- | --- |
| Minecraft | **26.2** |
| Java | **25** (required by Minecraft 26.x) |
| Fabric Loader | 0.19.0+ |

## Dependencies

The following are **required** for this mod to run:

- [AdvancedChatCore](https://github.com/flyingfinger1/AdvancedChatCore) **1.6.2+** (this fork's build)
- [MaLiLib](https://modrinth.com/mod/malilib) — for 26.x use the sakura-ryoko builds
- [Fabric API](https://modrinth.com/mod/fabric-api)

Optional:

- [AdvancedChatFilters](https://github.com/flyingfinger1/AdvancedChatFilters) **1.3.0+** — when present,
  macros can integrate with the filter system. The module loads this integration only if Filters is installed.

[Mod Menu](https://modrinth.com/mod/modmenu) is recommended to open the configuration screen.

## Features

- Bind commands, copies, info messages and chat suggestions to keys
- Run macros from chat filters (Konstruct templating)
- Auto-respond to matched messages with `.toml` match filters
- `/acmacros reloadToml` and the `[[reloadMacros]]` / `[[reloadKeybinds]]` send-text triggers reload
  your configuration without a restart

## Keybinds

Keybinds are defined in a [Konstruct](https://darkkronicle.github.io/Konstruct/) script, not an
in-game GUI. On first launch an `example_keybinds.knst` is written to
`config/advancedchat/advancedchatmacros/`; rename it to `keybinds.knst`, edit it, then type
`[[reloadKeybinds]]` in chat to apply. A minimal entry:

```
keys = 'I,K';
executeCommand('/gamemode creative');
```

Available actions: `executeCommand(cmd)` (a leading `/` runs it as a command, otherwise it is sent as
chat), `copy(text)`, `sendInfo(text)`, `suggestCommand(cmd)`.

## Building

The build needs a **JDK 25** toolchain (Minecraft 26.x). AdvancedChatMacros depends on the refurbished
AdvancedChatCore (and, optionally, AdvancedChatFilters), which it resolves from your local Maven
repository. Publish those locally first:

```
# in the AdvancedChatCore clone
./gradlew publishToMavenLocal      # publishes io.github.darkkronicle:AdvancedChatCore:1.6.2

# in the AdvancedChatFilters clone
./gradlew publishToMavenLocal      # publishes io.github.darkkronicle:AdvancedChatFilters:1.3.0

# then in AdvancedChatMacros
./gradlew build
```

To run the mod, install it together with AdvancedChatCore, MaLiLib and Fabric API (plus
AdvancedChatFilters if you want the filter integration).

## Credits n' more

- Code & Mastermind: DarkKronicle
- 26.2 port & modernisation: community fork
