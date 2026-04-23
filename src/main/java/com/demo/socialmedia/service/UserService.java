package com.demo.socialmedia.service;

import com.demo.socialmedia.dao.UserDAO;
import com.demo.socialmedia.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến User.
 */
@Service
public class UserService   {

    @Autowired
    private UserDAO userDAO;

    private PasswordEncoder passwordEncoder ;

    // Đăng ký tài khoản
    public User register(String username, String password) {
        // Kiểm tra username đã tồn tại chưa
        if (userDAO.findByUsername(username) != null) {
            return null; // Username đã tồn tại
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        userDAO.insert(user);
        return userDAO.findByUsername(username);
    }

    // Đăng nhập
    public User login(String username) {
        return userDAO.findByUsername(username);
    }

    // Lấy thông tin user theo ID
    public User getUserById(int id) {
        return userDAO.findById(id);
    }

    public User getUserByUsername(String username){
        return userDAO.findByUsername(username);
    }

    // Lấy tất cả user
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }


}
