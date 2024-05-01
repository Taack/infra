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

- Fix file path when updating. The same way than for O2M
- Improve restore state
- AsciidoctorJ
  - remove external kotlin code transpiled
  - support offline plugins
  - allow code on TQL like language
- Remove PlantUML deps for Gantt / graphs
  - Server side raster and SVG
  - Low level first
- Bump CSS, improve theming
- Remove Chart.js
  - Need server side rendering (simpler)
  - Why not JFreeCharts ? NO... but why ??
- Secured Solr Search
- More Secured TQL

# 0.4.2

- PoC PDF forms ...
- Improve Table / Filter
  - add top level `condition` (`if`, branch on 2 closures)
    - Test, debug, offline capabilities

# 0.4.3

- Allow dynamic graphs for Web only
  - manage picking, hover
  - manage moving, freedom degrees, constraints ...
  - PoC: DSLs describing actions