package taack.ssh.command

interface ICommand {
    String getHelp(int indent)
    List<CommandContextToken> parseL2R(String command, int from)
//    Iterator<ICommandDatum> autoCompleteL2R(String command, int from)
}