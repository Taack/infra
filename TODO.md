# 0.3.9

- ~~Grails 6.2.0~~
- ~~Cleaner deps~~

# 0.4.0

- ~~Improve Table / Filter~~
  - ~~add `loop` (a `for` merged with `list`)~~
    - ~~builders for additional filters~~
  - ~~auto-paginate~~
- ~~Auto-labeling for menus~~ 
- ~~Remove isAjax parameter~~
- ~~rowLink~~ and buttons without i18n
- ~~remove rowField direct value (to force ability to mask results)~~
- ~~Avoid ajaxBlock when possible~~
- ~~optional form i18n params~~
- ~~optional formAction i18n~~

# 0.4.1

- ~~Move supported Language into menus~~
- ~~Merge search menu, icon menu, and language Menu~~
- _~~More DSL level for tables~~_ (not needed)
- ~~Keep some params ... (lang, subsidiary, stock, others ...) via menu DSL~~
- ~~fix style for tableRow, add class list~~
- ~~Allow debugging Kotlin JS code~~ 
- ~~Test mac runtime and devel cold auto-restart~~
- ~~Solr indexField auto-labeling~~

# 0.5.0

- ~~Improve DSL hierarchy~~
  - ~~hidden fields on top only for readability~~
  - ~~filterField only under section only~~
  - ~~clean up form / table / filter DSLs~~
- ~~Hook to register form field (view, update, remove, secure)~~
- ~~Improve restore state~~
- ~~Reduce CSS and JS~~
- ~~rework grouping to be trees~~
- ~~Improve form layout, add row in DSL~~
- ~~More coherent button style in forms~~
- ~~More restrictive DSL~~
- ~~Simplify i18n, default action with naming convention starting with save, select, preview ... should be auto-translated~~
- ~~Bump CSS, improve theming~~
- ~~improve block caption~~
- ~~Bootstrap~~
- ~~Add menus to Blocks~~
- ~~Improve Table / Filter~~
  - ~~add top level `condition` (`if`, branch on 2 closures)~~
    - ~~Test, debug, offline capabilities~~
- ~~Add title to modal / menu / size~~
- ~~For GormEntity, propose a default M2O or M2M in forms (field) / shows~~
- ~~remove Width from form ... layout managed from elsewhere..~~ 
- ~~add col and row~~
- ~~remove close title~~
- ~~remove visit*End when possible~~
- ~~add refresh current block (see refresh in cms/editPage)~~
- ~~use different translations for `create` or `edit`, if ID is present~~
- ~~Use bootstrap progress~~
- ~~Choose bootstrap from jDeliver or custom~~
- ~~Add **horizontal scrollbar on tables** when needed~~

# 0.5.1

- ~~TaackPluginConfiguration, replace mainController by methodClosure~~
- ~~TaackApps must not be a grails plugin~~
- ~~Port intranet apps~~
- ~~PoC new Charts DSL~~

# 0.5.7-y(y<5)

- ~~Improve PDF rendering using bootstrap~~
- ~~PoC inline DSL JS (IJavascriptDescriptor.groovy)~~
- ~~PoC inline DSL CSS (with params)~~
- ~~Remove Chart.js~~
  - ~~Need server side rendering (simpler)~~
  - ~~Why not JFreeCharts ? NO... but why ??~~
- ~~AsciidoctorJ~~
  - ~~support offline plugins~~
  - ~~allow code on TQL like language~~
- ~~Remove PlantUML deps for Gantt / graphs~~
  - ~~Server side raster and SVG~~
  - ~~Low level first~~

# 0.5.7

- ~~Asciidoc online editor (h2...h4, paragraph only, inline style)~~
- upload attachments when dragging or pasting files to an Asciidoc area  
- ~~Asciidoc show, like for md~~
- ~~Modern Graph DSL (groupBy, sortBy)~~
  - ~~timeSeries~~
  - ~~areaChart~~
  - ~~bubbleChart~~
- ~~Security~~
  - ~~add sanitizer~~
  - ~~Call for all cases `taackUiEnablerService.checkAccess()`~~
- ~~bug fixes and dependency version bumps~~

# 0.5.x (x>7)

- Allow asciidoc diagram
- Allow asciidoc taack extensions
- improve history when browsing, allow seeing opened modals ...
- Modern Graph DSL (groupBy, sortBy)
  - boxPot
  - calendar
  - gantt
  - countries
  - webGl charts


0.6.x
- PoC merge crew service into Infra
- Grails 7.x
- Improve/fix mail rendering using bootstrap
- Add toast and websockets events (or not ...)
- SVG theme, use id
- Keyboard shortcut (+, CTRL + S, Enter, tab for nav ... between blocks / menu, and inside too)
- Ajax scrollable tables
- PDF / Docx / pptx / HTML from Asciidoc
- Hook to register typical object filter (list, secure)
- Secured Solr Search
- More Secured TQL
- inlineForms custom in show and tables (row or cells)
- PoC PDF forms ...
