package com.mzyupc.aredis.view.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.mzyupc.aredis.utils.FormatUtil;
import com.mzyupc.aredis.view.dialog.enums.RedisValueTypeEnum;
import com.mzyupc.aredis.view.dialog.enums.ValueFormatEnum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.batik.ext.swing.DoubleDocument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.function.Consumer;

/**
 * @author mzyupc@163.com
 * <p>
 * 新增key的对话框
 */
@Slf4j
@Getter
public class NewKeyDialog extends DialogWrapper {

    private Consumer<ActionEvent> customOkAction;

    private CardLayout cardLayout;

    // 选中的数据类型
    private RedisValueTypeEnum selectedType;

    /**
     * key 输入框
     */
    private JTextField keyTextField;

    /**
     * zset value
     */
    private JPanel zsetValuePanel;
    /**
     * zset score
     */
    private JTextField scoreTextField;

    /**
     * hash value
     */
    private JPanel hashValuePanel;
    /**
     * hash field
     */
    private JTextField fieldTextField;

    private EditorTextField stringValueTextArea;
    private EditorTextField listValueTextArea;
    private EditorTextField setValueTextArea;
    private EditorTextField zsetValueTextArea;
    private EditorTextField hashValueTextArea;

    public NewKeyDialog(@Nullable Project project) {
        super(project);
        this.init();
    }

    @Override
    protected void init() {
        super.init();
    }


    @Override
    protected @Nullable
    JComponent createCenterPanel() {

        JPanel keyPanel = createKeyPanel();
        JPanel valuePanel = createValuePanel();
        JPanel typePanel = createTypePanel(valuePanel);

        JPanel keyAndTypePanel = new JPanel(new BorderLayout());
        keyAndTypePanel.add(keyPanel, BorderLayout.NORTH);
        keyAndTypePanel.add(typePanel, BorderLayout.SOUTH);

        JPanel container = new JPanel(new BorderLayout());
        container.setMinimumSize(new Dimension(500, 250));
        container.add(keyAndTypePanel, BorderLayout.NORTH);
        container.add(valuePanel, BorderLayout.CENTER);
        return container;
    }

    @NotNull
    private JPanel createValuePanel() {
        JPanel stringValuePanel = createSimpleValuePanel(RedisValueTypeEnum.String);
        JPanel listValuePanel = createSimpleValuePanel(RedisValueTypeEnum.List);
        JPanel setValuePanel = createSimpleValuePanel(RedisValueTypeEnum.Set);
        zsetValuePanel = createSimpleValuePanel(RedisValueTypeEnum.Zset);
        hashValuePanel = createSimpleValuePanel(RedisValueTypeEnum.Hash);

        JPanel zsetTypePanel = createZSetValuePanel();
        JPanel hashTypePanel = createHashValuePanel();

        cardLayout = new CardLayout();
        JPanel valuePanel = new JPanel(cardLayout);
        valuePanel.add(RedisValueTypeEnum.String.name(), stringValuePanel);
        valuePanel.add(RedisValueTypeEnum.List.name(), listValuePanel);
        valuePanel.add(RedisValueTypeEnum.Set.name(), setValuePanel);
        valuePanel.add(RedisValueTypeEnum.Zset.name(), zsetTypePanel);
        valuePanel.add(RedisValueTypeEnum.Hash.name(), hashTypePanel);
        return valuePanel;
    }

    @NotNull
    private JPanel createTypePanel(JPanel valuePanel) {
        JBLabel typeLabel = new JBLabel("Type:");
        typeLabel.setPreferredSize(new Dimension(50, 25));

        JComboBox<RedisValueTypeEnum> redisValueTypeEnumJComboBox = new JComboBox<>(RedisValueTypeEnum.values());

        redisValueTypeEnumJComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (ItemEvent.SELECTED == e.getStateChange()) {
                    selectedType = (RedisValueTypeEnum) e.getItem();
                    cardLayout.show(valuePanel, selectedType.name());
                }
            }
        });
        redisValueTypeEnumJComboBox.setSelectedIndex(0);
        selectedType = RedisValueTypeEnum.String;

        JPanel typePanel = new JPanel(new BorderLayout());
        typePanel.add(typeLabel, BorderLayout.WEST);
        typePanel.add(redisValueTypeEnumJComboBox, BorderLayout.CENTER);
        return typePanel;
    }

    @NotNull
    private JPanel createKeyPanel() {
        JPanel keyPanel = new JPanel(new BorderLayout());
        keyPanel.setMinimumSize(new Dimension(300, 10));
        JBLabel keyLabel = new JBLabel("Key:");
        keyLabel.setPreferredSize(new Dimension(50, 25));
        keyPanel.add(keyLabel, BorderLayout.WEST);
        keyTextField = new JTextField();
        keyPanel.add(keyTextField, BorderLayout.CENTER);
        return keyPanel;
    }

    @NotNull
    private JPanel createZSetValuePanel() {
        JPanel scorePanel = new JPanel(new BorderLayout());
        JBLabel scoreLabel = new JBLabel("Score:");
        scoreLabel.setPreferredSize(new Dimension(50, 25));
        scorePanel.add(scoreLabel, BorderLayout.WEST);
        scoreTextField = new JTextField();
        scoreTextField.setDocument(new DoubleDocument());
        scorePanel.add(scoreTextField, BorderLayout.CENTER);

        JPanel zsetTypePanel = new JPanel(new BorderLayout());
        zsetTypePanel.add(scorePanel, BorderLayout.NORTH);
        zsetTypePanel.add(zsetValuePanel, BorderLayout.CENTER);
        return zsetTypePanel;
    }

    @NotNull
    private JPanel createHashValuePanel() {
        JPanel scorePanel = new JPanel(new BorderLayout());
        JBLabel scoreLabel = new JBLabel("Field:");
        scoreLabel.setPreferredSize(new Dimension(50, 25));
        scorePanel.add(scoreLabel, BorderLayout.WEST);
        fieldTextField = new JTextField();
        scorePanel.add(fieldTextField, BorderLayout.CENTER);

        JPanel zsetTypePanel = new JPanel(new BorderLayout());
        zsetTypePanel.add(scorePanel, BorderLayout.NORTH);
        zsetTypePanel.add(hashValuePanel, BorderLayout.CENTER);
        return zsetTypePanel;
    }

    /**
     * 创建一个value panel
     * @return
     */
    @NotNull
    private JPanel createSimpleValuePanel(RedisValueTypeEnum valueTypeEnum) {
        JBScrollPane valueArea;
        EditorTextField valueTextArea;
        switch (valueTypeEnum) {
            case List:
                listValueTextArea = new EditorTextField();
                listValueTextArea.setOneLineMode(false);
                listValueTextArea.setAutoscrolls(true);
                valueArea = new JBScrollPane(listValueTextArea.getComponent());
                valueTextArea = listValueTextArea;
                break;
            case Set:
                setValueTextArea = new EditorTextField();
                setValueTextArea.setOneLineMode(false);
                setValueTextArea.setAutoscrolls(true);
                valueArea = new JBScrollPane(setValueTextArea.getComponent());
                valueTextArea = setValueTextArea;
                break;
            case Zset:
                zsetValueTextArea = new EditorTextField();
                zsetValueTextArea.setOneLineMode(false);
                zsetValueTextArea.setAutoscrolls(true);
                valueArea = new JBScrollPane(zsetValueTextArea.getComponent());
                valueTextArea = zsetValueTextArea;
                break;
            case Hash:
                hashValueTextArea = new EditorTextField();
                hashValueTextArea.setOneLineMode(false);
                hashValueTextArea.setAutoscrolls(true);
                valueArea = new JBScrollPane(hashValueTextArea.getComponent());
                valueTextArea = hashValueTextArea;
                break;
            default:
                stringValueTextArea = new EditorTextField();
                stringValueTextArea.setOneLineMode(false);
                stringValueTextArea.setAutoscrolls(true);
                valueArea = new JBScrollPane(stringValueTextArea.getComponent());
                valueTextArea = stringValueTextArea;
        }

        JComboBox<ValueFormatEnum> newKeyValueFormatEnumJComboBox = new JComboBox<>(ValueFormatEnum.values());
        newKeyValueFormatEnumJComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (ItemEvent.SELECTED == e.getStateChange()) {
                    ValueFormatEnum formatEnum = (ValueFormatEnum) e.getItem();
                    String format = FormatUtil.format(valueTextArea.getText(), formatEnum);
                    valueTextArea.setText(format);
                }
            }
        });

        JPanel viewAsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        viewAsPanel.add(new JBLabel("View as:"));
        viewAsPanel.add(newKeyValueFormatEnumJComboBox);

        JPanel valueLabelPanel = new JPanel(new BorderLayout());
        valueLabelPanel.add(new JBLabel("Value:"), BorderLayout.WEST);
        // todo 先不实现value格式化
//        valueLabelPanel.add(viewAsPanel, BorderLayout.EAST);



        JPanel stringTypePanel = new JPanel(new BorderLayout());
        stringTypePanel.add(valueLabelPanel, BorderLayout.NORTH);
        stringTypePanel.add(valueArea, BorderLayout.CENTER);
        return stringTypePanel;
    }


    @NotNull
    @Override
    protected Action[] createActions() {
        DialogWrapperExitAction exitAction = new DialogWrapperExitAction("Cancel", CANCEL_EXIT_CODE);
        NewKeyDialog.CustomOKAction okAction = new NewKeyDialog.CustomOKAction();
        // 设置默认的焦点按钮
        okAction.putValue(DialogWrapper.DEFAULT_ACTION, true);
        return new Action[]{exitAction, okAction};
    }


    /**
     * 自定义 ok Action
     */
    protected class CustomOKAction extends DialogWrapperAction {
        protected CustomOKAction() {
            super("OK");
        }

        @Override
        protected void doAction(ActionEvent e) {
            if (customOkAction != null) {
                customOkAction.accept(e);
            }
        }
    }

    public void setCustomOkAction(Consumer<ActionEvent> customOkAction) {
        this.customOkAction = customOkAction;
    }
}