# PermCheck64
A spigot plugin that runs commands when a player joins, based on the player's permissions.

Created by tbm00 for play.mc64.wtf.

## Dependencies
- **Java 17+**: REQUIRED
- **Spigot 1.18.1+**: UNTESTED ON OLDER VERSIONS

## Commands
#### Admin Commands
- `/permcheck reload` Reload the plugin's config

## Permissions
#### Admin Permissions
- `permcheck64.reload` Ability to use reload the config

## Config
```
enabled: true
prefix: "&8[&fPermCheck&8]&r "
'1':
  perm: "pluginname.exampleperm1"
  perm-value: true
  command: "say <player> has permission pluginname.exampleperm1!"
'2':
  perm: "pluginname.exampleperm2"
  perm-value: false
  command: "say <player> doesn't have permission pluginname.exampleperm2!"
# Add more entries as needed
```
