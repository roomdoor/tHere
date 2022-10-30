package onde.there.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import onde.there.domain.Member;
import onde.there.dto.member.MemberDto;
import onde.there.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MockMvc mockMvc;



    @BeforeEach
    void delete_all_member() {
        System.out.println("Member Table Clear");
        memberRepository.deleteAll();
    }

    @Test
    void 아이디중복확인_성공_케이스 () throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        MemberDto.CheckIdRequest checkIdRequest = new MemberDto.CheckIdRequest("test");
        String content = objectMapper.writeValueAsString(checkIdRequest);

        mockMvc.perform(post("/members/check/id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['result']").exists())
                .andDo(print());
    }

    @Test
    void 아이디중복확인_실패_케이스_BADREQUEST_바디가없을경우 () throws Exception{
        String content = "";
        mockMvc.perform(post("/members/check/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['errorCode']").value("BAD_REQUEST"))
                .andExpect(jsonPath("$['errorMessage']").exists())
                .andDo(print());
    }

    @Test
    void 아이디중복확인_실패_케이스_BADREQUEST_id값_비어있는_경우 () throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        MemberDto.CheckIdRequest checkIdRequest = new MemberDto.CheckIdRequest("");
        String content = objectMapper.writeValueAsString(checkIdRequest);

        mockMvc.perform(post("/members/check/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['errorCode']").value("BAD_REQUEST"))
                .andExpect(jsonPath("$['errorMessage']").exists())
                .andDo(print());
    }

    @Test
    void 이메일중복확인_성공_케이스 () throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        MemberDto.CheckEmailRequest checkEmailRequest = new MemberDto.CheckEmailRequest("test@test.com");
        String content = objectMapper.writeValueAsString(checkEmailRequest);

        mockMvc.perform(post("/members/check/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['result']").exists())
                .andDo(print());
    }

    @Test
    void 이메일중복확인_실패_케이스_BADREQUEST_바디가없을경우 () throws Exception{
        String content = "";
        mockMvc.perform(post("/members/check/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['errorCode']").value("BAD_REQUEST"))
                .andExpect(jsonPath("$['errorMessage']").exists())
                .andDo(print());
    }

    @Test
    void 이메일중복확인_실패_케이스_BADREQUEST_email값_비어있는_경우 () throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        MemberDto.CheckEmailRequest checkEmailRequest = new MemberDto.CheckEmailRequest("");
        String content = objectMapper.writeValueAsString(checkEmailRequest);

        mockMvc.perform(post("/members/check/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['errorCode']").value("BAD_REQUEST"))
                .andExpect(jsonPath("$['errorMessage']").exists())
                .andDo(print());
    }

    @Test
    void 이메일중복확인_실패_케이스_BADREQUEST_이메일_형식_아님 () throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        MemberDto.CheckEmailRequest checkEmailRequest = new MemberDto.CheckEmailRequest("test");
        String content = objectMapper.writeValueAsString(checkEmailRequest);

        mockMvc.perform(post("/members/check/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['errorCode']").value("BAD_REQUEST"))
                .andExpect(jsonPath("$['errorMessage']").exists())
                .andDo(print());
    }

    @Test
    void 회원가입요청_성공_케이스() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        MemberDto.SignupRequest signupRequest = MemberDto.SignupRequest.builder()
                                                .id("test")
                                                .email("test@test.com")
                                                .name("테스트")
                                                .password("1234")
                                                .build();
        String content = objectMapper.writeValueAsString(signupRequest);

        mockMvc.perform(post("/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['message']").exists())
                .andExpect(jsonPath("$['email']").exists())
                .andDo(print());
    }

    @Test
    void 회원가입요청_실패_케이스_중복된_이메일() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        MemberDto.SignupRequest signupRequest = MemberDto.SignupRequest.builder()
                                                .id("test")
                                                .email("test@test.com")
                                                .name("테스트")
                                                .password("1234")
                                                .build();
        String content = objectMapper.writeValueAsString(signupRequest);
        memberRepository.save(new Member("test", "test@test.com", "1234", "test"));

        mockMvc.perform(post("/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['errorCode']").value("DUPLICATED_MEMBER_EMAIL"))
                .andExpect(jsonPath("$['errorMessage']").exists())
                .andDo(print());
    }

    @Test
    void 회원가입요청_실패_케이스_중복된_아이디() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        MemberDto.SignupRequest signupRequest = MemberDto.SignupRequest.builder()
                                                .id("test")
                                                .email("test@test.com")
                                                .name("테스트")
                                                .password("1234")
                                                .build();
        String content = objectMapper.writeValueAsString(signupRequest);
        memberRepository.save(new Member("test", "test1@test.com", "1234", "test"));

        mockMvc.perform(post("/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['errorCode']").value("DUPLICATED_MEMBER_ID"))
                .andExpect(jsonPath("$['errorMessage']").exists())
                .andDo(print());
    }

}
