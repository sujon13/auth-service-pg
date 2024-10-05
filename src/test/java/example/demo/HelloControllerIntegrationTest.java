package example.demo;


import com.fasterxml.jackson.databind.ObjectMapper;
import example.demo.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class HelloControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;  // Jackson ObjectMapper


    @Test
    public void testGetUserById_Success() throws Exception {
        // Assuming a user with ID 1 exists in the database
        mockMvc.perform(get("/users")
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[1].userId").value(2))
                .andExpect(jsonPath("$[2].userId").value(3))
                .andExpect(jsonPath("$[0].name").value("name1"))
                .andExpect(jsonPath("$[1].name").value("name2"))
                .andExpect(jsonPath("$[2].name").value("name3"));
    }

    @Test
    public void testCreateUser_Success() throws Exception {
        User user = new User(null, "name4", 4);
        String newUserJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                        .with(httpBasic("user", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("name4"))
                .andExpect(jsonPath("$.userId").value(4));
                //.andExpect(jsonPath("$.id").value(1));

        //Thread.sleep(600000);
    }
}
