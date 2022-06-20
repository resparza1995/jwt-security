package spring.jwtsecurity.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import spring.jwtsecurity.entity.User;
import spring.jwtsecurity.repository.UserRepository;

@Service
@Transactional
public class UserService {

    @Autowired
    UserRepository userRepository;

	public List<User> list(){
		return userRepository.findAll();
	}
	
    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }
	
    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public void save(User user) {
        userRepository.save(user);
    }
    
	public void deleteById(int id) {
		userRepository.deleteById(id);
	}
}
