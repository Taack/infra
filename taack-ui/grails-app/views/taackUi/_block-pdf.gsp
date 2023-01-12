<%--
  Created by IntelliJ IDEA.
  User: auo
  Date: 02/03/2020
  Time: 03:03
--%>

<%--
  Created by IntelliJ IDEA.
  User: auo
  Date: 30/04/2020
  Time: 08:12
--%>
<!DOCTYPE html>

<html>
<head>
    <meta charset="utf-8">

    <style>

    ${css}

    div#header {
        /*display: inline-block;*/
        border: 1px black solid;
        /*margin-left: -1cm;*/
        text-align: center;
        position: running(header-top-center);
    }

    div#header-left {
        /*margin-left: -1cm;*/
        /*display: inline-block;*/
        /*border: 1px red solid;*/
        text-align: left;
        position: running(header-top-left);
    }

    div#header-right {
        /*display: inline-block;*/
        border: 1px blue solid;
        /*margin-left: -1cm;*/
        text-align: right;
        position: running(header-top-right);
    }

    div#footer {
        display: block;
        text-align: center;
        font-size: 3mm;
        position: running(footer);
    }

    body {
        width: 190mm;
        /*height: 297mm;*/
    }

    @page {
        /*-fs-page-orientation: portrait;*/
        size: 210mm 297mm;
        @top-left {
            content: element(header-top-left)
        }
        @top-center {
            content: element(header-top-center)
        }
        @top-right {
            content: element(header-top-right)
        }

        margin-top: ${headerHeight?:"5cm"};
        @bottom-center {
            /*font-style: italic;*/
            /*font-family: "DINPro-LightItalic";*/
            content: element(footer)
        }
        @bottom-right {
            font-size: 3mm;
            content: counter(page) " / " counter(pages)
        }
    }

    div.pure-u-1 {
        /*border: 1px red solid;*/

        page-break-before: avoid;
        page-break-inside: auto;
        page-break-after: avoid;
    }

    table {
        -fs-table-paginate: paginate;
        width: 18.4cm;
        page-break-before: avoid;
        page-break-inside: auto;
        page-break-after: auto;
        /*margin-left: 1mm;*/
        margin-top: 6mm;
        border-collapse: collapse;
        font-size: 3mm;

    }

    .columnSmall {
        width: 12mm;
    }

    .terms {
        page-break-inside: auto;
        font-size: 2.5mm;
        line-height: 3mm;
    }

    .separator td {
        border-bottom: 0.3mm solid black;
    }

    tr, td {
        page-break-before: auto;
        page-break-inside: avoid;
        page-break-after: auto;
    }

    tbody {
        page-break-before: auto;
        page-break-inside: auto;
        page-break-after: auto;
    }

    thead {
        page-break-before: auto;
        page-break-inside: auto;
        page-break-after: auto;
        display: table-header-group;
    }

    tfoot {
        display: table-footer-group;
    }

    li {
        list-style-type: none;
    }

    .markdown-body ul{
        list-style: none;
        padding-left: 20px;
    }

    .markdown-body li:before{ content:"•"; font-size:10pt; }

    .markdown-body li li:before{ content:"•"; font-size:10pt; }

    ol {
        /*padding-inline-start: 0;*/
        padding-left: 0;
    }

    .pure-u-1, .pure-u-1-1, .pure-u-1-2, .pure-u-1-3, .pure-u-2-3, .pure-u-1-4, .pure-u-3-4, .pure-u-1-5, .pure-u-2-5, .pure-u-3-5, .pure-u-4-5, .pure-u-5-5, .pure-u-1-6, .pure-u-5-6, .pure-u-1-8, .pure-u-3-8, .pure-u-5-8, .pure-u-7-8, .pure-u-1-12, .pure-u-5-12, .pure-u-7-12, .pure-u-11-12, .pure-u-1-24, .pure-u-2-24, .pure-u-3-24, .pure-u-4-24, .pure-u-5-24, .pure-u-6-24, .pure-u-7-24, .pure-u-8-24, .pure-u-9-24, .pure-u-10-24, .pure-u-11-24, .pure-u-12-24, .pure-u-13-24, .pure-u-14-24, .pure-u-15-24, .pure-u-16-24, .pure-u-17-24, .pure-u-18-24, .pure-u-19-24, .pure-u-20-24, .pure-u-21-24, .pure-u-22-24, .pure-u-23-24, .pure-u-24-24 {
        display: block;
        float: left;
    }
    body {
        font-size: small;
    }

    .pure-table td {
        line-height: 18px;
        font-size: 11px !important;
    }


    %{--@font-face {--}%
    %{--    font-family: Manrope;--}%
    %{--    font-style: normal;--}%
    %{--    font-weight: normal;--}%
    %{--    src: url("file://${root}/pdf/fonts/Manrope-Regular.ttf");--}%
    %{--    /*-fs-font-subset: complete-font;*/--}%
    %{--}--}%

    %{--@font-face {--}%
    %{--    font-family: Inter;--}%
    %{--    font-style: normal;--}%
    %{--    font-weight: normal;--}%
    %{--    src: url("file://${root}/pdf/fonts/Inter-Regular.ttf");--}%
    %{--    /*-fs-font-subset: complete-font;*/--}%
    %{--}--}%

    @font-face {
        font-family: NotoSansSC;
        font-style: normal;
        font-weight: normal;
        src: url("file://${root}/pdf/fonts/NotoSansSC-Regular.ttf");
        /*-fs-font-subset: complete-font;*/
    }

    @font-face {
        font-family: Roboto;
        font-style: normal;
        font-weight: normal;
        src: url("file://${root}/pdf/fonts/Roboto-Medium.ttf");
        /*-fs-font-subset: complete-font;*/
    }

    %{--@font-face {--}%
    %{--    font-family: Manrope;--}%
    %{--    font-style: italic;--}%
    %{--    font-weight: 400;--}%
    %{--    src: url("file://${root}/pdf/fonts/LiberationSans-Italic.ttf");--}%
    %{--    -fs-font-subset: complete-font;--}%
    %{--}--}%

    %{--@font-face {--}%
    %{--    font-family: Manrope;--}%
    %{--    font-style: normal;--}%
    %{--    font-weight: bold;--}%
    %{--    src: url("file://${root}/pdf/fonts/Manrope-Bold.ttf");--}%
    %{--    /*-fs-font-subset: complete-font;*/--}%
    %{--}--}%

    @font-face {
        font-family: NotoSansSC;
        font-style: normal;
        font-weight: bold;
        src: url("file://${root}/pdf/fonts/NotoSansSC-Regular.ttf");
        /*-fs-font-subset: complete-font;*/
    }

    @font-face {
        font-family: Roboto;
        font-style: normal;
        font-weight: bold;
        src: url("file://${root}/pdf/fonts/Roboto-Bold.ttf");
        /*-fs-font-subset: complete-font;*/
    }

    %{--@font-face {--}%
    %{--    font-family: Inter;--}%
    %{--    font-style: normal;--}%
    %{--    font-weight: bold;--}%
    %{--    src: url("file://${root}/pdf/fonts/Inter-Bold.ttf");--}%
    %{--    /*-fs-font-subset: complete-font;*/--}%
    %{--}--}%

    %{--@font-face {--}%
    %{--    font-family: Manrope;--}%
    %{--    font-style: italic;--}%
    %{--    font-weight: bold;--}%
    %{--    src: url("file://${root}/pdf/fonts/LiberationSans-BoldItalic.ttf");--}%
    %{--    -fs-font-subset: complete-font;--}%
    %{--}--}%

    html {
        font-family: NotoSansSC, Roboto, sans-serif;
    }

    .pure-g {
        font-family: NotoSansSC, Roboto, sans-serif;
    }

    .pure-g [class *= 'pure-u'] {
        font-family: NotoSansSC, Roboto, sans-serif;
    }

    table.general-condition {
        font-size: smaller;
        page-break-inside: avoid;
    }

    span.property-value p {
        margin: 0.3cm;
    }
    ul.property-list, ul.taackSection {
        padding: 0;
    }

    ul.taackSection {
        padding-top: 0.5cm;
    }

    ul.taackSection>li {
        padding-left: 0.5cm;
    }

    span.property-value li {
        margin-top:0.1cm;
        list-style-type: disc;
    }

    li.fieldcontain {
        page-break-inside: avoid;
    }

    .taackMarkdown li {
        all: revert;
    }

    .taackMarkdown ol {
        all: revert;
    }



    </style>
    <title></title>
</head>

<body>
${raw(block)}
</body>
</html>
