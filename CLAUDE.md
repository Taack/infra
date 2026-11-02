# infra (Taack-UI)

Grails 7 plugins that provide the Taack-UI DSL. Consumers such as `../intranet3` use the jars published to **mavenLocal**, at the version `taackReleaseVersion` from `gradle.properties` (e.g. `0.7.12_SNAPSHOT`).
The `bin/` directories are IDE build output: ignore them.

## Modules

- `taack-ui`: the core.
  - DSL: `taack/ui/dsl`.
  - HTML rendering: `taack/ui/dump`.
  - Diagrams: `dump/diagram`.
  - Services: `grails-app/services/taack/{render,domain,app}`.
  - Kotlin/JS client bundle: `grails-app/assets/javascripts/client.js`.
- `taack-ui-pdf`: `UiPrintableSpecifier`, `RawHtmlPrintableDump`, `TaackUiPdfService`. HTML is turned into PDF by Flying Saucer.
- `taack-ui-search` (Solr), `taack-ui-jdbc`, `taack-ui-ssh`, `taack-ui-asciidoc`, `taack-ui-ext`, `taack-jdbc-common`.
- `taack-ui-test` and `taack-ui-test-minimal`: demo apps. Look there for DSL usage examples.
- `browser/client`: Kotlin/JS sources of `client.js`. It is not part of the root Gradle build.

## Commands

- Compile: `./gradlew :taack-ui:compileGroovy` (or `:taack-ui-pdf:compileGroovy`).
- Publish for consumers, only the modules you changed: `./gradlew :taack-ui:publishToMavenLocal :taack-ui-pdf:publishToMavenLocal`. Then restart the consumer server, because jars are not hot-reloaded.
- Client (inferred from the build output, verify before relying on it): `cd browser/client && ./gradlew jsBrowserDistribution`. Then copy `build/dist/js/productionExecutable/client.js{,.map}` into `taack-ui/grails-app/assets/javascripts/`.

## Architecture

- **DSL to visitor to dump:**
  - each `Ui*Specifier` runs its closure on a `*Spec`;
  - the spec calls an `IUi*Visitor`;
  - the `RawHtml*Dump` classes write HTML, and `RawHtmlPrintableDump` covers PDF.
  - A new DSL feature needs a spec method, an interface method, a no-op in the default `Ui*Visitor`, and the dump implementations.
  - When you change an interface (`IUi*Visitor`, `IDiagramRender`), search for every implementor with rg, including in `taack-ui-pdf` and `taack-ui-test`.
- **Security:**
  - `TaackUiEnablerService.securityClosure(closure, actions…)` and `hasAccess` decide whether a link is shown.
  - DSL elements (menu, menuIcon, rowAction, diagram click) check access and hide what is denied.
  - `sanitizeString` keeps formatting tags and links in labels.
- **Client (`browser/client`):**
  - links are ajax by default (`BaseAjaxAction`);
  - an action whose name contains `downloadBin` uses native navigation;
  - a table `rowAction` towards a `downloadBin` action with `inline` opens in a new tab.
- **Diagrams:**
  - `UiDiagramSpecifier` goes to `RawHtmlDiagramDump`, then to a `DiagramScene` subclass (`dump/diagram/scene`), which draws on an `IDiagramRender`.
  - The renderer is `SvgDiagramRender` on the web and `PngDiagramRender` in PDF, because Flying Saucer has no SVG support.
  - Scenes must work with both renderers. `renderImage` takes an href in SVG and a local file path in PNG.
  - Click: `DiagramOption.builder.setClickAction(action, params)`. The scene wraps clickable shapes in `renderGroup(['element-type': ElementType.TOOLTIP, 'diagram-action-url': url, 'data-x': …])`. The client (`DiagramTooltip.kt`) calls the URL with `key`, `x` and `y`.
  - Org chart: `orgChart { node key, parentKey, title, subtitle, header, image }` in `OrgChartDiagramScene`.
- **PDF:**
  - the template `_block-pdf.gsp` exists in BOTH `taack-ui` and `taack-ui-pdf` (`grails-app/views/taackUi/`), so keep the two copies in sync;
  - page size and widths come from `UiPrintableSpecifier.Orientation`;
  - a diagram is embedded as a base64 PNG, scaled to fit the page box, and never enlarged more than `DIAGRAM_MAX_RATIO`;
  - `printableHeader*('1.5cm')` reduces the default 5cm top margin.

## Rules

- Keep the DSL backward compatible. Consumers are other repos, so add new parameters with defaults.
- Add Javadoc on public DSL methods. Elsewhere, comment only the non-obvious why.
- Commit only when asked.
