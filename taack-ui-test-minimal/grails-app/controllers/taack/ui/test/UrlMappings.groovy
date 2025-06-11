package taack.ui.test

class UrlMappings {
    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(controller:"root")
        "500"(view:'/error')
        "404"(view:'/notFound')

    }
}
