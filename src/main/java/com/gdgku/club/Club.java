package com.gdgku.club;

import java.util.ArrayList;
import java.util.List;

public class Club {

    private Long id;
    private String name;
    private final List<Member> members = new ArrayList<>();

    public Club() {
    }

    public Club(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Member> getMembers() {
        return members;
    }
}
