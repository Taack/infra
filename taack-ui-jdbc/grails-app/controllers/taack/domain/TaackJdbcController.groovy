package taack.domain

import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.annotation.Secured

/**
 * Support Controller that communicate with the JDBC Driver. Tested for Libreoffice and Intellij clients.
 */
@GrailsCompileStatic
@Secured(['ROLE_ADMIN', 'ROLE_JDBC_ADMIN'])
class TaackJdbcController {

    TaackJdbcService taackJdbcService

    def initConn() {
        render 'OK'
    }

    def query(String req, Integer offset, Integer maxRow) {
        if (!req.contains(';')) {
            log.info "Adding ';' on query ..."
            req += ';'
        }
        byte[] b
        try {
            b = taackJdbcService.getBufFromTql(req, maxRow ?: 2, offset ?: 0)
        } catch(e) {
            log.error("Bad SQL: $req , ${e.message}")
            e.printStackTrace()
            b = taackJdbcService.getPingMessage()
            println b.length
        }
        response.setHeader("Content-disposition", "attachment; filename=buf.protobuf")
        response.contentType = 'application/protobuf'
        response.outputStream << b
        return false
    }

    def tables(String schemaPattern, String tableNamePattern) {
        response.setHeader("Content-disposition", "attachment; filename=buf.protobuf")
        response.contentType = 'application/protobuf'
        response.outputStream << taackJdbcService.getProtoTables(schemaPattern, tableNamePattern)
        return false
    }

    def columns(String schemaPattern, String tableNamePattern, String columnNamePattern) {
        response.setHeader("Content-disposition", "attachment; filename=buf.protobuf")
        response.contentType = 'application/protobuf'
        response.outputStream << taackJdbcService.getProtoColumns(null, schemaPattern, tableNamePattern, columnNamePattern)
        return false
    }

    def pk(String tableName) {
        response.setHeader("Content-disposition", "attachment; filename=buf.protobuf")
        response.contentType = 'application/protobuf'
        response.outputStream << taackJdbcService.getPrimaryKey(tableName)
        return false
    }

    def indexInfo(String tableName) {
        response.setHeader("Content-disposition", "attachment; filename=buf.protobuf")
        response.contentType = 'application/protobuf'
        response.outputStream << taackJdbcService.getIndexInfo(tableName)
        return false
    }

    def columnsMetaData(String tableName) {
        response.setHeader("Content-disposition", "attachment; filename=buf.protobuf")
        response.contentType = 'application/protobuf'
        if (tableName == '%') response.outputStream << taackJdbcService.getProtoColumns('default', 'public', '%', '%')
        else response.outputStream << taackJdbcService.getProtoColumns('default', 'public', tableName, '%')
        //else response.outputStream << taackJdbcService.getColumnRSMetaData(tableName)
        return false
    }

    def indexInfoMetaData(String tableName) {
        response.setHeader("Content-disposition", "attachment; filename=buf.protobuf")
        response.contentType = 'application/protobuf'
        response.outputStream << taackJdbcService.getIndexInfoRSMetaData(tableName)
        return false
    }
}
