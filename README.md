# Command64 <img align="left" src="icon.png" alt="Item64 Icon" width="40"/>
A spigot plugin that runs commands with configurable triggers.

Created by tbm00 for play.mc64.wtf.

## Features
- **CustomCommandEntries** run command(s) as the console when a player/console uses a custom command, with optional delays and inventory checks.
- **JoinCommandEntries** run command(s) as the console when a player joins.
- **ItemCommandEntries** run command(s) as the console when a player uses a custom item.

## Dependencies
- **Java 17+**: REQUIRED
- **Spigot 1.18.1+**: UNTESTED ON OLDER VERSIONS

## Commands & Permissions
#### Commands
- `/cmd help` Display this command list
- `/cmd give <itemKey> [player]` Spawn a custom item
- `/cmd -d <tickDelay> <customCommand> [argument]` Run delayed custom command as Console w/ optional argument
- `/cmd <customCommand> [argument]` Run custom command as Console w/ optional argument
#### Permissions
Each JoinCommandEntry, CustomCommandEntry, and ItemCommandEntry has configurable permission nodes (in `config.yml`) that must be fulfiled for a player to use the respective feature. The only hardcoded permission node is command64.help.
- `command64.help` Ability to display the command list *(Default: OP)*

## Default Config
```
# Command64 v1.0.1 by @tbm00
# https://github.com/tbm00/Command64/

# By default, everything is disabled.
# You should configure each module to your own liking.
# The predefined config is an example to give you and idea of what
# you can do when using this plugin with other plugins, like EssentialsX
# LuckPerms, PlayerParticles, SpecializedCrates, and MythicMobs.

# -------------------------------------------------------------------------------------- #
# joinCommandEntries get ran by the console when a player 
# (with the correct permission) connects to the server.
# ---------
# <player> == player who joined
# -------------------------------------------------------------------------------------- #
joinCommandEntries:
  enabled: false
  '1':
    enabled: false
    checkPerm: "group.particlebase"
    checkPermValue: false
    tickDelay: 10
    consoleCommands:
      - "pp reset <player>"
  '2':
    enabled: false
    checkPerm: "group.donor1"
    checkPermValue: true
    tickDelay: 10
    consoleCommands:
      - "say Welcome back to the server <player>!"
  '3':
    enabled: false
    checkPerm: "group.donor1"
    checkPermValue: false
    tickDelay: 10
    consoleCommands:
      - "say <player>, why haven't you donated?"
      - "say Gimme yo money!"
  # Add more entries as needed


# -------------------------------------------------------------------------------------- #
# customCommandEntries get ran by the console when the console, or
# a player (with the correct permission), uses the associated custom subcommand.
# ---------
# <player> == player who used the command
# <argument> == string included as running command's argument
# ---------
# If wanted, you can add an inventory check to any custom command entry.
# If so, invCheck.checkOnPlayer should be "ARGUMENT" or "SENDER".
# ---------
# If wanted, you can add "-d <X>" when running any custom command to delay the console
# commands for X ticks.
# -------------------------------------------------------------------------------------- #
customCommandEntries:
  enabled: false
  '1': # Usage: "/cmd save"
    enabled: false
    usePerm: "command64.admin"
    usePermValue: true
    customCommand: "save"
    consoleCommands:
      - "say <player> is saving the server!"
      - "save-all"
  '2': # Usage: "/cmd stop"
    enabled: false
    usePerm: "command64.admin"
    usePermValue: true
    customCommand: "stop"
    consoleCommands:
      - "say <player> is stopping the server!"
      - "stop"
  '3': # Usage: "/cmd promotedonor <argument>" i.e. "/cmd promotedonor Notch"
    enabled: false
    usePerm: "command64.supermod"
    usePermValue: true
    customCommand: "promotedonor"
    consoleCommands:
      - "lp user <argument> promote donor"
      - "say <argument> donated to the server and was promoted by <player>!"
  '4': # Usage "/cmd boss-fight-start"
    enabled: false
    usePerm: "command64.supermod"
    usePermValue: true
    customCommand: "boss-fight-start"
    consoleCommands:
      - "mm mobs spawn BossMinion -t world,-677,46,727"
      - "mm mobs spawn BossMinion -t world,-667,46,726"
      - "mm mobs spawn BossMinion -t world,-677,52,727"
      - "mm mobs spawn BossMinion -t world,-667,52,726"
      - "mm mobs spawn BossMinion -t world,-672,52,732"
      - "mm mobs spawn BossMinion -t Tadow,-673,52,722"
      - "mm mobs spawn BossMob -t world,-672,36,727"
      - "broadcast &bBoss fight started!"
      - "cmd -d 1200 boss-fight-round2" # 1 minute delay
  '5': # Intended Usage "/cmd -d <tickDelay> boss-fight-round2" i.e. "/cmd -d 1200 boss-fight-round2"
    enabled: false
    usePerm: "command64.supermod"
    usePermValue: true
    timerCommand: "boss-fight-round2"
    consoleCommands:
      - "mm mobs spawn BossMinion -t Tadow,-677,36,727"
      - "mm mobs spawn BossMinion -t Tadow,-667,36,726"
      - "mm mobs spawn BossMinion -t Tadow,-672,46,732"
      - "mm mobs spawn BossMinion -t Tadow,-673,46,722"
      - "mm mobs spawn BossMinion -t Tadow,-677,52,727"
      - "mm mobs spawn BossMinion -t Tadow,-667,52,726"
  '6': # Usage: "/cmd givekey <argument>" i.e. "/cmd givekey Notch"
    enabled: false
    usePerm: "command64.supermod"
    usePermValue: true
    customCommand: "givekey"
    consoleCommands:
      - "crates forceopen Crate32 <argument>"
    invCheck:
      checkIfSpaceBeforeRun: true
      checkOnPlayer: "ARGUMENT"
      ifNoSpaceConsoleCommands:
        - "msg <argument> &4You do not have enough space in your inventory... &cYou have two minutes to make room for your reward!"
        - "cmd -d 2400 givekey-try2 <argument>" # 2 minute delay
  '7': # Intended Usage: "/cmd -d <tickDelay> givekey-try2 <argument>" i.e. "/cmd -d 2400 givekey-try2 Notch"
    enabled: false
    usePerm: "command64.admin"
    usePermValue: true
    customCommand: "givekey-try2"
    consoleCommands:
      - "crates forceopen Crate32 <argument>"
      - "msg <argument> &2Force opening Crate32..! &eHope you have space :)"
  # Add more entries as needed


# -------------------------------------------------------------------------------------- #
# itemCommandEntries get ran by the console when a player
# (with the correct permission) uses a custom item.
# ---------
# <player> == player who used the item
# <argument> == string included as running command's argument
# -------------------------------------------------------------------------------------- #
itemCommandEntries:
  enabled: false
  '1':
    enabled: false
    givePerm: "command64.give.spawncompass"
    givePermValue: true
    usePerm: "essentials.spawn"
    usePermValue: true
    consoleCommands:
      - "sudo <player> spawn"
    key: "SPAWNCOMPASS"
    name: "&dSpawn Teleporter"
    item: "COMPASS"
    glowing: true
    lore:
      - "&5Teleports you to spawn"
  '2':
    enabled: false
    givePerm: "command64.give.staffpick"
    givePermValue: true
    usePerm: "essentials.break"
    usePermValue: true
    consoleCommands:
      - "sudo <player> break"
    key: "STAFFPICK"
    name: "&0Staff Pickaxe"
    item: "GOLDEN_PICKAXE"
    glowing: true
    lore:
      - "&4Be careful!"
  # Add more entries as needed
```