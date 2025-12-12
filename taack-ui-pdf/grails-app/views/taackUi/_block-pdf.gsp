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

    <title></title>
<style>
    
${raw(css)}

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
        content: counter(page) " / " counter(pages);
    }
}

.pure-u-1-3 {
    width: 60mm;
    display: inline-block;
    vertical-align: top;
}

.pure-u-1-2 {
    width: 90mm;
    display: inline-block;
    vertical-align: top;
}

th, td {
    border-bottom: 1px solid #d5d5d5;
}

th {
    background: #f8f9ff;
}

td {
    border-right: 1px solid #f5f5f5;
}
</style>
</head>

<body style="font-family: 'Noto Sans', sans, sans-serif;">
${raw(block)}
</body>
</html>
