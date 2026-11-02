package cms

import cms.dsl.parser.Parser
import cms.dsl.parser.exception.ParserException
import cms.dsl.parser.exception.TokenizerException
import cms.dsl.parser.exception.WrongDataException
import cms.lsp.CmsLanguageServer
import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional
import grails.util.Triple
import org.eclipse.lsp4j.*
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException
import org.eclipse.lsp4j.jsonrpc.json.MessageJsonHandler
import org.eclipse.lsp4j.jsonrpc.messages.*
import org.eclipse.lsp4j.jsonrpc.services.GenericEndpoint
import org.eclipse.lsp4j.jsonrpc.services.ServiceEndpoints
import org.eclipse.lsp4j.services.LanguageServer
import taack.wysiwyg.Asciidoc

import java.util.concurrent.ExecutionException
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Answers the JSON-RPC messages of the CMS body editor, see {@link CmsLanguageServer}.
 */
@GrailsCompileStatic
@Transactional(readOnly = true)
class CmsLspService {

    private static final MessageJsonHandler jsonHandler = new MessageJsonHandler(ServiceEndpoints.getSupportedMethods(LanguageServer))
    private static final Pattern imageMacro = Pattern.compile(/image::?([^\[\s]+)\[/)
    private static final Pattern imageMacroBeforeCursor = Pattern.compile(/image::?([^\[\s]*)$/)

    /**
     * Process one JSON-RPC message.
     *
     * @param body      the message
     * @param documents the opened documents of the HTTP session, by URI
     * @param page      the edited page, null while it is not saved yet
     * @param language  the edited language, iso2 lowercase
     * @return the JSON response, or null for a notification
     */
    String handle(String body, Map<String, String> documents, CmsPage page, String language) {
        Message message = jsonHandler.parseMessage(body)
        GenericEndpoint endpoint = new GenericEndpoint(new CmsLanguageServer(documents, features(page, language)))

        if (message instanceof NotificationMessage) {
            NotificationMessage notification = message as NotificationMessage
            endpoint.notify(notification.method, notification.params)
            return null
        }
        if (!(message instanceof RequestMessage)) return null

        RequestMessage request = message as RequestMessage
        ResponseMessage response = new ResponseMessage()
        response.jsonrpc = '2.0'
        response.rawId = request.rawId
        try {
            response.result = endpoint.request(request.method, request.params).get()
        } catch (ExecutionException e) {
            Throwable cause = e.cause
            if (cause instanceof ResponseErrorException) response.error = (cause as ResponseErrorException).responseError
            else {
                log.error("LSP ${request.method} failed", cause)
                response.error = new ResponseError(ResponseErrorCode.InternalError, String.valueOf(cause?.message), null)
            }
        }
        jsonHandler.serialize(response)
    }

    private CmsLanguageServer.CmsLanguageFeatures features(CmsPage page, String language) {
        new CmsLanguageServer.CmsLanguageFeatures() {
            @Override
            List<CompletionItem> complete(String text, Position position) {
                completion(page, language, text, position)
            }

            @Override
            List<Diagnostic> diagnose(String text) {
                diagnostics(page, language, text)
            }
        }
    }

    List<CompletionItem> completion(CmsPage page, String language, String text, Position position) {
        List<String> lines = text.split('\n', -1).toList()
        if (position.line >= lines.size()) return []
        String line = lines[position.line]
        String before = line.substring(0, Math.min(position.character, line.length()))

        int exprStart = before.lastIndexOf('${')
        if (exprStart != -1 && before.indexOf('}', exprStart) == -1) {
            return expressionCompletion(language, before.substring(exprStart + 2).stripLeading(), position)
        }

        Matcher m = imageMacroBeforeCursor.matcher(before)
        if (m.find()) {
            String typed = m.group(1)
            Range range = new Range(new Position(position.line, position.character - typed.length()), position)
            return pageImageNames(page).findAll { it.startsWith(typed) }.collect { String name ->
                CompletionItem item = new CompletionItem(name)
                item.kind = CompletionItemKind.File
                item.detail = 'Image attached to this page'
                item.textEdit = Either.forLeft(new TextEdit(range, "${name}[]".toString()))
                item
            }
        }
        []
    }

    private static List<CompletionItem> expressionCompletion(String language, String expression, Position position) {
        Parser.Suggestion suggestion
        try {
            suggestion = new Parser(language).suggest(expression)
        } catch (ignored) {
            return []
        }
        Range atCursor = new Range(position, position)
        suggestion.results.collect { Triple<String, String, String> r ->
            CompletionItem item = new CompletionItem(r.bValue)
            item.kind = suggestion.suggesterStatus == Parser.SuggesterStatus.FIRST_EXPR ? CompletionItemKind.Keyword : CompletionItemKind.Reference
            item.detail = suggestionDetail(suggestion.suggesterStatus, r)
            item.textEdit = Either.forLeft(new TextEdit(atCursor, r.aValue))
            item
        }
    }

    private static String suggestionDetail(Parser.SuggesterStatus status, Triple<String, String, String> r) {
        // Image and video suggestions describe themselves with an <img> tag only
        if (status == Parser.SuggesterStatus.ID_IMG) return CmsImage.read(r.bValue.substring(1) as Long)?.originalName
        if (status == Parser.SuggesterStatus.ID_VID) return CmsVideoFile.read(r.bValue.substring(1) as Long)?.toString()
        r.cValue?.replaceAll(/<[^>]*>/, '')?.trim()
    }

    List<Diagnostic> diagnostics(CmsPage page, String language, String text) {
        List<Diagnostic> res = []
        List<String> lines = text.split('\n', -1).toList()

        for (Asciidoc.Problem p : Asciidoc.validate(text)) {
            int line = Math.min(Math.max(p.line - 1, 0), lines.size() - 1)
            res << diagnostic(new Range(new Position(line, 0), new Position(line, lines[line].length())), severity(p.severity), 'asciidoctor', p.message)
        }

        int index = text.indexOf('${')
        while (index != -1) {
            int indexTo = text.indexOf('}', index)
            if (indexTo == -1) {
                res << diagnostic(range(text, index, index + 2), DiagnosticSeverity.Error, 'cms', 'No closing brace')
                break
            }
            String error = expressionError(language, text.substring(index + 2, indexTo))
            if (error) res << diagnostic(range(text, index, indexTo + 1), DiagnosticSeverity.Error, 'cms', error)
            index = text.indexOf('${', indexTo + 1)
        }

        if (page) {
            Set<String> images = pageImageNames(page)
            lines.eachWithIndex { String line, int i ->
                Matcher m = imageMacro.matcher(line)
                while (m.find()) {
                    String target = m.group(1)
                    if (!target.contains('://') && !images.contains(target)) {
                        res << diagnostic(new Range(new Position(i, m.start(1)), new Position(i, m.end(1))), DiagnosticSeverity.Warning, 'cms', "Image '${target}' is not attached to this page")
                    }
                }
            }
        }
        res
    }

    private static String expressionError(String language, String expression) {
        try {
            new Parser(language).parse(expression)
            return null
        } catch (TokenizerException te) {
            return "${te.message}, ${te.problematicString}"
        } catch (ParserException pe) {
            return "${pe.message}, ${pe.lookahead}"
        } catch (WrongDataException wde) {
            return "${wde.message}, ${wde.id}"
        } catch (Exception e) {
            return "Cannot render expression: ${e.message}"
        }
    }

    private static Set<String> pageImageNames(CmsPage page) {
        if (!page) return [] as Set<String>
        Set<String> names = new HashSet<String>()
        for (CmsImage i : (page.bodyImages ?: [] as Set<CmsImage>) + CmsImage.findAllWhere(cmsPage: page)) {
            if (i.originalName) names.add(i.originalName)
        }
        names
    }

    private static Range range(String text, int from, int to) {
        new Range(position(text, from), position(text, to))
    }

    private static Position position(String text, int offset) {
        int line = 0
        int lineStart = 0
        for (int i = 0; i < offset; i++) {
            if (text.charAt(i) == '\n' as char) {
                line++
                lineStart = i + 1
            }
        }
        new Position(line, offset - lineStart)
    }

    private static DiagnosticSeverity severity(String asciidoctorSeverity) {
        switch (asciidoctorSeverity) {
            case 'ERROR':
            case 'FATAL':
                return DiagnosticSeverity.Error
            case 'WARN':
                return DiagnosticSeverity.Warning
            default:
                return DiagnosticSeverity.Information
        }
    }

    private static Diagnostic diagnostic(Range range, DiagnosticSeverity severity, String source, String message) {
        new Diagnostic(range, message, severity, source)
    }
}
