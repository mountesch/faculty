import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "service")
class ServiceConfig {
    var maxRecords: Int = 10
    var bannedNames: List<String> = emptyList()
    var allowedCategories: List<String> = emptyList()
}