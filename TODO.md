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

# 0.4.2

- ~~Improve DSL hierarchy~~
  - ~~hidden fields on top only for readability~~
  - ~~filterField only under section only~~
  - ~~clean up form / table / filter DSLs~~
- Hook to register typical object filter (list, secure)
- ~~Hook to register form field (view, update, remove, secure)~~
- ~~Improve restore state~~
- re-add href instead of ajaxAction, improve history when browsing, allow seeing opened modals ...
- Fix table grouping / trees with paginating
- Bump CSS, improve theming
- ~~Improve Table / Filter~~
  - ~~add top level `condition` (`if`, branch on 2 closures)~~
    - ~~Test, debug, offline capabilities~~

# 0.4.3

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