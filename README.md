# Command64 <img align="left" src="icon.png" alt="Item64 Icon" width="40"/>
A spigot plugin that runs commands with configurable triggers.

Created by tbm00 for play.mc64.wtf.

## Features
- **CustomCommandEntries** run command(s) as the console when a player/console uses a custom command, with optional delays and inventory checks.
- **JoinCommandEntries** run command(s) as the console when a player joins.
- **ItemCommandEntries** run command(s) as the console when a player uses a custom item.
- **RewardSystem** give players rewards that can be triggered when they're ready and have inventory space.

## Dependencies
- **Java 17+**: REQUIRED
- **Spigot 1.18.1+**: UNTESTED ON OLDER VERSIONS

## Commands & Permissions
#### Commands
- `/cmd help` Display this command list
- `/cmd give <itemKey> [player]` Spawn a custom item
- `/cmd <customCommand> [argument]` Run custom command as Console w/ optional argument
- `/cmd -d <tickDelay> <customCommand> [argument]` Run delayed custom command as Console w/ optional argument
- `/cmd queue <rewardName> <player>` Add reward to a player's reward queue
#### Permissions
Each JoinCommandEntry, CustomCommandEntry, and ItemCommandEntry has configurable permission nodes (in `config.yml`) that must be fulfiled for a player to use the respective feature. The only hardcoded permission nodes are:
- `command64.help` Ability to display the command list *(Default: OP)*
- `command64.enqueuerewards` Ability add rewards to a player's queue *(Default: OP)*
- `command64.redeemrewards` Ability redeem queued rewards *(Default: everyone)*

## Default Config
```
# Command64 v1.1.0-beta by @tbm00
# https://github.com/tbm00/Command64/

# By default, everything is disabled.
# You should configure each module to your own liking.
# The predefined config is an example to give you and idea of what
# you can do when using this plugin with other plugins, like EssentialsX
# LuckPerms, PlayerParticles, SpecializedCrates, and MythicMobs.


# -------------------------------------------------------------------------------------- #
# This module gives players the ability redeem pending rewards (commands) by using `/redeemreward`.
# -- If the player has no pending rewards, they will be sent noPendingRewardMessage.
# -- Else if the the player has inventory space, the first rewardEntry's consoleCommands 
#      (in the player's pending queue) will be run.
# -- Else the first rewardEntry's consoleCommands that doesn't have a invCheck will be run.
# -- If there is no pending reward without a invCheck, they will be sent noInvSpaceMessage.
# ---------
# 1st) Admins/Console add rewardCommandEntries to a player's pending queue
#       by using `/cmd queue <rewardName> <username>`.
# 2nd) Players redeem rewards (in-order, unless there are skips due to full invs) by using `/redeemreward`.
# ---------
# Currently, there is no database saving players' pending reward queues.
# So, pending rewards do not persist after restarting/reloading the server/plugin!
# ---------
# <player> == player who joined
# -------------------------------------------------------------------------------------- #
rewardSystem:
  enabled: false
  noRewardMessage: "&cYou don't have any pending rewards!"
  noInvSpaceMessage: "&cYou don't have enough inventory space for your reward!"
  rewardedMessage: "&aYou redeemed a reward!"
  rewardEntries:
    '1': # Usage: `/cmd queue forcecrateopen <player>`
      name: "forcecrateopen"
      consoleCommands:
        - "crates forceopen Crate32 <player>"
      invCheck: true
    # Add more entries as needed


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