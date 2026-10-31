# AutoHibernate
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/autohibernate?logo=modrinth&color=00AF5C&label=downloads)](https://modrinth.com/mod/autohibernate)
[![Fabric](https://img.shields.io/badge/loader-fabric-blue?logo=fabric)](https://fabricmc.net/)

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

# Download
You can download the mod on [Modrinth](https://modrinth.com/mod/autohibernate).

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

# Development
> [!WARNING]
> This section is meant for developers who want to contribute to the mod or build it locally.
> 
> If you just want to use the mod, you can ignore this and [download the mod](https://modrinth.com/mod/autohibernate) instead.

> [!NOTE]
> You need to have [Git](https://git-scm.com/) & [JDK 21](https://adoptium.net/temurin/releases?version=21&os=any&arch=any) installed on your computer.

To build the mod locally, you have to:
1. Clone the repository and navigate to it
   ```
   git clone https://github.com/DerLuuzDev/AutoHibernate.git; cd AutoHibernate
   ``` 
2. Build the mod using Gradle
    **Linux**
    ```
    ./gradlew build
    ```
    **Windows**
    ```
    gradlew.bat build
    ```
3. The built mod will be located in `build/libs/`

## 📜 Credits

AutoHibernate is inspired by the concept of pausing idle Minecraft
servers. It is an original, ground-up implementation written under the
MIT license.

[regional difficulty]: https://minecraft.wiki/w/Difficulty#Regional_difficulty
