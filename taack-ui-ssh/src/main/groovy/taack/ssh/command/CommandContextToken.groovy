package taack.ssh.command

class CommandContextToken {
    final ICommand commandDatum
    final Object context
    final String command
    final int startPos
    final int endPos

    CommandContextToken(ICommand commandDatum, Object context, String command, int startPos, int endPos) {
        this.commandDatum = commandDatum
        this.context = context
        this.command = command
        this.startPos = startPos
        this.endPos = endPos
    }


    @Override
    String toString() {
        return 'CommandContextDatum{' + \
                'commandDatum=' + commandDatum + \
                ', context=' + context + \
                ", command='" + command + '\'' + \
                ', startPos=' + startPos + \
                ', endPos=' + endPos + \
                '}'
    }
}
