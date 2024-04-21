# 3.9

- Grails 6.2.0
- Cleaner deps

# 3.10

- AsciidoctorJ
  - remove external kotlin code transpiled
  - support offline plugins
  - allow code on TQL like language

# 3.11

- Secured Solr Search
- More Secured TQL

# 4.0

- Improve Filter / Form DSLs
  - implicit or declarative: okButton, saveButton, defaultButton ...
- Improve Table / Filter
  - add `loop` (a `for` merged with `list`)
    - builders for additional filters
    - custom paginate
  - add top level `condition` (`if`, branch on 2 closures)
    - Test, debug, offline capabilities

# 4.1

- Remove PlantUML deps for Gantt / graphs
  - Server side raster and SVG
  - Low level first
- Bump CSS, improve theming
- Remove Chart.js
  - Need server side rendering (simpler)
  - Why not JFreeCharts ? NO... but why ??

# 4.2

- PoC PDF forms ...

# 4.3

- Allow dynamic graphs for Web only
  - manage picking, hover
  - manage moving, freedom degrees, constraints ...
  - PoC: DSLs describing actions