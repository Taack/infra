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
- Fix table grouping / trees with pagination, rework grouping to be trees (do an iterateOnGroup) 
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

# 0.5.x

- Improve/fix mail rendering using bootstrap
- Improve PDF rendering using bootstrap
- Vertical / Horizontal menu switch
- Add toast and websockets events
- SVG theme, use id
- Keyboard shortcut (+, CTRL + S, Enter, tab for nav ... between blocks / menu, and inside too)
- Ajax scrollable tables
- PDF / Docx / pptx / HTML from Asciidoc
- PoC new Charts DSL
- PoC inline DSL JS (IJavascriptDescriptor.groovy)
- PoC inline DSL CSS (with params)
- Asciidoc online editor (paragraph only)
- inlineForms custom in show and tables (row or cells)
- improve history when browsing, allow seeing opened modals ...
- Hook to register typical object filter (list, secure)
- Secured Solr Search
- More Secured TQL
- PoC PDF forms ...
- Modern Graph DSL (groupBy, sortBy)
  - timeSeries
  - areaChart
  - boxPot
  - bubbleChart
  - calendar
  - gantt
  - countries
  - webGl charts
- Remove Chart.js
  - Need server side rendering (simpler)
  - Why not JFreeCharts ? NO... but why ??
- AsciidoctorJ
  - remove external kotlin code transpiled
  - support offline plugins
  - allow code on TQL like language
- Remove PlantUML deps for Gantt / graphs
  - Server side raster and SVG
  - Low level first
- Allow dynamic graphs for Web only
  - manage picking, hover
  - manage moving, freedom degrees, constraints ...
  - PoC: DSLs describing actions