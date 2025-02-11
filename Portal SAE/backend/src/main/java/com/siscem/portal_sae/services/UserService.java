package com.siscem.portal_sae.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.siscem.portal_sae.dtos.user.UserByRoleDTO;
import com.siscem.portal_sae.dtos.user.UserLoginDTO;
import com.siscem.portal_sae.dtos.user.UsersDTO;
import com.siscem.portal_sae.models.User;
import com.siscem.portal_sae.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	JwtService jwtService;
	
	/**
	 * Retrieves all users and converts them into UsersDTO objects.
	 * 
	 * @return ResponseEntity<List<UsersDTO>> An HTTP response containing a list of
	 *         UsersDTO objects representing all users. Returns ResponseEntity with
	 *         status OK and a list of UsersDTO if users are found.
	 */
	public ResponseEntity<List<UsersDTO>> getAllUsers() {
		ModelMapper modelMapper = new ModelMapper();
		List<User> users = userRepository.findAll();
		List<UsersDTO> usersDTO = users.stream().map(user -> modelMapper.map(user, UsersDTO.class))
				.collect(Collectors.toList());

		return ResponseEntity.ok(usersDTO);
	}

	/**
	 * Retrieves users with a specific role and converts them into UserByRoleDTO
	 * objects.
	 * 
	 * @param roleName The name of the role for which users are to be retrieved.
	 * @return ResponseEntity<List<UserByRoleDTO>> An HTTP response containing a
	 *         list of UserByRoleDTO objects representing users with the specified
	 *         role. Returns ResponseEntity with status OK and a list of
	 *         UserByRoleDTO if users with the specified role are found. Returns
	 *         ResponseEntity with status NOT_FOUND if no users with the specified
	 *         role are found.
	 */
	public ResponseEntity<List<UserByRoleDTO>> getUsersByRole(String roleName) {
		ModelMapper modelMapper = new ModelMapper();
		List<User> users = userRepository.findByRolNombreRol(roleName);

		if (users.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		List<UserByRoleDTO> usersDTO = users.stream().map(user -> modelMapper.map(user, UserByRoleDTO.class))
				.collect(Collectors.toList());

		return ResponseEntity.ok(usersDTO);
	}

	/**
	 * Logs in a user with the provided username and password and returns user
	 * details in a UserLoginDTO object.
	 * 
	 * @param username The username of the user to be logged in.
	 * @param password The password of the user to be logged in.
	 * @return ResponseEntity<String> An HTTP response containing a JWT token if a
	 *         user with the provided credentials is found. Returns ResponseEntity
	 *         with status OK and a JWT token if a user with the provided
	 *         credentials is found. Returns ResponseEntity with status NOT_FOUND
	 *         if no user with the provided credentials is found.
	 */
	public ResponseEntity<String> loginUser(String username, String password) {
		ModelMapper modelMapper = new ModelMapper();
		Optional<User> userOptional = userRepository.findByNombreAndContrasena(username, password);
		UserLoginDTO userLoginDTO = userOptional.map(user -> modelMapper.map(user, UserLoginDTO.class)).orElse(null);
		if (userLoginDTO != null) {
			return ResponseEntity.ok(generateJWT(userLoginDTO));
		} else {
			return ResponseEntity.status(404).body("Usuario no encontrado");
		}
	}

	
	/**
	 * Generates a JWT token for the given user login details.
	 * 
	 * @param userLoginDTO The UserLoginDTO containing user details.
	 * @return String The JWT token generated for the user.
	 */
	public String generateJWT(UserLoginDTO userLoginDTO) {
	    return jwtService.getToken(userLoginDTO);
	}

}
