# Command64
A spigot plugin that runs commands with configurable triggers.

Created by tbm00 for play.mc64.wtf.

## Features
- **JoinCommandEntries** run command(s) as the console when a player joins (if player has associated permission node).
- **CustomCommandEntries** run command(s) as the console when a player/console uses a custom command (if player has associated permission node).
- **TimerCommandEntries** run command(s) as the console after a delay, initially triggered by a player/console using a custom timer command (if player has associated permission node).
- **ItemCommandEntries** run command(s) as the console when a player uses a custom item (if player has associated permission node).

## Dependencies
- **Java 17+**: REQUIRED
- **Spigot 1.18.1+**: UNTESTED ON OLDER VERSIONS

## Commands & Permissions
#### Commands
- `/cmd help` Display this command list
- `/cmd give <itemKey>` Spawn in a custom \<item\> in your inventory
- `/cmd give <itemKey> <player>` Spawn in a custom \<item\> in player's inventory
- `/cmd timer <tickDelay> <timerCommand> [argument]` Run delayed command as Console w/ optional argument
- `/cmd <customCommand> [argument]` Run custom command as Console w/ optional argument
#### Permissions
Each JoinCommandEntry, CustomCommandEntry, TimerCommandEntry, and ItemCommandEntry has configurable permission nodes (in `config.yml`) that must be fulfiled for a player to use the respective feature. The only hardcoded permission node is command64.help.
- `command64.help` Ability to display the command list *(Default: OP)*


## Config
```
# By default, everything is disabled.
# You should configure each module to your own liking.
# The predefined config is an example of what you can do
# when using this plugin with other plugins, like EssentialsX
# LuckPerms, PlayerParticles, and VotingPlugin.

# joinCommandEntries get ran by the console when a player 
# (with the assocated permission) connects to the server.
joinCommandEntries:
  enabled: false
  '1':
    enabled: false
    checkPerm: "group.particlebase"
    checkPermValue: false
    consoleCommands:
      - "pp reset <player>"
  '2':
    enabled: false
    checkPerm: "group.donor1"
    checkPermValue: true
    consoleCommands:
      - "say Welcome back to the server <player>!"
  '3':
    enabled: false
    checkPerm: "group.donor1"
    checkPermValue: false
    consoleCommands:
      - "say <player>, why haven't you donated?"
      - "say Gimme yo money!"
  # Add more entries as needed

# customCommandEntries get ran by the console when the console, or
# a player (with the associated permission), uses the associated custom command.
customCommandEntries:
  enabled: false
  '1': # Usage: "/cmd save"
    enabled: false
    usePerm: "group.admin"
    usePermValue: true
    customCommand: "save"
    consoleCommands:
      - "say <player> is saving the server!"
      - "save-all"
  '2': # Usage: "/cmd stop"
    enabled: false
    usePerm: "group.admin"
    usePermValue: true
    customCommand: "stop"
    consoleCommands:
      - "say <player> is stopping the server!"
      - "stop"
  '3': # Usage: "/cmd promotedonor <argument>" i.e. "/cmd promotedonor Notch"
    enabled: false
    usePerm: "group.supermod"
    usePermValue: true
    customCommand: "promotedonor"
    consoleCommands:
      - "lp user <argument> promote donor"
      - "say <argument> donated to the server and was promoted by <player>!"
  # Add more entries as needed

# timerCommandEntries get ran by the console after a delay.
# Initially triggered by the console, or a player
# (with the associated permission), using the associated timer command.
timerCommandEntries:
  enabled: false
  '1': # Usage: "/cmd timer <tickDelay> voteparty"
    enabled: false
    usePerm: "group.supermod"
    usePermValue: true
    timerCommand: "voteparty"
    consoleCommands:
      - "av voteparty force"
  '2': # Usage: "/cmd timer <tickDelay> unban <argument>" i.e. "/cmd timer 12000 unban Notch"
    enabled: false
    usePerm: "group.supermod"
    usePermValue: true
    timerCommand: "unban"
    consoleCommands:
      - "unban <argument>"
  # Add more entries as needed

# itemCommandEntries get ran by the console when a player
# (with the associated permission) uses a custom item.
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
    name: "&0Staffpickaxe"
    item: "GOLDEN_PICKAXE"
    glowing: true
    lore:
      - "&4Be careful!"
  # Add more entries as needed
```
