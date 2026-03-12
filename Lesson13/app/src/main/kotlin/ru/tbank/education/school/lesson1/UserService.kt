import org.springframework.stereotype.Service

@Service
class UserService(private val config: ServiceConfig) {
    private val users = mutableMapOf<Int, User>()
    private var nextId = 1

    fun create(user: User): User? { ... }
    fun read(id: Int): User? = users[id]
    fun update(id: Int, user: User): User? { ... }
    fun delete(id: Int): Boolean = users.remove(id) != null
    fun list(): List<User> = users.values.toList()
}