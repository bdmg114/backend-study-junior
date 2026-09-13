package com.gdgku.club;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * [DTO 미분리 문제] ClubController는 Controller-Service 계층은 잘 나뉘어 있지만,
 * API 응답으로 Member/Club 엔티티를 그대로 돌려주고 있어서 회원가입 시 입력한
 * 비밀번호가 응답 JSON에 그대로 노출된다.
 *
 * 이 테스트를 통과시키려면 비밀번호를 제외한 응답 전용 DTO(예: MemberResponse)를 만들어
 * Controller가 엔티티 대신 DTO를 반환하도록 리팩터링해야 한다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class ClubControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private Long createClub(String name) {
        Club request = new Club();
        request.setName(name);
        return restTemplate.postForEntity("/clubs", request, Club.class).getBody().getId();
    }

    private void joinClub(Long clubId, String name, String email, String password) {
        Member request = new Member();
        request.setName(name);
        request.setEmail(email);
        request.setPassword(password);
        restTemplate.postForEntity("/clubs/" + clubId + "/members", request, Member.class);
    }

    @Test
    void 동아리를_생성할_수_있다() {
        Club request = new Club();
        request.setName("백엔드 스터디");

        ResponseEntity<Club> response = restTemplate.postForEntity("/clubs", request, Club.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("백엔드 스터디", response.getBody().getName());
    }

    @Test
    void 동아리에_가입하면_회원_목록에_추가된다() {
        Long clubId = createClub("백엔드 스터디");

        joinClub(clubId, "Alice", "alice@test.com", "secret1234!");

        ResponseEntity<Member[]> response = restTemplate.getForEntity("/clubs/" + clubId + "/members", Member[].class);
        assertEquals(1, response.getBody().length);
        assertEquals("Alice", response.getBody()[0].getName());
    }

    @Test
    void 회원가입_응답에는_비밀번호가_노출되면_안된다() {
        Long clubId = createClub("백엔드 스터디");

        Member request = new Member();
        request.setName("Bob");
        request.setEmail("bob@test.com");
        request.setPassword("supersecret!");

        ResponseEntity<String> response =
                restTemplate.postForEntity("/clubs/" + clubId + "/members", request, String.class);

        assertFalse(response.getBody().contains("supersecret"),
                "회원가입 응답에 비밀번호가 그대로 노출되었습니다: " + response.getBody());
    }

    @Test
    void 회원_목록_조회_응답에도_비밀번호가_노출되면_안된다() {
        Long clubId = createClub("백엔드 스터디");
        joinClub(clubId, "Charlie", "charlie@test.com", "charliepw!");

        ResponseEntity<String> response =
                restTemplate.getForEntity("/clubs/" + clubId + "/members", String.class);

        assertFalse(response.getBody().contains("charliepw"),
                "회원 목록 응답에 비밀번호가 그대로 노출되었습니다: " + response.getBody());
    }
}
