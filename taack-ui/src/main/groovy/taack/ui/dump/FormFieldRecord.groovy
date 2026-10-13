package taack.ui.dump

import grails.util.Holders
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom

//The request params a rendered form is allowed to bind onto one entity, signed so the client cannot widen them.
@Slf4j
@CompileStatic
final class FormFieldRecord {
    static final String PARAM = 'taackRecordedFormFields'
    static final String SIGNING_KEY_CONFIG = 'taack.formSigningKey'

    private static final String ALGORITHM = 'HmacSHA256'
    private static final int RANDOM_KEY_BYTES = 32
    private static final String LINE_SEPARATOR = '\n'
    private static final String TOKEN_SEPARATOR = '.'
    private static final String ROOT_PROPERTY_DELIMITERS = '.['
    private static final int HEADER_LINES = 2
    private static byte[] signingKey

    final String className
    final String id
    final Set<String> fields

    FormFieldRecord(String className, String id, Set<String> fields) {
        this.className = className
        this.id = id ?: ''
        this.fields = new LinkedHashSet<String>(fields ?: Collections.<String> emptySet()).asImmutable()
    }

    String sign() {
        byte[] payload = ([className, id] + (fields as List<String>)).join(LINE_SEPARATOR).getBytes(StandardCharsets.UTF_8)
        encode(payload) + TOKEN_SEPARATOR + encode(mac(payload))
    }

    //A new entity accepts a token issued for any id of its class, so a form rendered for an existing entity can be saved as a copy.
    static FormFieldRecord verify(String token, String expectedClassName, String expectedId) {
        if (!token) return null
        String[] parts = token.split('\\' + TOKEN_SEPARATOR, -1)
        if (parts.length != 2) return null

        byte[] payload
        byte[] signature
        try {
            payload = Base64.urlDecoder.decode(parts[0])
            signature = Base64.urlDecoder.decode(parts[1])
        } catch (IllegalArgumentException ignored) {
            return null
        }
        if (!MessageDigest.isEqual(mac(payload), signature)) return null

        List<String> lines = new String(payload, StandardCharsets.UTF_8).split(LINE_SEPARATOR, -1).toList()
        if (lines.size() < HEADER_LINES) return null
        FormFieldRecord record = new FormFieldRecord(lines[0], lines[1], lines.drop(HEADER_LINES).findAll() as LinkedHashSet<String>)
        record.className == expectedClassName && (!expectedId || record.id == expectedId) ? record : null
    }

    /**
     * A recorded field allows its own key and its nested keys ({@code owner.id}, {@code tags[0].id}).
     * A recorded map entry allows only that entry, not the rest of the map.
     */
    boolean allows(String paramKey) {
        fields.any { String field -> paramKey == field || paramKey == field + '.id' || paramKey.startsWith(field + '[') }
    }

    List<String> getBindableProperties() {
        fields.collect { String field -> field.tokenize(ROOT_PROPERTY_DELIMITERS).first() }.unique()
    }

    /**
     * The allowed subset of request params. Grails also nests dotted params ({@code attributes.color} under
     * {@code attributes}) and binds from that nested map, so a nested map is kept trimmed to its allowed entries.
     */
    Map<String, Object> bindableParams(Map<?, ?> params) {
        allowedEntries(params, '')
    }

    private Map<String, Object> allowedEntries(Map<?, ?> params, String keyPrefix) {
        Map<String, Object> bindable = [:]
        for (Map.Entry<?, ?> param in params.entrySet()) {
            String key = keyPrefix + param.key
            if (param.value instanceof Map) {
                Map<String, Object> nested = allowedEntries(param.value as Map, key + '.')
                if (nested) bindable.put(param.key.toString(), nested)
            } else if (allows(key)) {
                bindable.put(param.key.toString(), param.value)
            }
        }
        bindable
    }

    private static String encode(byte[] bytes) {
        Base64.urlEncoder.withoutPadding().encodeToString(bytes)
    }

    private static byte[] mac(byte[] payload) {
        Mac mac = Mac.getInstance(ALGORITHM)
        mac.init(new SecretKeySpec(getSigningKey(), ALGORITHM))
        mac.doFinal(payload)
    }

    private static synchronized byte[] getSigningKey() {
        if (signingKey == null) {
            String configured = Holders.config?.getProperty(SIGNING_KEY_CONFIG, String)
            signingKey = configured ? configured.getBytes(StandardCharsets.UTF_8) : randomKey()
        }
        signingKey
    }

    private static byte[] randomKey() {
        log.warn("${SIGNING_KEY_CONFIG} is not configured, signing forms with a random key: forms rendered before a restart or by another node will be rejected on save")
        byte[] key = new byte[RANDOM_KEY_BYTES]
        new SecureRandom().nextBytes(key)
        key
    }
}
