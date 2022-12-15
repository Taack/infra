package taack.ssh

import groovy.transform.CompileStatic
import taack.ssh.command.CommandContextToken
import taack.ssh.command.CommandTree

@CompileStatic
class CommandRegister {
    final Map<String, CommandTree> registry = [:]

    CommandRegister(final CommandTree... sshCommands) {
        registry.putAll(sshCommands.collectEntries {
            return new AbstractMap.SimpleImmutableEntry<String, CommandTree>(it.commandName, it)
        })
    }

    private static String commandKey(final String command) {
        if (!command) return null
        final String ltrimCommand = command.trim()
        int spacePos = ltrimCommand.indexOf(' ')
        if (spacePos == -1) {
            ltrimCommand
        } else {
            ltrimCommand.substring(0, spacePos)
        }
    }

    String help(final String command = null) {
        final ck = commandKey(command)
        if (ck && registry.entrySet().contains(ck)) {
            registry[command]
        } else {
            registry.keySet().join("\n")
        }
    }

    Iterator<CommandContextToken> parseL2R(final String command) {
        if (command) {
            final sshCommands = registry[commandKey(command)]
            if (sshCommands) return sshCommands.parseL2R(command, 0).iterator()
        }
        null
    }
}
