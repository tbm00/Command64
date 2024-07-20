# PermCheck64
A spigot plugin that runs commands when a player joins, based on the player's permissions.

Created by tbm00 for play.mc64.wtf.

## Dependencies
- **Java 17+**: REQUIRED
- **Spigot 1.18.1+**: UNTESTED ON OLDER VERSIONS

## Config
```
enabled: true
permCommandEntries:
  '1':
    enabled: true
    perm: "essentials.tpa"
    permValue: true
    command: "say <player> has perm node essentials.tpa!"
  '2':
    enabled: true
    perm: "essentials.tpa"
    permValue: false
    command: "say <player> doesn't have perm node essentials.tpa!"
  '3':
    enabled: true
    perm: "pluginname.exampleperm"
    permValue: true
    command: "say <player> has perm node pluginname.exampleperm!"
  '4':
    enabled: true
    perm: "pluginname.exampleperm"
    permValue: false
    command: "say <player> doesn't have perm node pluginname.exampleperm!"
  # Add more entries as needed
```
