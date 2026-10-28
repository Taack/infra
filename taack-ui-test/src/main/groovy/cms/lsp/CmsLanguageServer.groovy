package cms.lsp

import groovy.transform.CompileStatic
import org.eclipse.lsp4j.*
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.lsp4j.services.LanguageServer
import org.eclipse.lsp4j.services.TextDocumentService
import org.eclipse.lsp4j.services.WorkspaceService

import java.util.concurrent.CompletableFuture

/**
 * Language Server for the CMS page body editor (Asciidoc + CMS <code>${...}</code> expressions).
 *
 * The transport is one HTTP POST per JSON-RPC message, so an instance only lives for one message:
 * the documents are kept by the caller (in the HTTP session) and the CMS logic is in {@link CmsLanguageFeatures}.
 * Only full text synchronization and pull diagnostics (LSP 3.17) are supported, since the server cannot push.
 */
@CompileStatic
class CmsLanguageServer implements LanguageServer {

    /**
     * CMS knowledge used to answer requests, implemented by <code>CmsLspService</code>.
     */
    interface CmsLanguageFeatures {
        List<CompletionItem> complete(String text, Position position)

        List<Diagnostic> diagnose(String text)
    }

    private final Map<String, String> documents
    private final CmsLanguageFeatures features

    private final TextDocumentService textDocumentService = new TextDocumentService() {
        @Override
        void didOpen(DidOpenTextDocumentParams params) {
            documents.put(params.textDocument.uri, params.textDocument.text)
        }

        @Override
        void didChange(DidChangeTextDocumentParams params) {
            if (params.contentChanges) documents.put(params.textDocument.uri, params.contentChanges.last().text)
        }

        @Override
        void didClose(DidCloseTextDocumentParams params) {
            documents.remove(params.textDocument.uri)
        }

        @Override
        void didSave(DidSaveTextDocumentParams params) {
        }

        @Override
        CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams params) {
            String text = documents.get(params.textDocument.uri)
            List<CompletionItem> items = text != null ? features.complete(text, params.position) : []
            CompletableFuture.completedFuture(Either.<List<CompletionItem>, CompletionList> forLeft(items))
        }

        @Override
        CompletableFuture<DocumentDiagnosticReport> diagnostic(DocumentDiagnosticParams params) {
            String text = documents.get(params.textDocument.uri)
            List<Diagnostic> diagnostics = text != null ? features.diagnose(text) : []
            CompletableFuture.completedFuture(new DocumentDiagnosticReport(new RelatedFullDocumentDiagnosticReport(diagnostics)))
        }
    }

    private final WorkspaceService workspaceService = new WorkspaceService() {
        @Override
        void didChangeConfiguration(DidChangeConfigurationParams params) {
        }

        @Override
        void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
        }
    }

    CmsLanguageServer(Map<String, String> documents, CmsLanguageFeatures features) {
        this.documents = documents
        this.features = features
    }

    @Override
    CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        ServerCapabilities capabilities = new ServerCapabilities()
        capabilities.setTextDocumentSync(TextDocumentSyncKind.Full)
        capabilities.setCompletionProvider(new CompletionOptions(false, ['{', ':', '#', ' ']))
        capabilities.setDiagnosticProvider(new DiagnosticRegistrationOptions(false, false))
        CompletableFuture.completedFuture(new InitializeResult(capabilities, new ServerInfo('taack-cms-lsp', '0.1')))
    }

    @Override
    CompletableFuture<Object> shutdown() {
        CompletableFuture.completedFuture(null)
    }

    @Override
    void exit() {
    }

    @Override
    TextDocumentService getTextDocumentService() {
        textDocumentService
    }

    @Override
    WorkspaceService getWorkspaceService() {
        workspaceService
    }
}
