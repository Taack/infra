package cms.dsl.parser.exception

class WrongDataException extends Exception {
    String id

    WrongDataException(String message, String id) {
        super(message)
        this.id = id
    }
}
