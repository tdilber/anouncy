package com.beyt.anouncy.user.controller;

import com.beyt.anouncy.user.TestUtil;
import com.beyt.anouncy.user.UserApplication;
import com.beyt.anouncy.user.dto.UserSignUpDTO;
import com.beyt.anouncy.user.entity.Configuration;
import com.beyt.anouncy.user.repository.ConfigurationRepository;
import com.beyt.anouncy.user.repository.UserRepository;
import com.beyt.anouncy.user.service.ConfigurationService;
import com.beyt.anouncy.user.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = UserApplication.class)
@AutoConfigureMockMvc
class UserControllerTestIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    void beforeAll() {
        configurationRepository.save(new Configuration("anouncy.jwtToken.secret", "asdasdasdasdasd';laskjdf;asdasdasdasdlkjaslkjhasdkljhalksdjhfl;lfasd;lkajsdflkjasd;lfkjaasdasasdfasdf2"));
        configurationRepository.save(new Configuration("anouncy.password.salt.user", "asdasdasdasdasd';laskjdf;lkasd;lfkas'ldkf';lasdkf;lkasd;lkajsdflkjasd;lfkja;lsdjkfasdasdasdsdafasdfasdf2"));
        configurationRepository.save(new Configuration("anouncy.password.salt.anonymous", "asdasdasasdfasdfasdflaksdjflkasjhdflkjhasdkljhlaksjdhflkjashdfld3"));
        configurationRepository.save(new Configuration("anouncy.password.salt.session", "asdasdaiouasdpfoiuaspodifuapsoidufpoaisudfipoausdpofiuaspoidufoaiusdpfoiuasd4"));
        configurationRepository.flush();
        configurationService.fetchAllConfigurations();
    }

    @Test
    void signIn() throws Exception {
    }

    @Test
    void signUp() throws Exception {
        UserSignUpDTO validUser = new UserSignUpDTO();

        validUser.setUsername("test-user");
        validUser.setPassword("password");
        validUser.setFirstName("Talha");
        validUser.setLastName("Dilber");
        validUser.setEmail("test@anouncy.com");
        validUser.setImageUrl("imageUrl");
        validUser.setLangKey("tr");
        assertThat(userRepository.findByUsername("test-user")).isEmpty();

        MvcResult mvcResult = mockMvc
                .perform(post("/user/sign-up").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(validUser)))
                .andExpect(status().isOk()).andReturn();

        UserService.UserJwtResponse resultValue = TestUtil.getResultValue(mvcResult.getResponse().getContentAsString(), UserService.UserJwtResponse.class);

        assertThat(userRepository.findByUsername("test-user")).isPresent();
        assertThat(resultValue.getToken()).isNotBlank();
    }

    @Test
    void signOut() {
    }

    @Test
    void tokenResolver() {
    }
}
