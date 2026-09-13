package com.gdgku.club;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClubService {

    private final Map<Long, Club> clubs = new HashMap<>();
    private long nextClubId = 1L;
    private long nextMemberId = 1L;

    public Club createClub(String name) {
        Club club = new Club(nextClubId++, name);
        clubs.put(club.getId(), club);
        return club;
    }

    public Club getClub(Long clubId) {
        return clubs.get(clubId);
    }

    public Member addMember(Long clubId, String name, String email, String password) {
        Club club = clubs.get(clubId);
        Member member = new Member(nextMemberId++, name, email, password);
        club.getMembers().add(member);
        return member;
    }

    public List<Member> getMembers(Long clubId) {
        return clubs.get(clubId).getMembers();
    }
}
