package cms.dsl

enum MediaStyleKind {
    VIDEO,
    IMAGE,
    FILE_PREVIEW
}

enum MediaStyle {
    SMALL_VIDEO('smallVideo', 640, 0, MediaStyleKind.VIDEO, ' -vf scale=640:trunc(ow/a/2)*2 ', 's'),
    MEDIUM_VIDEO('mediumVideo', 1280, 0, MediaStyleKind.VIDEO, ' -vf scale=1280:trunc(ow/a/2)*2 ', 'm'),
    LARGE_VIDEO('largeVideo', 1920, 0, MediaStyleKind.VIDEO, ' -vf scale=1920:trunc(ow/a/2)*2 ', 'l'),
    SMALL_IMAGE('smallImage', 640, 0, MediaStyleKind.IMAGE, '-resize 640x600', 's'),
    MEDIUM_IMAGE('mediumImage', 1280, 0, MediaStyleKind.IMAGE, '-resize 1280x800', 'm'),
    LARGE_IMAGE('largeImage', 1920, 0, MediaStyleKind.IMAGE, '-resize 1920x1200', 'l')

    MediaStyle(String name, int width, int height, MediaStyleKind kind, String arguments, String suffix) {
        this.name = name
        this.width = width
        this.height = height
        this.kind = kind
        this.arguments = arguments
        this.suffix = suffix
    }
    final String name

    final int width
    final int height

    final MediaStyleKind kind

    final String arguments
    final String suffix
}
