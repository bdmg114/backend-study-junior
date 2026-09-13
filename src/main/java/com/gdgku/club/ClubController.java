package com.gdgku.club;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * [문제: DTO 미분리]
 *
 * Controller-Service 계층은 잘 나뉘어 있지만, API 응답으로 Member/Club 엔티티를
 * 그대로 돌려주고 있다. 그래서 회원가입 시 입력한 비밀번호가 응답 JSON에 그대로 노출된다.
 * ClubControllerTest의 회원가입_응답에는_비밀번호가_노출되면_안된다(),
 * 회원_목록_조회_응답에도_비밀번호가_노출되면_안된다() 테스트가 이 문제를 잡아낸다.
 *
 * 할 일: 비밀번호를 제외한 응답 전용 DTO(예: MemberResponse)를 만들어서
 * Controller가 엔티티 대신 DTO를 반환하도록 리팩터링해서 테스트를 통과시키자.
 */
@RestController
@RequestMapping("/clubs")
public class ClubController {

    private final ClubService clubService;

    public ClubController(ClubService clubService) {
        this.clubService = clubService;
    }

    @PostMapping
    public Club createClub(@RequestBody Club request) {
        return clubService.createClub(request.getName());
    }

    @GetMapping("/{clubId}")
    public Club getClub(@PathVariable Long clubId) {
        return clubService.getClub(clubId);
    }

    @PostMapping("/{clubId}/members")
    public Member joinClub(@PathVariable Long clubId, @RequestBody Member request) {
        return clubService.addMember(clubId, request.getName(), request.getEmail(), request.getPassword());
    }

    @GetMapping("/{clubId}/members")
    public List<Member> getMembers(@PathVariable Long clubId) {
        return clubService.getMembers(clubId);
    }
}
