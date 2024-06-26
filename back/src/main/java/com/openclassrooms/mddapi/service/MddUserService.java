package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.exceptions.BadRequestExceptionHandler;
import com.openclassrooms.mddapi.exceptions.NotFoundExceptionHandler;
import com.openclassrooms.mddapi.model.MddUser;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.repository.MddUserRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing MddUser entities.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class MddUserService implements UserDetailsService {

    MddUserRepository mddUserRepository;

    /**
     * Finds all MddUsers in the database.
     *
     * @return a list of all MddUsers.
     */
    public List<MddUser> findAllUsers() {
        try {
            log.info("findAllUsers");
            List<MddUser> userList = new ArrayList<>();
            mddUserRepository.findAll().forEach(user -> userList.add(user));
            return userList;
        } catch (Exception e) {
            log.error("We could not find all users: " + e.getMessage());
            throw new NotFoundExceptionHandler("We could not find any users");
        }
    }

    /**
     * Finds a MddUser in the database by ID.
     *
     * @param id the ID of the MddUser to find.
     * @return the MddUser with the given ID, if it exists.
     */
    public MddUser findUserById(Long id) {
        try {
            log.info("findUserById - id: " + id);
            return mddUserRepository.findById(id)
                    .orElseThrow(() -> new NotFoundExceptionHandler("User not found"));
        } catch (Exception e) {
            log.error("We could not find user: " + id, e.getMessage());
            throw new NotFoundExceptionHandler("We could not find your user");
        }
    }

    /**
     * Finds a MddUser in the database by username.
     *
     * @param username the username of the MddUser to find.
     * @return the MddUser with the given username, if it exists.
     */
    public MddUser findUserByUsername(String username) {
        try {
            log.info("findUserByUsername - username: " + username);
            return mddUserRepository.findByUsername(username)
                    .orElseThrow(() -> new NotFoundExceptionHandler("User not found"));
        } catch (Exception e) {
            log.error("We could not find user: " + username, e.getMessage());
            throw new NotFoundExceptionHandler("We could not find your user");
        }
    }

    /**
     * Finds a MddUser in the database by email.
     *
     * @param mail the email of the MddUser to find.
     * @return the MddUser with the given email, if it exists.
     */
    public MddUser findUserByEmail(String mail) {
        try {
            log.info("findUserByEmail - email: " + mail);
            return mddUserRepository.findByEmail(mail)
                    .orElseThrow(() -> new NotFoundExceptionHandler("User not found"));
        } catch (Exception e) {
            log.error("We could not find user: " + mail, e.getMessage());
            throw new NotFoundExceptionHandler("We could not find your user");
        }
    }

    /**
     * Creates a new MddUser in the database.
     *
     * @param user the MddUser to create.
     * @return the created MddUser.
     */
    public MddUser createUser(MddUser user) {

        mddUserRepository.findByEmail(user.getEmail()).ifPresent(
                val -> {
                    log.warn("User with this email alreay exists");
                    throw  new BadRequestExceptionHandler("User with this email already exists");
                }
        );

        try {
            log.info("createUser");
            user.setId(null);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            return mddUserRepository.save(user);
        } catch (Exception e) {
            log.error("Failed to create user: ", e.getMessage());
            throw new BadRequestExceptionHandler("Failed to create user : " + e.getMessage());
        }
    }

    /**
     * Updates an existing MddUser in the database.
     *
     * @param user the MddUser to update.
     * @return the updated MddUser.
     */
    public MddUser updateUser(MddUser user) {
        try {
            log.info("updateUser - id: " + user.getId());
            MddUser existingUser = mddUserRepository.findById(user.getId())
                    .orElseThrow(() -> new NotFoundExceptionHandler("User not found"));

            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getEmail());
            existingUser.setPassword(user.getPassword());
            existingUser.setComments(user.getComments());
            existingUser.setPosts(user.getPosts());

            mddUserRepository.save(existingUser);
            return existingUser;
        } catch (Exception e) {
            log.error("Failed to update user: ", e.getMessage());
            throw new BadRequestExceptionHandler("Failed to update user");
        }
    }

    /**
     * Deletes a MddUser from the database by ID.
     *
     * @param id the ID of the MddUser to delete.
     * @return a message indicating that the user was deleted.
     */
    public String deleteUser(Long id) {
        try {
            log.info("deleteUser - id: " + id);
            MddUser user = mddUserRepository.findById(id)
                    .orElseThrow(() -> new NotFoundExceptionHandler("User not found"));

            mddUserRepository.delete(user);
            return "User deleted";
        } catch (Exception e) {
            log.error("Failed to delete user: ", e.getMessage());
            throw new BadRequestExceptionHandler("Failed to delete user");
        }
    }

    /**
     * Loads a MddUser from the database by email.
     *
     * @param mail the email of the MddUser to load.
     * @return the UserDetails of the loaded MddUser.
     * @throws UsernameNotFoundException if no user with the given email exists.
     */
    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        MddUser user = mddUserRepository.findByEmail(mail).orElseThrow(() -> new UsernameNotFoundException("No user with this email address"));
        List<SimpleGrantedAuthority> authi = new ArrayList<>();
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authi);
    }
}