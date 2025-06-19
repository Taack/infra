package cms


import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class PublisherService {

//    ContentProtoGeneratorService contentProtoGeneratorService
//    ItemProtoGeneratorService itemProtoGeneratorService
//    BpReportService bpReportService
//
//    @Value('${publish.proto.directory}')
//    String publishProtoDirectory
//
//    final String outputDir = '/home/auo/pdfs/bp/'

//    private File generateProto(String brand) {
//        CmsContent.Structure content = contentProtoGeneratorService.generateProto(brand)
//        File proto = new File("${publishProtoDirectory}/CmsStructure-${brand}-${System.currentTimeMillis()}.proto")
//        FileOutputStream fout = new FileOutputStream(proto)
//        content.writeTo(fout)
//        proto
//    }

//    private File generateBpProto(String brand) {
//        CmsItem.Bucket content = itemProtoGeneratorService.generateProto(brand)
//        File proto = new File("${publishProtoDirectory}/Cms-${brand}-${System.currentTimeMillis()}.proto")
//        FileOutputStream fout = new FileOutputStream(proto)
//        content.writeTo(fout)
//        proto
//    }

//    private boolean copyFile(String brand, File file) {
//        CmsBrand cmsBrand = CmsBrand.getCmsBrandByName(brand)
//        Process p = "scp ${file.absolutePath} ${cmsBrand.url}:${cmsBrand.homeDir}CmsStructure.${brand}.protobuf".execute()
//        def output = new StringWriter(), error = new StringWriter()
//        p.waitForProcessOutput(output, error)
//        if (p.exitValue() != 0) {
//            log.warn "copyFile exit value: ${p.exitValue()}"
//            log.warn "copyFile err out: ${error}"
//        }
//        log.info "${output}"
//
//        p.exitValue() == 0
//    }

//    private boolean copyBpFile(String brand, File file) {
//        CmsBrand cmsBrand = CmsBrand.getCmsBrandByName(brand)
//        Process p = "scp ${file.absolutePath} ${cmsBrand.url}:${cmsBrand.homeDir}Cms.${cmsBrand.bpBrandName}.protobuf".execute()
//        def output = new StringWriter(), error = new StringWriter()
//        p.waitForProcessOutput(output, error)
//        if (p.exitValue() != 0) {
//            log.warn "copyBpFile exit value: ${p.exitValue()}"
//            log.warn "copyBpFile err out: ${error}"
//        }
//        log.info "${output}"
//
//        p.exitValue() == 0
//    }

//    private boolean reloadCmsContent(String brand) {
//        String uri
//        CredentialsProvider provider = null
//
//        switch (brand) {
//            case '2cp':
//                uri = 'citel.fr/refreshCmsContent'
//                break
//            case 'Inc':
//                uri = 'citel.us/refreshCmsContent'
////                provider = new BasicCredentialsProvider()
////                UsernamePasswordCredentials credentials = new UsernamePasswordCredentials('auo', '!23AuOOO')
////                provider.setCredentials(AuthScope.ANY, credentials)
//                break
//            case 'shanghai':
//                uri = 'citel.cn/refreshCmsContent'
//                provider = new BasicCredentialsProvider()
//                UsernamePasswordCredentials credentials = new UsernamePasswordCredentials('auo', '123auo32')
//                provider.setCredentials(AuthScope.ANY, credentials)
//                break
//            case 'obsta':
//                uri = 'obsta.com/refreshCmsContent'
//                break
//            case 'gmbh':
//                uri = 'citel.de/refreshCmsContent'
//                break
//            case 'india':
//                uri = 'citel.in/refreshCmsContent'
//                break
//            case 'ooo':
//                uri = 'citel.ru/refreshCmsContent'
//                break
//            default:
//                return false
//        }
//        final String random = 'QERGoij2345aùtqsdfgZ54qrfg'
//
//        final HttpPost post = new HttpPost('https://' + uri)
////        final HttpPost post = new HttpPost('http://localhost:9442/refreshCmsContent')
//        String salt = new Date().toString()
//        String key = (random + salt).sha256()
//
//        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create()
//        entityBuilder.addTextBody('salt', salt).addTextBody('key', key)
//        final HttpEntity entity = entityBuilder.build()
//        post.setEntity(entity)
//        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create()
//        if (provider) {
//
////            httpClientBuilder.setDefaultCredentialsProvider(provider)
//        }
//        final CloseableHttpClient httpClient = httpClientBuilder.build()
//        final CloseableHttpResponse response = httpClient.execute(post)
//
//        final int statusCode = response.statusLine.statusCode
//        if (statusCode == 200) {
//            return true
//        } else {
//            log.error "HttpPost fail with code: ${statusCode}"
//            return false
//        }
//    }

//    private boolean reloadService(String brand) {
//        CmsBrand cmsBrand = CmsBrand.getCmsBrandByName(brand)
//        log.info "restart ${cmsBrand.url}"
//        Process p = new ProcessBuilder('/usr/bin/ssh',
//                "${cmsBrand.url}', '-C",
//                "sudo systemctl restart ${cmsBrand.serviceName}").start()
//        def output = new StringWriter(), error = new StringWriter()
//        p.waitForProcessOutput(output, error)
//
//        if (p.exitValue() != 0) {
//            log.warn "reloadService exit value: ${p.exitValue()}"
//            log.warn "reloadService err out: ${error}"
//        }
//        log.info "${output}"
//    }

//    private boolean rsyncFiles(String brand) {
//        CmsBrand cmsBrand = CmsBrand.getCmsBrandByName(brand)
//        log.info "rsyncFiles ${cmsBrand.url}"
//        Process p = new ProcessBuilder('/usr/bin/ssh',
//                "${cmsBrand.url}', '-C",
//                'rsync -avz -e ssh auo@intranet.citel.fr:/home/auo/f .').start()
//        def output = new StringWriter(), error = new StringWriter()
//        p.waitForProcessOutput(output, error)
//
//        if (p.exitValue() != 0) {
//            log.warn "reloadService exit value: ${p.exitValue()}"
//            log.warn "reloadService err out: ${error}"
//            return false
//        }
//        log.info "${output}"
//        return true
//    }

//    private boolean rsyncCmsFiles(String brand) {
//        CmsBrand cmsBrand = CmsBrand.getCmsBrandByName(brand)
//        log.info "rsyncCmsFiles ${cmsBrand.url}"
//        Process p = new ProcessBuilder('/usr/bin/ssh',
//                "${cmsBrand.url}', '-C",
//                "rsync -avz -e ssh auo@intranet.citel.fr:/home/auo/CMS/TaackCMS/originalContent ${cmsBrand.homeDir}CMS/TaackCMS/").start()
//        def output = new StringWriter(), error = new StringWriter()
//        p.waitForProcessOutput(output, error)
//
//        if (p.exitValue() != 0) {
//            log.warn "reloadService exit value: ${p.exitValue()}"
//            log.warn "reloadService err out: ${error}"
//            return false
//        }
//        log.info "${output}"
//        return true
//    }

//    private boolean rsyncPdfs(String brand) {
//        CmsBrand cmsBrand = CmsBrand.getCmsBrandByName(brand)
//        log.info "rsyncPdfs ${cmsBrand.url}"
//        Process p = new ProcessBuilder('/usr/bin/ssh',
//                "${cmsBrand.url}', '-C",
//                'rsync -avz -e ssh auo@intranet.citel.fr:/home/auo/pdfs .').start()
//        def output = new StringWriter(), error = new StringWriter()
//        p.waitForProcessOutput(output, error)
//
//        if (p.exitValue() != 0) {
//            log.warn "reloadService exit value: ${p.exitValue()}"
//            log.warn "reloadService err out: ${error}"
//            return false
//        }
//        log.info "${output}"
//        return true
//    }

//    private boolean producePdfs(String brand) {
//        CmsBrand cmsBrand = CmsBrand.getCmsBrandByName(brand)
//        log.info "generate PDFs for $brand +++ ${cmsBrand.brandMandatoryValueId.mandatoryValuesOr}"
//
//        List<specification.Value> valuesOr = specification.Value.findAllByIdInList(cmsBrand.brandMandatoryValueId.mandatoryValuesOr)
//        List<specification.Value> valuesAnd = specification.Value.findAllByIdInList(cmsBrand.brandMandatoryValueId.mandatoryValuesAnd)
//
//        List<Long> itemIdsAnd = (valuesAnd.items.flatten() as List<Item>)*.id.unique() as List<Long>
//        List<Long> itemIdsOr = (valuesOr.items.flatten() as List<Item>)*.id.unique() as List<Long>
//
//        List<Long> itemIds = itemIdsAnd.intersect(itemIdsOr) as List<Long>
//
//        List<Item> items = itemIds.collect { Item.get(it) }.findAll { Item item ->
//            (item as Item).range != null && (item as Item).ref != null &&
//                    (item.restrictedPublishedOrigins.size() == 0 || item.restrictedPublishedOrigins.contains(brand))
//        }
//
//        File dir = new File(outputDir)
//        dir.mkdirs()
//        ObjectOutputStream status = new ObjectOutputStream(new FileOutputStream(outputDir + 'status_' + brand))
//        status.writeObject(cmsBrand.languages)
//
//        items.each { Item item ->
//            status.writeObject(item.id)
//            cmsBrand.languages.each { String l ->
//                File f = bpReportService.buildReport(item, l, brand)
//                status.writeObject(f.path)
//            }
//        }
//
//        log.info "generate PDFs for $brand End ---"
//        true
//    }

//    boolean publishCmsContent(String brand) {
//        File proto = generateProto(brand)
//        if (!proto) return false
//        log.info 'publishCmsContent proto'
//        if (!copyFile(brand, proto)) return false
//        log.info 'publishCmsContent copy proto'
//        if (!rsyncCmsFiles(brand)) return false
//        log.info 'publishCmsContent rsync OK'
//        reloadCmsContent(brand)
//    }
//
//    boolean publishBpContent(String brand) {
//        File proto = generateBpProto(brand)
//        if (!proto) return false
//        log.info 'publishBpContent proto'
//        if (!copyBpFile(brand, proto)) return false
//        log.info 'publishBpContent copy proto'
//        if (!rsyncFiles(brand)) return false
//        log.info 'publishBpContent rsync OK'
//        if (!rsyncCmsFiles(brand)) return false
//        log.info 'publishBpContent rsync CMS OK'
//        reloadService(brand)
//    }
//
//    boolean restartTomcat(String brand) {
//        reloadService(brand)
//    }
//
//    boolean updatePdfs(String brand) {
//        if (!producePdfs(brand)) return false
//        rsyncPdfs(brand)
//    }
}
