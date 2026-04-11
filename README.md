# AutoHibernate

Automatically puts your Minecraft server to sleep when no players are
online — zero CPU usage, instant resume.

## ✨ Key Features

- **Zero Idle Resource Usage** — Server CPU drops to 0% (or near 0%)
  when nobody is connected.
- **Preserves World State** — Block updates, entity movement, crop
  growth, and random ticks are completely frozen while hibernating.
- **Freezes Local Difficulty** — In-game time stops ticking, preventing
  [regional difficulty][] from increasing while nobody is playing.
- **Instant Wake-Up** — The server resumes the moment a player connects,
  with no noticeable lag or delay.
- **Console Support** — Commands entered into the server console continue
  to work while the server is hibernating.

## 🎮 Commands

You can manually control the hibernation behavior using the
`/autohibernate` command:

| Command                       | Description                                                                                                  |
|:------------------------------|:-------------------------------------------------------------------------------------------------------------|
| `/autohibernate`              | Shows the current status (auto-pause on/off, server awake/sleeping).                                         |
| `/autohibernate autopause true`  | Enables automatic hibernation when the server is empty.                                                   |
| `/autohibernate autopause false` | Disables automatic hibernation.                                                                           |
| `/autohibernate sleep`        | Instantly forces the server to hibernate (until a player joins or it's manually woken).                       |
| `/autohibernate wake`         | Instantly wakes the server from hibernation.                                                                 |

## ❓ FAQ

**Can I use this on a newer version of Minecraft?**
Try the latest release first! AutoHibernate often works perfectly fine
even if Modrinth doesn't explicitly list the newest Minecraft snapshot or
minor version as supported.

**Can I use this on an older version of Minecraft?**
Probably not, as this would likely require porting to a completely
different mod loader.

**Can you port this to Forge/NeoForge/Paper?**
AutoHibernate may be ported to other mod loaders in the future if time
permits. For now, it remains a Fabric mod.

## 📜 Credits

AutoHibernate is inspired by the concept of pausing idle Minecraft
servers. It is an original, ground-up implementation written under the
MIT license.

[regional difficulty]: https://minecraft.wiki/w/Difficulty#Regional_difficulty