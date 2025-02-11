package com.ilsmp.base.ui;

import javax.swing.*;

import com.github.mars05.crud.hub.common.dto.ProjectTemplateDTO;

public class ListElement {
    private Icon icon;
    private Long id;
    private String name;

    private ProjectTemplateDTO projectTemplateDTO;

    public ListElement(Icon icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    public void setProjectTemplateDTO(ProjectTemplateDTO projectTemplateDTO) {
        this.projectTemplateDTO = projectTemplateDTO;
    }

    public ProjectTemplateDTO getProjectTemplateDTO() {
        return projectTemplateDTO;
    }

    public ListElement(Icon icon, Long id, String name) {
        this.icon = icon;
        this.id = id;
        this.name = name;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
