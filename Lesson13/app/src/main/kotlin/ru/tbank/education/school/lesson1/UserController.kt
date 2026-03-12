import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(private val service: UserService) {
    @PostMapping fun create(@RequestBody user: User): User? = service.create(user)
    @GetMapping("/{id}") fun read(@PathVariable id: Int): User? = service.read(id)
    @PutMapping("/{id}") fun update(@PathVariable id: Int, @RequestBody user: User): User? = service.update(id, user)
    @DeleteMapping("/{id}") fun delete(@PathVariable id: Int): Boolean = service.delete(id)
    @GetMapping fun list(): List<User> = service.list()
}