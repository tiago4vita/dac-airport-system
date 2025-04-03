

@RestController
@RequestMapping("/api/auth")
class AuthController(){


    @GetMapping("/{id}")
    fun getUser(@PathVariable id:Long){

    }

    @GetMapping
    fun getAllUsers(){

    }

    @PostMapping
    fun createUser(@RequestBody userRequest : UserRequest){

    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id:Long,@RequestBody userRequest : UserRequest){

    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id:Long){

    }
}