package cms.dsl.parser.render

import cms.*
import cms.dsl.MediaStyle
import cms.dsl.MediaStyleKind
import cms.dsl.parser.Parser
import cms.dsl.parser.exception.WrongDataException
import grails.compiler.GrailsCompileStatic

import java.util.regex.Matcher
import java.util.regex.Pattern

@GrailsCompileStatic
class MarkDownRender {
    static class Line {
        public int top
        public int width
        public int left
        public int rotate
        public String color = "white"
    }

    static class Circle {
        public int top
        public int left
        public String color = "white"

    }

    static Line getLine(String dimensions) {
        Line draw = new Line()
        List<String> dimensionsType = ["Left", "Width", "Rotate", "Top", "Color"]
        for (int i = 0; i != dimensionsType.size(); i++) {
            // println(dimensionsType[i] + "      " + dimensions)
            Pattern p = Pattern.compile("(?i)" + dimensionsType[i] + '=(.*?)(?=,|$)')
            Matcher m = p.matcher(dimensions)
            if (m.find()) {
                if (dimensionsType[i].compareToIgnoreCase("Left") == 0) {
                    draw.left = Integer.parseInt(m.group(1))
                }
                if (dimensionsType[i].compareToIgnoreCase("Width") == 0) {
                    draw.width = Integer.parseInt(m.group(1))
                }
                if (dimensionsType[i].compareToIgnoreCase("Top") == 0) {
                    draw.top = Integer.parseInt(m.group(1))
                }
                if (dimensionsType[i].compareToIgnoreCase("Rotate") == 0) {
                    draw.rotate = Integer.parseInt(m.group(1))
                }
                if (dimensionsType[i].compareToIgnoreCase("Color") == 0) {
                    draw.color = m.group(1)
                    println(m.group(1))
                }

            }
        }
        return draw

    }

    static Circle getCircle(String dimensions) {
        Circle circle = new Circle()
        println(dimensions)
        List<String> dimensionsType = ["Left", "Top", "Color"]
        for (int i = 0; i != dimensionsType.size(); i++) {
            // println(dimensionsType[i] + "      " + dimensions)
            Pattern p = Pattern.compile("(?i)" + dimensionsType[i] + '=(.*?)(?=,|$)')
            Matcher m = p.matcher(dimensions)
            if (m.find()) {
                if (dimensionsType[i].compareToIgnoreCase("Left") == 0) {
                    circle.left = Integer.parseInt(m.group(1))
                }
                if (dimensionsType[i].compareToIgnoreCase("top") == 0) {
                    circle.top = Integer.parseInt(m.group(1))
                }
                if (dimensionsType[i].compareToIgnoreCase("Color") == 0) {
                    circle.color = m.group(1)
                    println(m.group(1))
                }

            }
        }
        return circle

    }

    private static String insert(CmsInsert insert, String lang) {
        if (insert) {
            CmsPage page = insert.cmsPage
            if (page) {
                """
<div class="insert page">
<div class="title">
${insert.title.get(lang)}
</div>
<div class="hat">
${CmsHtmlGeneratorService.translateNoMatcher(insert.hat.get(lang) as String)}
</div>
</div>
"""
            } else {
                Pattern pattern = Pattern.compile(";")
                String[] argument_splited = pattern.split("${insert.additionalClasses}")
                StringBuffer output = new StringBuffer()
                for (int i = 0; i < argument_splited.length; i++) {
                    List<String> elementName = ["Dash", "Circle", "A"]

                    for (int j = 0; j < elementName.size(); j++) {
                        Pattern p = Pattern.compile("(?i)" + elementName[j] + ":(.*)")
                        Matcher m = p.matcher(argument_splited[i])
                        while (m.find()) {
                            switch (elementName[j]) {
                                case "Dash":
                                    println("Dash ++" + elementName[j]
                                    )
                                    Line line = getLine(m.group(1))
                                    println("Line else = " + line.left + ", " + line.width + ", " + line.rotate + ", " + line.top)
                                    System.out.println("enter title")
                                    output.append(
                                            """
<div class="line" style="border-bottom: 4px solid ${line.color};   transform: rotate(${line.rotate}deg); position: absolute; border-style: none none dashed;left: ${line.left}px;width: ${line.width}px;  top:${line.top}px;">
</div>
""")
                                    break
                                case "Circle":

                                    Circle circle = getCircle(m.group(1))
                                    println("Circle ++" + elementName[j])
                                    println("Circle else = " + circle.top + ", " + circle.color)

                                    output.append(
                                            """
<span style="display: inline-block;  width: 21px;  height: 21px;  border-radius: 50%;  border-style: solid;  border-width: 4px;  border-color: ${circle.color};  background-color: rgba(0, 0, 0, 0);  position: absolute;  top:${circle.top}px; left:${circle.left}px;  pointer-events:none;    transform: translateX(-50%);" class="inner-circle"></span>
""")
                                    break

                            }
                        }
                    }
                }

                String title = insert.title.get(lang)
                String hat = insert.hat.get(lang)
                if (title) {
                    output.append(
                            """
<div class="insert item" style="top: ${insert.y}px; left: ${insert.x}px; ${
                                insert.width ? 'width: ' + insert.width + 'px' : ''
                            }; text-align: center">
<div class="title ${insert.additionalClasses ?: ''}">
${insert.title.get(lang)}
</div>
</div>
""")
                }
                output.toString()

            }
        } else ""
    }

    private static String renderApp(CmsImage aim, String lang, Parser.Arguments arguments) {
        String header = """
<div class="application" style="width: ${aim.width}px; margin: auto;">
<img src="/cms/mediaPreview/${aim.id}" alt="${aim.altText?.get(lang)}" $arguments>
"""
        StringBuffer body = new StringBuffer(header)
        println aim
        (CmsInsert.findAllByImageApplication(aim) as List<CmsInsert>).each { CmsInsert it ->
            println it

            body.append(insert(it, lang))
        }
        body.append("</div>")
        body.toString()
    }


    static String renderImage(String id, String lang, Parser.Arguments arguments) {
        CmsImage i = CmsImage.get(id.substring(1))
        if (i == null) throw new WrongDataException("Wrong Image ID", id)
        if (i.imageType == ImageType.APPLICATION_BACKGROUND)
            return renderApp(i, lang, arguments)
        String alt = i.altText.get(lang)

        if (arguments.format) {
            MediaStyle mediaStyleImage = MediaStyle.values().find {
                it.kind == MediaStyleKind.IMAGE && it.suffix == arguments.format
            }
            """
<img src="/cms/mediaPreview/${i.id}?mediaStyle=${mediaStyleImage}" alt="${alt}" $arguments>
"""
        } else
            """
<img src="/cms/mediaPreview/${i.id}" alt="${alt}" $arguments>
"""

    }

    static String renderVideo(String id, Parser.Arguments arguments, String lang) {
        CmsVideoFile v = CmsVideoFile.get(id.substring(1))
        if (v == null) throw new WrongDataException("Wrong Video ID", id)

        MediaStyle mediaStyle = MediaStyle.values().find {
            it.kind == MediaStyleKind.VIDEO && it.suffix == arguments.format
        } ?: MediaStyle.MEDIUM_VIDEO
        MediaStyle mediaStyleImage = MediaStyle.values().find {
            it.kind == MediaStyleKind.IMAGE && it.suffix == arguments.format
        } ?: MediaStyle.MEDIUM_IMAGE
        if (v.youtubeI18n[lang])
            """
<iframe width="720px" height="640px" src="https://www.youtube.com/watch?v=${v.youtubeI18n[lang]}">
</iframe>
"""
        else
            """
<video class="video" style="object-fit: cover; width: 720px;" poster="/cms/mediaPreview/${v.preview.id}?mediaStyle=${mediaStyleImage}" controls $arguments>
   <source src='/serverFile/getVideoFileFormat/${v.id}?mediaStyle=${mediaStyle}&lang=${lang}'  type='video/mp4'/>
</video>
"""
    }

    static String renderVideoImage(String id, String lang, Parser.Arguments arguments) {
        CmsVideoFile v = CmsVideoFile.read(id.substring(1))
        if (v == null) throw new WrongDataException("Wrong Image ID", id)
        CmsImage i = v.preview
        String alt = i?.altText?.get(lang)

        MediaStyle mediaStyleImage = MediaStyle.values().find {
            it.kind == MediaStyleKind.IMAGE && it.suffix == arguments.format
        } ?: MediaStyle.SMALL_IMAGE
        if (v.youtubeI18n[lang])
            """
<div class="imageContainer" ${arguments.style ? arguments : 'style="width: 320px; margin: auto;"'}>
<a class='popup' href='#!' data-link='${v.youtubeI18n[lang]}' data-width="${v.width ?: 960}px" data-height="${
                v.height ?: 640
            }px" >
<img ${arguments.style ? arguments.printWithoutProperties(['margin']) : 'width="320"'} src="/cms/mediaPreview/${i?.id?:0}?mediaStyle=${mediaStyleImage}" onload="youtubeVideo();" alt="${alt}" >
<div class="imageOverlay">${/*v.getDesc(lang)*/ false ?: ""}</div>
</a>
</div>
"""
        else """
<div class="imageContainer" ${arguments.style ? arguments : 'style="width: 320px; margin: auto;"'}>
<a class='popup' href='#!' data-link='/${lang}/video/${v.id}' data-width="${v.width ?: 960}px" data-height="${
            v.height ?: 640
        }px">
<img ${arguments.style ? arguments.printWithoutProperties(['margin']) : 'width="320"'} src="/cms/mediaPreview/${i?.id?:0}?mediaStyle=${mediaStyleImage}"  onload="youtubeVideo();" alt="${alt}" $arguments>
<div class="imageOverlay">${/*v.getDesc(lang)*/ false ?: ""}</div>
</a>
</div>
"""
    }

    static String renderPdf(String id, Parser.Arguments arguments, String lang) {
        CmsPdfFile v = CmsPdfFile.get(id.substring(1))
        if (v == null) throw new WrongDataException("Wrong Pdf ID", id)

        """
<div style="text-align: center;${arguments.style ? arguments.style.replace("'", "") : ''}">
<a href=''>
<img src="/cms/mediaPreviewPdf/${v.id}"/>
<div>${v.altText[lang]}</div>
</a>
</div>
"""
    }


    static String renderLinkFromUrl(String url, Parser.Arguments arguments) {
        "<a href='$url' ${arguments ?: ''}>"
    }

    static String renderLinkFromPage(String id, String lang, Parser.Arguments arguments) {
        renderLinkFromUrl "fakeUrl", arguments
    }


    static String renderImageOption(CmsImage i) {
        """
<img src="/cms/mediaPreview/${i.id}" style="width: 40px;"/>
            """

    }

}