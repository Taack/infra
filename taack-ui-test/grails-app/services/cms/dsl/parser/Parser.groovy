package cms.dsl.parser

import cms.*
import cms.dsl.parser.exception.ParserException
import cms.dsl.parser.exception.WrongDataException
import cms.dsl.parser.render.MarkDownRender
import crew.config.SupportedLanguage
import grails.util.Triple
import groovy.transform.CompileStatic

// http://cogitolearning.co.uk/2013/05/writing-a-parser-in-java-implementing-the-parser/
@CompileStatic
class Parser {
    Iterator<Tokenizer.TokenInfo> tokens
    private Tokenizer.TokenInfo lookahead
    private final String lang
    private int start
    private int end
    private boolean checkValue = true

    enum SuggesterStatus {
        FIRST_EXPR,
        ID_IMG,
        ID_PAGE,
        ID_VID,
        ID_SLIDESHOW,
        ID_PDF,
        ID_INSERT,
        SECOND_ARGS,
        CLOSE
    }

    SuggesterStatus suggesterStatus

    Parser(String lang) {
        this.lang = lang
    }

    private void nextToken() {
        // at the end of input we return an epsilon token
        if (tokens.hasNext()) {
            lookahead = tokens.next()
            start = lookahead.start
            end = lookahead.end
        } else {
            lookahead = new Tokenizer.TokenInfo(Token.EPSILON, '', end, end)
        }
    }

    String parse(String expr) {
        Tokenizer tokenizer = new Tokenizer()
        suggesterStatus = SuggesterStatus.FIRST_EXPR
        tokenizer.tokenize(expr)
        parse(tokenizer.tokens)
    }

    String parse(List<Tokenizer.TokenInfo> tokens) {
        this.tokens = tokens.iterator()
        if (!this.tokens.hasNext()) {
            nextToken()
            return
        }
        nextToken()

        suggesterStatus = SuggesterStatus.FIRST_EXPR
        String ret = expression()

        if (lookahead.token != Token.EPSILON) {
            throw new ParserException('Unexpected symbol found!', this.tokens, lookahead)
        }
        ret
    }

    String expression() {
        String ret = ''
        if (lookahead.token == Token.IMG) {
            suggesterStatus = SuggesterStatus.ID_IMG
            nextToken()
            if (lookahead.token == Token.ID) {
                String id = lookahead.sequence
                if (checkValue) {
                    CmsImage i = CmsImage.read(id.substring(1))
                    if (!i) throw new WrongDataException('No Image', id)
                    else if (i.hide) throw new WrongDataException('Image Not Published', id)
                }
                nextToken()
                ret = MarkDownRender.renderImage(id, lang, arguments(new Arguments()))
            } else {
                throw new ParserException('Missing Image ID', this.tokens, lookahead)
            }
        } else if (lookahead.token == Token.IMG_LINK) {
            suggesterStatus = SuggesterStatus.ID_IMG
            nextToken()
            if (lookahead.token == Token.ID) {
                String id = lookahead.sequence
                if (checkValue) {
                    CmsImage i = CmsImage.read(id.substring(1))
                    if (!i) throw new WrongDataException('No Image', id)
                    else if (i.hide) throw new WrongDataException('Image Not Published', id)
                }
                nextToken()
                ret = "/cms/mediaPreview/${id.substring(1)}"
            } else {
                throw new ParserException('Missing Image ID', this.tokens, lookahead)
            }
        } else if (lookahead.token == Token.VID) {
            suggesterStatus = SuggesterStatus.ID_VID
            nextToken()
            if (lookahead.token == Token.ID) {
                String id = lookahead.sequence
                if (checkValue) {
                    CmsVideoFile v = CmsVideoFile.read(id.substring(1))
                    if (!v) throw new WrongDataException('No Video', id)
                    else if (v.hide) throw new WrongDataException('Video Not Published', id)
                }
                nextToken()
                ret = MarkDownRender.renderVideo(id, arguments(new Arguments()), lang)
            } else {
                throw new ParserException('Missing Video ID', this.tokens, lookahead)

            }
        } else if (lookahead.token == Token.PDF) {
            suggesterStatus = SuggesterStatus.ID_PDF
            nextToken()
            if (lookahead.token == Token.ID) {
                String id = lookahead.sequence
                if (checkValue) {
                    CmsPdfFile v = CmsPdfFile.read(id.substring(1))
                    if (!v) throw new WrongDataException('No PDF', id)
                }
                nextToken()
                ret = MarkDownRender.renderPdf(id, arguments(new Arguments()), lang)
            } else {
                throw new ParserException('Missing Video ID', this.tokens, lookahead)

            }
        } else if (lookahead.token == Token.VID_LINK) {
            suggesterStatus = SuggesterStatus.ID_VID
            nextToken()
            if (lookahead.token == Token.ID) {
                String id = lookahead.sequence
                if (checkValue) {
                    CmsVideoFile v = CmsVideoFile.get(id.substring(1))
                    if (!v) throw new WrongDataException('No Video', id)
                    else if (v.hide) throw new WrongDataException('Video Not Published', id)
                }
                nextToken()
                ret = MarkDownRender.renderVideoImage(id, lang, arguments(new Arguments()))
            } else {
                throw new ParserException('Missing Video ID', this.tokens, lookahead)

            }
        } else if (lookahead.token == Token.SLIDESHOW) {
            suggesterStatus = SuggesterStatus.ID_SLIDESHOW
            nextToken()
            if (lookahead.token == Token.ID) {
                String id = lookahead.sequence
                if (checkValue) {
                    CmsPage v = CmsPage.get(id.substring(1))
                    if (!v) throw new WrongDataException('No Slideshow', id)
                    else if (!v.published) throw new WrongDataException('Slideshow Not Published', id)
                    else if (v.pageType != CmsPageType.SLIDESHOW) throw new WrongDataException("Slideshow Not a Slideshow ... ${v}", id)
                }
                nextToken()
                ret = CmsUiService.INSTANCE.bodySlideshow(CmsPage.read(id.substring(1)), SupportedLanguage.fromIso2(lang))
            } else {
                throw new ParserException('Missing Slideshow ID', this.tokens, lookahead)

            }
        } else if (lookahead.token == Token.INSERT_LINK) {
            suggesterStatus = SuggesterStatus.ID_INSERT
            nextToken()
            if (lookahead.token == Token.ID) {
                String id = lookahead.sequence
                if (checkValue) {
                    CmsInsert v = CmsInsert.get(id.substring(1))
                    if (!v) throw new WrongDataException('No Insert', id)
                }
                nextToken()
            } else {
                throw new ParserException('Missing Insert ID', this.tokens, lookahead)

            }
        } else if (lookahead.token == Token.ITEM_LINK) {
            suggesterStatus = SuggesterStatus.ID_INSERT
            nextToken()
            if (lookahead.token == Token.ID) {
                String id = lookahead.sequence
                nextToken()
            } else {
                throw new ParserException('Missing Insert ID', this.tokens, lookahead)
            }
        } else if (lookahead.token == Token.LINK) {
            suggesterStatus = SuggesterStatus.ID_PAGE
            nextToken()
            if (lookahead.token == Token.ID) {
                String id = lookahead.sequence
                if (checkValue) {
                    CmsPage p = CmsPage.get(id.substring(1))
                    if (!p) throw new WrongDataException('No Video', id)
                    else if (!p.published) throw new WrongDataException('Page Not Published', id)
                }
                nextToken()
                ret = MarkDownRender.renderLinkFromPage(id, lang, arguments(new Arguments()))
                nextToken()
            } else if (lookahead.token == Token.URL_LITERAL) {
                String url = lookahead.sequence
                nextToken()
                ret = MarkDownRender.renderLinkFromUrl(url, arguments(new Arguments()))
            } else {
                throw new ParserException('Missing Page ID', this.tokens, lookahead)
            }
        } else if (lookahead.token == Token.CLOSE_LINK) {
            nextToken()
            ret = '</a>'
        }
        ret
    }

    static class Arguments {
        String style
        String classes
        String format

        enum Alignment {
            LEFT, RIGHT
        }
        Alignment alignment

        @Override
        String toString() {
            String ret = ''
            if (classes) ret += " class=$classes "
            if (style) ret += " style=$style "
            if (alignment) ret += " align=${alignment == Alignment.LEFT?"'left'':''right''}'
            ret
        }

        String printWithoutProperties(List<String> properties) {
            if (!this.style) return toString()
            String style = this.style
            properties.each {
                style = style.replaceAll("$it: .*;', '")
            }
                        String ret = ''
            if (classes) ret += " class=$classes "
            if (style) ret += " style=$style "
            if (alignment) ret += " align=${alignment == Alignment.LEFT?"'left'':''right''}'
            ret
        }
    }

    Arguments arguments(Arguments a) {
        suggesterStatus = SuggesterStatus.SECOND_ARGS

        if (lookahead.token == Token.PEEK_ARG_SEP) {
            nextToken()
            if (lookahead.token == Token.CLASS) {
                nextToken()
                if (lookahead.token == Token.CLASS_LIST) a.classes = lookahead.sequence
                else throw new ParserException('Class List Malformed: ', this.tokens, lookahead)
                nextToken()
                arguments(a)
            } else if (lookahead.token == Token.STYLE) {
                nextToken()
                if (lookahead.token == Token.STYLE_LITERAL) a.style = lookahead.sequence
                else throw new ParserException('Style List Invalid: ', this.tokens, lookahead)
                nextToken()
                arguments(a)
            } else if (lookahead.token == Token.ALIGN_LEFT) {
                nextToken()
                a.alignment = Arguments.Alignment.LEFT
                arguments(a)
            } else if (lookahead.token == Token.ALIGN_RIGHT) {
                nextToken()
                a.alignment = Arguments.Alignment.RIGHT
                arguments(a)
            }
        }
        a
    }

    static class Suggestion {
        String remainingChars
        SuggesterStatus suggesterStatus
        List<Triple<String, String, String>> results

        @Override
        String toString() {
            return 'Suggestion{' +
                    "remainingChars='" + remainingChars + '\'' +
                    ', suggesterStatus=' + suggesterStatus +
                    ', results=' + results +
                    '}'
        }
    }

    Suggestion suggest(String entry) {
        Suggestion s = new Suggestion()
        if (entry == null || entry == '') {
            s.remainingChars = ''
            s.results = suggestFirstExpr('')
        }
        try {
            parse(entry)
        } catch(e) {
        } finally {
            s.remainingChars = entry.substring(lookahead.start)

            s.suggesterStatus = suggesterStatus
            switch (suggesterStatus) {
                case SuggesterStatus.FIRST_EXPR:
                    s.results = suggestFirstExpr(s.remainingChars)
                    break
                case SuggesterStatus.ID_PAGE:
                    s.results = suggestIdPage(s.remainingChars)
                    break
                case SuggesterStatus.ID_IMG:
                    s.results = suggestIdImage(s.remainingChars)
                    break
                case SuggesterStatus.ID_VID:
                    s.results = suggestIdVideo(s.remainingChars)
                    break
                case SuggesterStatus.ID_SLIDESHOW:
                    s.results = suggestIdSlideshow(s.remainingChars)
                    break
                case SuggesterStatus.ID_INSERT:
                    s.results = suggestIdInsert(s.remainingChars)
                    break
                case SuggesterStatus.SECOND_ARGS:
                    s.results = suggestSecondArg(s.remainingChars)
                    break
            }
        }
        s
    }

    enum FirstExpr {
        IMG(Token.IMG, 'Insert an Image'),
        VID(Token.VID, 'Insert a Video'),
        PDF(Token.PDF, 'Insert a PDF'),
        VID_LINK(Token.VID_LINK, 'Insert a Video Link'),
        INSERT_LINK(Token.INSERT_LINK, 'Insert an Insert Link'),
        LINK(Token.LINK, 'Insert a Link'),
        LINK_CLOSE(Token.CLOSE_LINK, 'Insert a Link Close')
        Token token
        String description

        FirstExpr(Token token, String description) {
            this.token = token
            this.description = description
        }
    }

    static List<Triple<String, String, String>> suggestFirstExpr(String seq) {
        List<Triple<String, String, String>> res = []
        for (FirstExpr f : FirstExpr.values()) {
            String tokenString = f.token.toString()
            if (tokenString != seq && (tokenString.startsWith(seq) || seq == '')) {
                String toComplete = tokenString
                Triple<String, String, String> matchedExpr = new Triple<>(
                        toComplete - seq,
                        toComplete,
                        f.description
                )
                res << matchedExpr
            }
        }
        res
    }

    enum SecondaryParameter {
        EMPTY('', '', '', '', 0),
        CLASS('CLASS:', 'Class list', ', ', ' \'\'', 2),
        STYLE('STYLE:', 'Style', ', ', ' \'\'', 2)

        String token
        String description
        String postfix
        String prefix
        int cursorPos

        SecondaryParameter(String token, String description, String prefix, String postfix, int cursorPos) {
            this.token = token
            this.description = description
            this.postfix = postfix
            this.prefix = prefix
            this.cursorPos = cursorPos
        }
    }

    static List<Triple<String, String, String>> suggestSecondArg(String seq) {
        List<Triple<String, String, String>> res = []
        for (SecondaryParameter p : SecondaryParameter.values()) {
            String tokenString = p.token
            if (tokenString.startsWith(seq.trim())) {
                String toComplete = p.prefix + tokenString + p.postfix
                Triple<String, String, String> matchedExpr = new Triple<>(
                        toComplete - seq,
                        toComplete,
                        p.description
                )
                res << matchedExpr
            }
        }
        res
    }

    static List<Triple<String, String, String>> suggestIdPage(String seq) {
        List<Triple<String, String, String>> res = []
        for (CmsPage p : CmsPage.findAll(published: true) as Collection<CmsPage>) {
            String tokenString = "#${p.id}"
            if (tokenString.startsWith(seq)) {
                String toComplete = tokenString
                Triple<String, String, String> matchedExpr = new Triple<>(
                        toComplete - seq,
                        toComplete,
                        p.name
                )
                res << matchedExpr
            }
        }
        res
    }

    static List<Triple<String, String, String>> suggestIdInsert(String seq) {
        List<Triple<String, String, String>> res = []
        for (CmsInsert p : CmsInsert.list() as Collection<CmsInsert>) {
            String tokenString = "#${p.id}"
            if (tokenString.startsWith(seq)) {
                String toComplete = tokenString
                Triple<String, String, String> matchedExpr = new Triple<>(
                        toComplete - seq,
                        toComplete,
                        p.id.toString()
                )
                res << matchedExpr
            }
        }
        res
    }

    static List<Triple<String, String, String>> suggestIdVideo(String seq) {
        List<Triple<String, String, String>> res = []
        for (CmsVideoFile v : CmsVideoFile.findAll(hide: false) as Collection<CmsVideoFile>) {
            String tokenString = "#${v.id}"
            if (tokenString.startsWith(seq)) {
                String toComplete = tokenString
                Triple<String, String, String> matchedExpr = new Triple<>(
                        toComplete - seq,
                        toComplete,
                        MarkDownRender.renderImageOption(v.preview)
                )
                res << matchedExpr
            }
        }
        res
    }

    static List<Triple<String, String, String>> suggestIdSlideshow(String seq) {
        List<Triple<String, String, String>> res = []
        for (CmsPage v : CmsPage.findAll(published: true, pageType: CmsPageType.SLIDESHOW) as Collection<CmsPage>) {
            String tokenString = "#${v.id}"
            if (tokenString.startsWith(seq)) {
                String toComplete = tokenString
                Triple<String, String, String> matchedExpr = new Triple<>(
                        toComplete - seq,
                        toComplete,
                        v.toString()
                )
                res << matchedExpr
            }
        }
        res
    }

    static List<Triple<String, String, String>> suggestIdImage(String seq) {
        List<Triple<String, String, String>> res = []
        for (CmsImage i : CmsImage.findAll(hide: false) as Collection<CmsImage>) {
            String tokenString = "#${i.id}"
            if (tokenString.startsWith(seq)) {
                String toComplete = tokenString
                Triple<String, String, String> matchedExpr = new Triple<>(
                        toComplete - seq,
                        toComplete,
                        MarkDownRender.renderImageOption(i)
                )
                res << matchedExpr
            }
        }
        res
    }
}
