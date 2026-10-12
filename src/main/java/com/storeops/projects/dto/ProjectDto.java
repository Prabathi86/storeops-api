package com.storeops.projects.dto;

import com.storeops.projects.ProjectRole;
import java.util.List;

public class ProjectDto {
    public String projectId;
    public String projectName;
    public String storeId;
    public List<MemberDto> members;

    public static class MemberDto {
        public String userId;
        public ProjectRole role;
    }
}
