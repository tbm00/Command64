# Command64
A spigot plugin that runs commands with configurable triggers.

Created by tbm00 for play.mc64.wtf.

## Dependencies
- **Java 17+**: REQUIRED
- **Spigot 1.18.1+**: UNTESTED ON OLDER VERSIONS

## Config
```
# By default, everything is disabled.
# You should configure each module to your own liking.
# The predefined config is an example of what you can do
# when using this plugin with other plugins, like EssentialsX
# LuckPerms, and PlayerParticles.


# joinCommandEntries get ran by the console when a player 
# (with the correct permission) connects to the server.
joinCommandEntries:
  enabled: false
  '1':
    enabled: false
    checkPerm: "group.particlebase"
    checkPermValue: false
    commands:
      - "pp reset <player>"
  '2':
    enabled: false
    checkPerm: "group.donor1"
    checkPermValue: true
    commands: 
      - "say Welcome back to the server <player>!"
  '3':
    enabled: false
    checkPerm: "group.donor1"
    checkPermValue: false
    commands: 
      - "say <player>, why haven't you donated?"
      - "say Gimme yo money!"
  # Add more entries as needed


# customCommandEntries get ran by the console when a player 
# (with the correct permission) uses the custom command.
customCommandEntries:
  enabled: false
  '1': # Usage: "/cmd stop"
    enabled: false
    usePerm: "group.admin"
    usePermValue: true
    customCommand: "stop"
    consoleCommands:
      - "say <player> is stopping the server!"
      - "stop"
  '2': # Usage: "/cmd promotedonor <argument>" i.e. "/cmd promotedonor Notch"
    enabled: false
    usePerm: "group.supermod"
    usePermValue: true
    customCommand: "promotedonor"
    consoleCommands:
      - "lp user <argument> promote donor"
      - "say <argument> donated to the server and was promoted by <player>!"
  # Add more entries as needed


# itemCommandEntries get ran by the console when a player
# (with the correct permission) uses a custom item.
itemCommandEntries:
  enabled: false
  '1':
    enabled: false
    givePerm: "commad64.give.spawncompass"
    givePermValue: true
    usePerm: "essentials.spawn"
    usePermValue: true
    commands:
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
    commands:
      - "sudo <player> break"
    key: "STAFFPICK"
    name: "&8Staffpickaxe"
    item: "GOLDEN_PICKAXE"
    glowing: true
    lore: []
  # Add more entries as needed
```
