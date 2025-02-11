package com.ilsmp.base.step;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.mars05.crud.hub.common.dto.FileTemplateDTO;
import com.github.mars05.crud.hub.common.enums.FileTemplateTypeEnum;
import com.github.mars05.crud.hub.common.enums.ProjectTypeEnum;
import com.github.mars05.crud.hub.common.util.StringUtils;
import com.google.common.base.Preconditions;
import com.ilsmp.base.dto.GenerateDTO;
import com.ilsmp.base.dto.ProjectTemplateRespDTO;
import com.ilsmp.base.service.ProjectTemplateService;
import com.ilsmp.base.setting.CrudSettings;
import com.ilsmp.base.util.CrudUtils;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.CheckBoxList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.ThreeStateCheckBox;
import org.jetbrains.uast.values.UBooleanConstant;

public class CodeStep extends ModuleWizardStep {
    private JPanel myMainPanel;
    private JTextField basePackageTextField;
    private JScrollPane myScrollPane;
    private ThreeStateCheckBox allCheckBox;
    private CheckBoxList checkBoxList;
    private TextFieldWithBrowseButton projectPathButton;
    private JLabel basePackageLabel;

    private List<String> nameList = new ArrayList<>();
    private Long ptId;

    private final ProjectTemplateService projectTemplateService = CrudUtils.getBean(ProjectTemplateService.class);

    public CodeStep() {
        projectPathButton.addBrowseFolderListener("选择代码生成的项目路径", "", null,
                new FileChooserDescriptor(false, true, false, false, false, false),
                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT);
        allCheckBox.addActionListener(e -> {
            if (nameList.size() == getSelectedNameList().size()) {
                allCheckBox.setState(ThreeStateCheckBox.State.NOT_SELECTED);
                nameList.forEach(s -> checkBoxList.setItemSelected(s, false));
            } else {
                allCheckBox.setState(ThreeStateCheckBox.State.SELECTED);
                nameList.forEach(s -> checkBoxList.setItemSelected(s, true));
            }
            checkBoxList.repaint();
        });
        checkBoxList.setCheckBoxListListener((index, value) -> {
            if (nameList.size() == getSelectedNameList().size()) {
                allCheckBox.setState(ThreeStateCheckBox.State.SELECTED);
            } else if (getSelectedNameList().size() > 0) {
                allCheckBox.setState(ThreeStateCheckBox.State.DONT_CARE);
            } else {
                allCheckBox.setState(ThreeStateCheckBox.State.NOT_SELECTED);
            }
        });
    }

    @Override
    public JComponent getComponent() {
        getList();
        return myMainPanel;
    }

    @Override
    public void updateDataModel() {

    }

    private Boolean stringIsEmpty(String str) {
        if (str == null || str.isEmpty()) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) > ' ') {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean validate() throws ConfigurationException {
        try {
            String projectPath = projectPathButton.getText();
            if (org.apache.commons.lang3.StringUtils.isEmpty(projectPath)) {
                throw new Exception("请选择项目路径");
            }
            String basePackage = basePackageTextField.getText();
            if (Arrays.asList(ProjectTypeEnum.JAVA.getCode(), ProjectTypeEnum.MAVEN.getCode())
                    .contains(projectTemplateService.detail(ptId).getProjectType())) {
                if (org.apache.commons.lang3.StringUtils.isEmpty(basePackage)) {
                    throw new Exception("请输入basePackage");
                }
                Preconditions.checkArgument(StringUtils.isPackageName(basePackage), "basePackage格式错误");
            }
            if (getSelectedNameList().isEmpty()) {
                throw new Exception("请选择需要生成的代码");
            }

            GenerateDTO generateDTO = CrudSettings.currentGenerate();
            generateDTO.setProjectPath(projectPath);
            generateDTO.setBasePackage(basePackage);
            generateDTO.setNameList(getSelectedNameList());
        } catch (Exception e) {
            throw new ConfigurationException(e.getMessage(), "验证失败");
        }
        return super.validate();
    }

    private void getList() {
        GenerateDTO generateDTO = CrudSettings.currentGenerate();
        if (generateDTO.getProjectTemplate() != null && !generateDTO.getProjectTemplate().getId().equals(ptId)
                && CollectionUtils.isNotEmpty(generateDTO.getTables())) {
            ptId = generateDTO.getProjectTemplate().getId();
            checkBoxList.clear();

            ProjectTemplateRespDTO projectTemplateRespDTO = projectTemplateService.detail(ptId);
            nameList = projectTemplateRespDTO.getFileTemplateList().stream().filter(fileTemplateDTO -> fileTemplateDTO.getType() == FileTemplateTypeEnum.CODE.getCode())
                    .map(FileTemplateDTO::getName).collect(Collectors.toList());
            nameList.forEach(s -> {
                if (CollectionUtils.isNotEmpty(generateDTO.getNameList())) {
                    checkBoxList.addItem(s, s, generateDTO.getNameList().contains(s));
                } else {
                    checkBoxList.addItem(s, s, true);
                }
            });

            if (Arrays.asList(ProjectTypeEnum.JAVA.getCode(), ProjectTypeEnum.MAVEN.getCode())
                    .contains(projectTemplateRespDTO.getProjectType())) {
                basePackageLabel.setVisible(true);
                basePackageTextField.setVisible(true);
            } else {
                basePackageLabel.setVisible(false);
                basePackageTextField.setVisible(false);
            }

            if (org.apache.commons.lang3.StringUtils.isNotBlank(generateDTO.getProjectPath())) {
                projectPathButton.setText(generateDTO.getProjectPath());
            }
            if (org.apache.commons.lang3.StringUtils.isNotBlank(generateDTO.getBasePackage())) {
                basePackageTextField.setText(generateDTO.getBasePackage());
            }
            if (nameList.size() == getSelectedNameList().size()) {
                allCheckBox.setState(ThreeStateCheckBox.State.SELECTED);
            } else if (getSelectedNameList().size() > 0) {
                allCheckBox.setState(ThreeStateCheckBox.State.DONT_CARE);
            } else {
                allCheckBox.setState(ThreeStateCheckBox.State.NOT_SELECTED);
            }
        }
    }

    private void createUIComponents() {
        checkBoxList = new CheckBoxList();
        myScrollPane = new JBScrollPane();
    }

    private List<String> getSelectedNameList() {
        return nameList.stream().filter(s ->
                checkBoxList.isItemSelected(s)
        ).collect(Collectors.toList());
    }
}
