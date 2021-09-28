package spring.jwtsecurity.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import spring.jwtsecurity.dto.JwtDto;
import spring.jwtsecurity.dto.LoginUser;
import spring.jwtsecurity.dto.NewUser;
import spring.jwtsecurity.entity.Rol;
import spring.jwtsecurity.entity.User;
import spring.jwtsecurity.enums.RolName;
import spring.jwtsecurity.jwt.JwtProvider;
import spring.jwtsecurity.service.RolService;
import spring.jwtsecurity.service.UserService;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    RolService rolService;

    @Autowired
    JwtProvider jwtProvider;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/new")
    public ResponseEntity<?> newUser(@RequestBody NewUser newUser, BindingResult bindingResult) {

        if (bindingResult.hasErrors())
            return new ResponseEntity<>("Invalid fields.", HttpStatus.BAD_REQUEST);

        if (userService.existsByUsername(newUser.getUsername()))
            return new ResponseEntity<>("This username already exists.", HttpStatus.BAD_REQUEST);

        if (newUser.getUsername() == null || newUser.getUsername() == "")
            return new ResponseEntity<>("User must have username.", HttpStatus.BAD_REQUEST);

        if (newUser.getPassword() == null || newUser.getPassword() == "")
            return new ResponseEntity<>("User must have password.", HttpStatus.BAD_REQUEST);

        User user = new User(newUser.getUsername(), passwordEncoder.encode(newUser.getPassword()));
        Set<Rol> roles = new HashSet<>();
        roles.add(rolService.getByRolName(RolName.ROLE_USER).get());
        if (newUser.getRoles().contains("admin"))
            roles.add(rolService.getByRolName(RolName.ROLE_ADMIN).get());
        user.setRoles(roles);
        userService.save(user);

        return new ResponseEntity<>("User created!", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUser loginUser, BindingResult bindingResult) {

        if (bindingResult.hasErrors())
            return new ResponseEntity<>("Invalid fields.", HttpStatus.BAD_REQUEST); // Return message

        Authentication authentication = authenticationManager.
                authenticate(new UsernamePasswordAuthenticationToken(loginUser.getUsername(),
                                                                     loginUser.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        JwtDto jwtDto = new JwtDto(jwt, userDetails.getUsername(), userDetails.getAuthorities());

        return new ResponseEntity<>(jwtDto, HttpStatus.OK); // Return JwtDto

    }
    
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/listAll")
	public ResponseEntity<List<User>> list(){
		
		List<User> list = userService.list();
		
		return new ResponseEntity<List<User>>(list, HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ADMIN') ||  hasRole('SUPERUSER')")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") int id){
						
		userService.deleteById(id);
		
		return new ResponseEntity<>("User deleted succesfully", HttpStatus.OK);
	}
}
