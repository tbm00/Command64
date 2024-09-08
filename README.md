# Command64
A spigot plugin that runs commands with configurable triggers.

Created by tbm00 for play.mc64.wtf.

## Features
- **JoinCommandEntries** run command(s) as the console when a player joins (if player has associated permission node).
- **CustomCommandEntries** run command(s) as the console when a player/console uses a custom command (if player has associated permission node).
- **TimerCommandEntries** run command(s) as the console after a delay, initially triggered by a player/console using a custom timer command (if player has associated permission node). + the ability to check if player has space in inventory before running command(s).
- **ItemCommandEntries** run command(s) as the console when a player uses a custom item (if player has associated permission node).

## Dependencies
- **Java 17+**: REQUIRED
- **Spigot 1.18.1+**: UNTESTED ON OLDER VERSIONS

## Commands & Permissions
#### Commands
- `/cmd help` Display this command list
- `/cmd give <itemKey> [player]` Spawn a custom item
- `/cmd timer <tickDelay> <timerCommand> [argument]` Run delayed command as Console w/ optional argument
- `/cmd <customCommand> [argument]` Run custom command as Console w/ optional argument
#### Permissions
Each JoinCommandEntry, CustomCommandEntry, TimerCommandEntry, and ItemCommandEntry has configurable permission nodes (in `config.yml`) that must be fulfiled for a player to use the respective feature. The only hardcoded permission node is command64.help.
- `command64.help` Ability to display the command list *(Default: OP)*

## Config
```
# Command64 v0.2.4-beta by @tbm00

# By default, everything is disabled.
# You should configure each module to your own liking.
# ...
# The predefined config is an example to give you and idea of what
# you can do when using this plugin with other plugins,like EssentialsX
# LuckPerms, PlayerParticles, SpecializedCrates, and VotingPlugin.

# joinCommandEntries get ran by the console when a player 
# (with the correct permission) connects to the server.
# <player> = player who joined
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

# customCommandEntries get ran by the console when the console, or
# a player (with the correct permission), uses the associated custom command.
# <sender> = player who used the command
# <argument> = string included as command's argument
customCommandEntries:
  enabled: false
  '1': # Usage: "/cmd save"
    enabled: false
    usePerm: "group.admin"
    usePermValue: true
    customCommand: "save"
    consoleCommands:
      - "say <sender> is saving the server!"
      - "save-all"
  '2': # Usage: "/cmd stop"
    enabled: false
    usePerm: "group.admin"
    usePermValue: true
    customCommand: "stop"
    consoleCommands:
      - "say <sender> is stopping the server!"
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
# (with the correct permission), using the associated timer command.
# <sender> = player who used the command
# <argument> = string included as command's argument
# invCheck.checkIfSpaceBeforeRun should be true or false.
# invCheck.checkPlayer should be "ARGUMENT", "SENDER", or "" for disabled.
# invCheck.ifNoSpaceConsoleCommands should be a list of strings, or "" for disabled.
timerCommandEntries:
  enabled: false
  '1': # Usage: "/cmd timer <tickDelay> voteparty"
    enabled: false
    usePerm: "group.supermod"
    usePermValue: true
    timerCommand: "voteparty"
    consoleCommands:
      - "av voteparty force"
    invCheck:
      checkIfSpaceBeforeRun: false
      checkPlayer: ""
      ifNoSpaceConsoleCommands: []
  '2': # Usage: "/cmd timer <tickDelay> unban <argument>" i.e. "/cmd timer 12000 unban Notch"
    enabled: false
    usePerm: "group.supermod"
    usePermValue: true
    timerCommand: "unban"
    consoleCommands:
      - "unban <argument>"
    invCheck:
      checkIfSpaceBeforeRun: false
      checkPlayer: ""
      ifNoSpaceConsoleCommands: []
  '3': # Usage: "/cmd timer <tickDelay> givekey <argument>" i.e. "/cmd timer 1 givekey Notch"
    enabled: false
    usePerm: "group.supermod"
    usePermValue: true
    timerCommand: "givekey"
    consoleCommands:
      - "scrates forceopen Crate32 <argument>"
    invCheck:
      checkIfSpaceBeforeRun: true
      ifNoSpaceConsoleCommands:
        - "msg <argument> &4You do not have enough space in your inventory... &cYou have two minutes to make room for your reward!"
        - "cmd timer 2400 givekey-try2 <argument>"
  '4': # Usage: "/cmd timer <tickDelay> givekey-try2 <argument>" i.e. "/cmd timer 2400 givekey-try2 Notch"
    enabled: false
    usePerm: "group.admin"
    usePermValue: true
    timerCommand: "givekey-try2"
    consoleCommands:
      - "scrates forceopen Crate32 <argument>"
      - "msg <argument> &2Force opening Crate32..! &eHope you have space :)"
    invCheck:
      checkIfSpaceBeforeRun: false
      checkPlayer: ""
      ifNoSpaceConsoleCommands: []
  # Add more entries as needed

# itemCommandEntries get ran by the console when a player
# (with the correct permission) uses a custom item.
# <player> = player who used the item
itemCommandEntries:
  enabled: false
  '1':
    enabled: false
    givePerm: "command64.give.spawncompass"
    givePermValue: true
    usePerm: "essentials.spawn"
    usePermValue: true
    consoleCommands:
      - "sudo <sender> spawn"
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
      - "sudo <sender> break"
    key: "STAFFPICK"
    name: "&0Staffpickaxe"
    item: "GOLDEN_PICKAXE"
    glowing: true
    lore:
      - "&4Be careful!"
  # Add more entries as needed
```
