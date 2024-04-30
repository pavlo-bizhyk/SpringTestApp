import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.cs.assignment.controller.UserController;
import org.cs.assignment.model.User;
import org.cs.assignment.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @Test
    public void testGetUsersByDateRange_NoParams_ReturnsAllUsers() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        when(userRepository.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateUser_ValidUser_ReturnsCreated() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        User user = new User(1L, "qqq@gmail.com", "Steve", "Smith", LocalDate.now().minusYears(20));
        user.setBirthDate(LocalDate.now().minusYears(20));

        when(userRepository.saveUser(any(User.class))).thenReturn(user);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()));

        verify(userRepository, times(1)).saveUser(any(User.class));
    }

    @Test
    public void testUpdateSomeUserFields_ValidFields_ReturnsOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        Long userId = 1L;
        User user = new User(userId, "test@example.com", "John", "Doe", LocalDate.now().minusYears(20));
        User updatedUserFields = new User(null, "Jane", null, null);

        when(userRepository.getUserById(userId)).thenReturn(user);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc.perform(patch("/users/{id}", userId)
                        .content(objectMapper.writeValueAsString(updatedUserFields))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.firstName").value(updatedUserFields.getFirstName()));

        verify(userRepository, times(1)).getUserById(userId);
        verify(userRepository, times(1)).saveUser(any(User.class));
    }

    @Test
    public void testUpdateAllUserFields_ValidFields_ReturnsOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        Long userId = 1L;
        User user = new User(userId, "test@example.com", "John", "Doe", LocalDate.now().minusYears(20)); // Existing user
        User updatedUserFields = new User(null, "new@example.com", "Jane", "Smith", LocalDate.now().minusYears(25)); // Updated fields

        when(userRepository.getUserById(userId)).thenReturn(user);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc.perform(put("/users/{id}", userId)
                        .content(objectMapper.writeValueAsString(updatedUserFields))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value(updatedUserFields.getEmail()))
                .andExpect(jsonPath("$.firstName").value(updatedUserFields.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(updatedUserFields.getLastName()));

        verify(userRepository, times(1)).getUserById(userId);
        verify(userRepository, times(1)).saveUser(any(User.class));
    }

    @Test
    public void testDeleteUser_ValidId_ReturnsNoContent() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        Long userId = 1L;

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNoContent());

        verify(userRepository, times(1)).deleteUserById(userId);
    }
}