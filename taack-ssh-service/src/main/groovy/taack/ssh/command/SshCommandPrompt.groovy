package taack.ssh.command

import groovy.transform.CompileStatic
import org.apache.sshd.common.channel.ChannelOutputStream
import org.apache.sshd.server.Environment
import org.apache.sshd.server.ExitCallback
import org.apache.sshd.server.channel.ChannelSession
import org.apache.sshd.server.command.Command
import org.apache.sshd.server.session.ServerSession
import org.apache.sshd.server.session.ServerSessionAware
import taack.ssh.CommandRegister
import taack.ssh.SshEventRegistry

import java.util.logging.Logger

@CompileStatic
class SshCommandPrompt implements Command, Runnable, ServerSessionAware {

    private static final Logger log = Logger.getLogger(SshCommandPrompt.class.name)

    private InputStream inputStream
    private ChannelOutputStream out, err
    private ExitCallback callback
    private ServerSession session
    private List<String> history = []
    private int historyPosition = 0
    private boolean insertMode = true

    // Terminal Codes VT100
    // https://en.wikipedia.org/wiki/ANSI_escape_code
    // https://wiki.bash-hackers.org/scripting/terminalcodes
    // https://stackoverflow.com/questions/37557309/how-to-set-cursor-color-in-bash-shell
    // https://unix.stackexchange.com/questions/55423/how-to-change-cursor-shape-color-and-blinkrate-of-linux-console
    private final char escape = 27
    private final String cursorForegroundYellow = "12"
    private final String cursorForegroundDarkBlue = "10"
    private final String styleReset = "$escape[0m"
    private final String redForeground = '31'
    private final String greenForeground = '32'
    private final String blueForeground = '34'
    private final String bold = '1'
    private final String italic = '3'
    private final String underline = '4'
    private final String styleSoftwareCursorYellow = "$escape[?16;$cursorForegroundYellow;32;c"
    private final String styleSoftwareCursorDarkBlue = "$escape[?16;$cursorForegroundDarkBlue;32;c"
    private final String styleRedBold = "$escape[${bold};${redForeground}m"
    private final String styleGreenBold = "$escape[${bold};${greenForeground}m"
    private final String styleGreenItalicUnderline = "$escape[${italic};${underline};${greenForeground}m"
    private final String styleBlueItalicUnderline = "$escape[${italic};${bold};${underline};${blueForeground}m"
    private final char attrBold = 1
    private final char attrReset = 0
    private final char endLine = 13
    private final char ctrlD = 4
    private final char backSpace = 127
    private final char tab = 9
    private final String upArrow = '[A'
    private final String downArrow = '[B'
    private final String rightArrow = '[C'
    private final String leftArrow = '[D'
    private final String clearFromCursorToEol = '[0J'
    private final String clearTheScreen = '[2J'
    private final String insertKey = '[2'
    private final String hideCursor = '[?25l'
    private final String showCursor = '[?25h'
    private final String insertModeSelected = "[4h"
    private final String replacementModeSelected = "[4l"
    private final String clearCurrentLine = "[k"

    private final String taack52 = """
Et
$styleGreenBold
 /_/_/_/_/_/                                 /_/
    /_/     _/_/_/_/   _/_/_/_/    _/_/_/   /_/ /_/
   /_/    /_/   /_/  /_/   /_/  /_/        /_/_/
  /_/    /_/   /_/  /_/   /_/  /_/        /_/ /_/
 /_/      /_/_/_/    /_/_/_/    /_/_/_/  /_/   /_/
$styleReset $styleGreenItalicUnderline
                                Intranet Builder
$styleReset
"""
    private final String taack41 = """
Et
$styleGreenBold
_/_/_/_/_/                          _/
   _/    _/_/_/   _/_/_/   _/_/_/  _/ _/
  _/   _/   _/  _/   _/  _/       _/_/
 _/   _/   _/  _/   _/  _/       _/ _/
_/    _/_/_/   _/_/_/   _/_/_/  _/   _/
$styleReset $styleBlueItalicUnderline
                     Intranet Builder
$styleReset
"""

    private final String taack34 = """
Et
$styleGreenBold
_/_/_/_/_/                   _/
   _/  _/_/_/  _/_/_/  _/_/ _/ _/
  _/ _/   _/ _/   _/ _/    _/_/
 _/ _/   _/ _/   _/ _/    _/ _/
_/  _/_/_/  _/_/_/  _/_/ _/   _/
$styleReset $styleBlueItalicUnderline
               Intranet Builder
$styleReset
"""

    private void setCursorStyle() {
        if (insertMode)
            out << styleSoftwareCursorYellow
        else
            out << styleSoftwareCursorDarkBlue
    }

    private String getPrompt() {
        "Hello${styleGreenBold} ${session.username} ${styleReset}\$ "
    }

    private void refreshLine() {
        out.write((byte) endLine)
        out << "$escape$clearCurrentLine"
    }

    private void refreshPrompt() {
        refreshLine()
        out << prompt
    }

    private void newPrompt() {
        out << "\n"
        refreshPrompt()
    }

    private void commandEnterExecution() {
        out << "\n"
        refreshLine()
    }

    private String readCommand(InputStream inputStream) {
        char pc = ' '
        StringBuffer sb = new StringBuffer(128)
        int cursorPosition = 0
        boolean blockLeftMovements = false
        String lastBuffer = null
        while (pc != endLine && pc != ctrlD) {
            char c = inputStream.read()
            if (c != endLine && c != backSpace && c != escape) {
//                out << c
            } else if (c == backSpace || c == escape) {
                // More difficult to manage than what it sounds
                // TODO: implement Tabulation
                if (c == backSpace) {
                    if (cursorPosition >= 0) {
                        if (!blockLeftMovements) {
                            out << "$escape$leftArrow"
                            out << "$escape$clearFromCursorToEol"
                        }
                        if (cursorPosition > 0) {
                            blockLeftMovements = false
                            cursorPosition--
                            sb = new StringBuffer(sb.substring(0, cursorPosition))
                        } else if (cursorPosition == 0) {
                            blockLeftMovements = true
                            sb = new StringBuffer(128)
                        }
                    }
                } else if (c == escape) {
                    char c1 = inputStream.read()
                    char c2 = inputStream.read()
                    final String escCmd = "$c1$c2"
                    if (leftArrow == escCmd) {
                        if (cursorPosition >= 0) {
                            if (!blockLeftMovements) out << "$escape$leftArrow"
                            if (cursorPosition > 0) {
                                blockLeftMovements = false
                                cursorPosition--
                            } else if (cursorPosition == 0) {
                                blockLeftMovements = true
                            }
                        }
                    } else if (rightArrow == escCmd) {
                        if (cursorPosition < sb.length()) {
                            out << "$escape$rightArrow"
                            cursorPosition++
                        }
                    } else if (upArrow == escCmd) {
                        if (history.size() > 0 && historyPosition >= 0) {
                            if (!lastBuffer && !sb.toString().empty) lastBuffer = sb.toString()
                            sb = new StringBuffer(history[historyPosition == 0 ? 0 : --historyPosition])
                            refreshPrompt()
                            out << sb?.toString()?.toCharArray()
                            out << "$escape$clearFromCursorToEol"
                            cursorPosition = sb.length()
                        }
                    } else if (downArrow == escCmd) {
                        if (historyPosition <= history.size() - 1 && lastBuffer) {
                            if (historyPosition == history.size() - 1) {
                                if (lastBuffer) sb = new StringBuffer(lastBuffer)
                            } else sb = new StringBuffer(history[++historyPosition])
                            refreshPrompt()
                            out << sb?.toString()
                            cursorPosition = sb.length()
                        }
                    } else if (insertKey == escCmd) {
                        char c3 = inputStream.read()
                        insertMode = !insertMode
                        if (insertMode)
                            out << "$escape$insertModeSelected"
                        else
                            out << "$escape$replacementModeSelected"
                    }
                }
            }
            if (!(c == backSpace || c == escape)) {
                try {
                    if (c != endLine) {
                        if (sb.length() == 0 || cursorPosition >= sb.length()) {
                            cursorPosition++
                            sb.append(c)
                            out << c
                        } else {
                            if (insertMode) {
                                String toOutput
                                if (cursorPosition > 0) {
                                    toOutput = sb.insert(cursorPosition, c).substring(cursorPosition)
                                } else {
                                    toOutput = sb.insert(0, c)
                                }
                                toOutput.eachWithIndex { it, int index ->
                                    out << it
                                    if (index != 0) out << "${escape}${leftArrow}"
                                }
                            } else {
                                String toOutput = sb.replace(cursorPosition, cursorPosition + 1, c.toString()).substring(cursorPosition)
                                toOutput.eachWithIndex { it, int index ->
                                    out << it
                                    if (index != 0) out << "${escape}${leftArrow}"
                                }
                            }
                            cursorPosition++
                        }
                    }
                    pc = c
                } catch (e) {
                    log.info "ERR(${session.username}) im: $insertMode, cp: $cursorPosition, c: $c, sb: $sb => $e"
                }
            }
        }
        commandEnterExecution()
        sb.toString().trim()
    }

    int queryTermCols() {
        out << "${escape}[s"
        out << "$escape[999;999H"
        out << "$escape[6n"

        StringBuffer response = new StringBuffer()
        char c = inputStream.read()
        while (c != (char) 'R') {
            c = inputStream.read()
            response.append(c)
        }
        out << "$escape[u"
        def cols = response.toString() =~ /\[[0-9]+;([0-9]+)R/
        if (cols.matches()) cols.group(1).toInteger()
        else 30
    }

    void taackLogo() {
        int cols = queryTermCols()
        String toDraw
        if (cols >= 52) toDraw = taack52
        else if (cols > 40) toDraw = taack41
        else if (cols >= 34) toDraw = taack34
        else toDraw = "Taack | Intranet Builder"
        toDraw.lines().each {
            out << it
            refreshLine()
            out << "\n"
        }
    }

    @Override
    void run() {
        log.info "SshCommandPrompt::run ${out.class}"
        boolean done = false
        taackLogo()
        while (!done) {
            try {
                newPrompt()
                String cmd = readCommand(inputStream)
                if (cmd == "exit" || cmd.contains(new String(ctrlD))) {
                    done = true
                } else if (cmd == "history") {
                    out << "history:\n"
                    history.each {
                        refreshLine()
                        out << "${it}\n"
                    }
                } else if (cmd == "clear") {
                    out << "$escape$clearTheScreen"
                    taackLogo()
                } else {
                    boolean printHelp = cmd.empty
                    if (!printHelp)
                        try {
                            String output = SshEventRegistry.Command.processCommandForUser(session.username, cmd, inputStream, out)
                            if (output) out << output
                            else printHelp = true
                        } catch (e) {
                            e.printStackTrace()
                            printHelp = true
                        }
                    if (printHelp) {
                        if (cmd && cmd != "h" && cmd != "help") {
                            out << "Command not found: ${styleRedBold}$cmd${styleReset}\n"
                            refreshLine()
                        }
                        out << "TaackShell usage:\n"
                        refreshLine()
                        out << "\t<TAB>\t${styleGreenBold}Autocompletion when available${styleReset}\n"
                        refreshLine()
                        out << "\t<UP_ARROW>\t${styleGreenBold}Previous command in history${styleReset}\n"
                        refreshLine()
                        out << "\t<DOWN_ARROW>\t${styleGreenBold}Next command in history${styleReset}\n"
                        refreshLine()
                        out << "\t<BACKSPACE>\t${styleGreenBold}Delete the end of the line${styleReset}\n"
                        refreshLine()
                        out << "\tclear\t${styleGreenBold}Clear the screen${styleReset}\n"
                        refreshLine()
                        out << "\texit\t${styleGreenBold}Exit This Shell${styleReset}\n"
                        refreshLine()
                        out << "\tls\t${styleGreenBold}list objects and directory in this folder${styleReset}\n"
                        refreshLine()
                        out << "\thistory\t${styleGreenBold}list previous commands${styleReset}\n"
                        CommandRegister contextualCommands = SshEventRegistry.Command.contextualCommandRegisterForUser(session.username)
                        if (contextualCommands) {
                            refreshLine()
                            out << "Other commands:\n"
                            refreshLine()

                            contextualCommands.help().lines().each {
                                out << "\t"
                                out << it
                                refreshLine()
                                out << "\n"
                            }
                        }
                    }
                }
                if (!done && !cmd.empty) {
                    history.add(cmd)
                }
                historyPosition = history.size()
            } catch (Exception e) {
                callback.onExit(-1, e.getMessage())
                return
            }
        }
        callback.onExit(0)
    }

    @Override
    void setExitCallback(ExitCallback callback) {
        log.info "SshCommandPrompt::setExitCallback"
        this.callback = callback
    }

    @Override
    void setErrorStream(OutputStream err) {
        log.info "SshCommandPrompt::setErrorStream"
        this.err = err as ChannelOutputStream
    }

    @Override
    void setInputStream(InputStream inputStream) {
        log.info "SshCommandPrompt::setInputStream"
        this.inputStream = inputStream
    }

    @Override
    void setOutputStream(OutputStream out) {
        log.info "SshCommandPrompt::setOutputStream"
        this.out = out as ChannelOutputStream
    }

    @Override
    void start(ChannelSession channel, Environment env) throws IOException {
        log.info "SshCommandPrompt::start"
        new Thread(this, session.toString()).start()
    }

    @Override
    void destroy(ChannelSession channel) throws Exception {
        log.info "SshCommandPrompt::destroy"
        inputStream.close()
        out.close()
        err.close()
        SshEventRegistry.Command.closeCommandConnection(session.username)
    }

    @Override
    void setSession(ServerSession session) {
        log.info "SshCommandPrompt::setSession"
        this.session = session
        SshEventRegistry.Command.newCommandConnection(session.username)
    }
}
