package taack.ssh.command

import groovy.transform.CompileStatic

import java.util.regex.Matcher

@CompileStatic
class CommandTree implements ICommand {
    static class SubCommand implements ICommand {
        final String subCommandName
        final List<ICommand> commandDatumIterator
        final String comment

        SubCommand(String subCommandName, List<ICommand> commandDatumIterator, String comment) {
            this.subCommandName = subCommandName
            this.commandDatumIterator = commandDatumIterator
            this.comment = comment
        }

        @Override
        String getHelp(int indent) {
            StringBuffer help = new StringBuffer()
            help.append(' ' * indent)
            help.append("Sub Command \"${subCommandName}\": ${comment}")
            help.append("\n")
            commandDatumIterator.each {
                help.append(it.getHelp(indent + 2))
            }
            return help.toString()
        }

        @Override
        List<CommandContextToken> parseL2R(String command, int from) {
            if (command.length() < from) return null
            final int trailingWhiteSpace = countTrailingWhiteSpace(command.substring(from))
            from += trailingWhiteSpace
            if (command.substring(from).startsWith(subCommandName + " ") || command.substring(from).endsWith(subCommandName)) {
                List<CommandContextToken> res = []
                res.add(new CommandContextToken(this, null, command, from, from + subCommandName.length()))
                from += subCommandName.length()
                commandDatumIterator.each {
                    def itc = it.parseL2R(command, from)
                    if (itc) {
                        res.addAll(itc)
                        from = itc.last().endPos
                    }
                }
                return res
            }
            return null
        }
    }

    static class Arg implements ICommand {
        enum Type {
            STRING,
            NUMBER,
            DATE
        }

        Arg(Type type, boolean autoComplete, boolean optional, String argName, String comment) {
            this.type = type
            this.autoComplete = autoComplete
            this.optional = optional
            this.argName = argName
            this.comment = comment
        }

        final Type type
        final boolean autoComplete
        final boolean optional
        final String argName
        final String comment

        private String formatArgType() {
            switch (type) {
                case Type.STRING:
                    return "'String Literal'"
                case Type.NUMBER:
                    return "Number [0-9.]+"
                case Type.DATE:
                    return "'yyyy-MM-dd'"
            }
        }

        private Matcher typeMatcher(String commandFrom) {
            switch (type) {
                case Type.STRING:
                    return commandFrom =~ /('[^'.]+').*/
                case Type.NUMBER:
                    return commandFrom =~ /([0-9\.]+).*/
                case Type.DATE:
                    return commandFrom =~ /('[0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]').*/
            }
        }

        @Override
        String getHelp(int indent) {
            StringBuffer help = new StringBuffer()
            help.append(' ' * indent)
            help.append("${argName ? "--${argName}" : ''} ${formatArgType()}: ${comment} (${optional ? 'Optional' : 'Mandatory'})")
            help.append("\n")
            return help.toString()
        }

        @Override
        List<CommandContextToken> parseL2R(String command, int from) {
            if (command.length() < from) return null
            final int trailingWhiteSpace = countTrailingWhiteSpace(command.substring(from))
            from += trailingWhiteSpace
            if (argName) from += "--${argName} ".length()
            if (command.length() < from) return null
            Matcher m = typeMatcher(command.substring(from))

            if (m.matches()) {
                return [new CommandContextToken(this, m.group(1), command, from + m.start(1), from + m.end(1))]
            } else {
                return null
            }
        }
    }
    final String commandName
    final List<ICommand> cmdIterator
    final String comment

    CommandTree(String commandName, List<ICommand> argIterator, String comment) {
        this.commandName = commandName
        this.cmdIterator = argIterator
        this.comment = comment
    }

    @Override
    String getHelp(int indent) {
        StringBuffer help = new StringBuffer()
        help.append("Command \"${commandName}\": ${comment}")
        help.append("\n")
        cmdIterator.each {
            help.append(it.getHelp(2))
        }
        return help.toString()
    }

    @Override
    List<CommandContextToken> parseL2R(String command, int from) {
        List<CommandContextToken> res = []
        final int trailingWhiteSpace = countTrailingWhiteSpace(command.substring(from))
        from += trailingWhiteSpace
        if (command.substring(from).startsWith(commandName)) {
            res.add(new CommandContextToken(this, null, command, from, from + commandName.length()))
            from += commandName.length()
            cmdIterator.each {
                def itc = it.parseL2R(command, from)
                if (itc) {
                    res.addAll(itc)
                    from = itc.last().endPos

                }
            }
            return res
        } else null
    }

    private static int countTrailingWhiteSpace(String s) {
        final int sLen = s.length()
        if (sLen > 0) {
            int counter = 0
            char sc = s.charAt(counter)
            final char blank = ' '.charAt(0)
            while (sc == blank && counter < sLen - 1) {
                counter ++
                sc = s.charAt(counter)
            }
            counter
        } else 0
    }
}
