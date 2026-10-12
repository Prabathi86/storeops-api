package com.storeops.projects;

import java.util.ArrayList;
import java.util.List;

public class Project {
    private String id;
    private String name;
    private String storeId;
    private List<ProjectMember> members = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStoreId() { return storeId; }
    public void setStoreId(String storeId) { this.storeId = storeId; }

    public List<ProjectMember> getMembers() { return members; }
}
