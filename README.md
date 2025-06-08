# TwistModeration

A professional moderation plugin for Paper Minecraft servers with automated chat filtering and comprehensive punishment system.

## Features

### 🛡️ Automated Chat Filtering
- Real-time inappropriate content detection
- Configurable word filters and patterns
- Automatic spam and caps detection
- Customizable actions (warn, mute, kick, ban)
- Staff notifications for filter triggers

### ⚖️ Comprehensive Punishment System
- **Mute/Tempmute** - Silence disruptive players
- **Warn** - Issue warnings with automatic escalation
- **Kick** - Remove players temporarily
- **Tempban** - Temporary server bans
- **History** - Complete punishment tracking

### 🤖 Smart Auto-Punishments
- Auto-mute after X warnings
- Auto-kick after X warnings  
- Auto-ban after X kicks
- Fully configurable thresholds

### 💾 Database Integration
- SQLite database for punishment storage
- Complete punishment history tracking
- Persistent mute/ban states
- Automatic cleanup of expired punishments

## Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/mute <player> [reason]` | `twistmoderation.mute` | Permanently mute a player |
| `/tempmute <player> <duration> [reason]` | `twistmoderation.tempmute` | Temporarily mute a player |
| `/unmute <player>` | `twistmoderation.unmute` | Unmute a player |
| `/warn <player> <reason>` | `twistmoderation.warn` | Warn a player |
| `/kick <player> [reason]` | `twistmoderation.kick` | Kick a player |
| `/tempban <player> <duration> [reason]` | `twistmoderation.tempban` | Temporarily ban a player |
| `/unban <player>` | `twistmoderation.unban` | Unban a player |
| `/clearchat [lines]` | `twistmoderation.clearchat` | Clear chat messages |
| `/history <player>` | `twistmoderation.history` | View punishment history |
| `/twistmoderation reload` | `twistmoderation.admin` | Reload configuration |

### Duration Examples
- `5m` - 5 minutes
- `1h` - 1 hour  
- `2d` - 2 days
- `1w` - 1 week

## Permissions

- `twistmoderation.*` - All permissions
- `twistmoderation.bypass` - Bypass chat filter and mute restrictions
- `twistmoderation.admin` - Administrative access

## Installation

1. Download the latest release
2. Place `TwistModeration.jar` in your `plugins` folder
3. Restart your server
4. Configure the plugin in `plugins/TwistModeration/config.yml`
5. Reload with `/twistmoderation reload`

## Configuration

The plugin comes with a comprehensive configuration file allowing you to customize:

- Chat filter sensitivity and actions
- Auto-punishment thresholds
- Message formats and colors
- Staff notification settings
- Database configuration

## Requirements

- Paper 1.20+ (or compatible fork)
- Java 17+

## Support

For support, feature requests, or bug reports, please visit our GitHub repository.

---

**TwistModeration** - Professional moderation made simple.