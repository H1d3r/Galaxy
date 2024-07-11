package org.m2sec.panels.httphook;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.Theme;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.m2sec.Galaxy;
import org.m2sec.core.common.CacheOption;
import org.m2sec.core.common.Constants;
import org.m2sec.core.common.Render;
import org.m2sec.core.httphook.JavaFileHooker;
import org.m2sec.core.common.FileTools;
import org.m2sec.panels.SwingTools;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 22:24
 * @description:
 */

public class JavaImpl extends IHookService<JavaFileHooker> {
    private final CacheOption cache;
    private final MontoyaApi api;

    private static final String javaFileSuffix = ".java";

    private final JComboBox<String> codeCombo = new JComboBox<>();

    RSyntaxTextArea codeTextArea = new RSyntaxTextArea();

    public JavaImpl(CacheOption cache, MontoyaApi api) {
        this.cache = cache;
        this.api = api;
        initPanel();
    }

    private void initPanel() {
        JTextComponent.removeKeymap("RTextAreaKeymap");
        UIManager.put("RTextAreaUI.inputMap", null);
        UIManager.put("RTextAreaUI.actionMap", null);
        UIManager.put("RSyntaxTextAreaUI.inputMap", null);
        UIManager.put("RSyntaxTextAreaUI.actionMap", null);
//        setBackground(Color.red);
        setLayout(new BorderLayout());
        // 创建顶部下拉框的面板
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel selectLabel = new JLabel("JavaFile: ");
        SwingTools.addTipToLabel(selectLabel, Constants.HTTP_HOOK_JAVA_DEF, api);
        JPanel rightPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton newButton = new JButton("New");
        JButton deleteButton = new JButton("Delete");
        rightPanel.add(selectLabel);
        rightPanel.add(codeCombo);
        rightPanel.add(saveButton);
        rightPanel.add(newButton);
        rightPanel.add(deleteButton);
        reloadExamples(codeCombo);

        topPanel.add(rightPanel, BorderLayout.WEST);


        // 创建 RTextScrollPane 并添加到 JFrame
        RTextScrollPane scrollPane = new RTextScrollPane(codeTextArea);

        add(new JLabel("This is JAVA"), BorderLayout.NORTH);
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
//        setBackground(Color.blue);

        codeCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                codeTextArea.setText(FileTools.readFileAsString(getFilePath((String) e.getItem())));
                codeTextArea.setCaretPosition(0);
            }
        });

        saveButton.addActionListener(e -> FileTools.writeFile(getFilePath((String) codeCombo.getSelectedItem()),
            codeTextArea.getText()));

        newButton.addActionListener(e -> {
            String filename = JOptionPane.showInputDialog(null, "Please input filename: ");
            if (filename != null) {
                String filepath = getFilePath(filename.replace(javaFileSuffix, ""));
                FileTools.createFiles(filepath);
                String content = Render.renderTemplate(FileTools.readResourceAsString("templates/HttpHookTemplate.java"),
                    new HashMap<>(Map.of("filename", filename)));
                FileTools.writeFile(filepath, content);
                reloadExamples(codeCombo);
                codeCombo.setSelectedItem(filename);
            }
        });

        deleteButton.addActionListener(e -> {
            String filepath = getFilePath((String) codeCombo.getSelectedItem());
            FileTools.deleteFileIfExist(filepath);
            reloadExamples(codeCombo);
            codeCombo.setSelectedIndex(0);
        });

        setData();
        codeTextArea.setText(FileTools.readFileAsString(getFilePath((String) codeCombo.getSelectedItem())));

    }

    private void setData() {
        codeCombo.setSelectedItem(cache.getJavaSelectItem());
    }

    public String getData() {
        return (String) codeCombo.getSelectedItem();
    }

    private void reloadExamples(JComboBox<String> codeCombo) {
        codeCombo.removeAllItems();
        List<String> examples = FileTools.listDir(Constants.HTTP_HOOK_EXAMPLES_FILE_DIR);
        examples.stream().filter(x -> new File(x).getName().endsWith(javaFileSuffix)).forEach(x -> codeCombo.addItem(new File(x).getName().replace(javaFileSuffix, "")));
        setData();
    }

    private String getFilePath(String item) {
        return Constants.HTTP_HOOK_EXAMPLES_FILE_DIR + File.separator + item + javaFileSuffix;
    }

    public void resetCodeTheme() {
        codeTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        codeTextArea.setAntiAliasingEnabled(true);
        codeTextArea.setAutoIndentEnabled(true);
        codeTextArea.setPaintTabLines(true);
        codeTextArea.setTabSize(4);
        codeTextArea.setCodeFoldingEnabled(true);
        codeTextArea.setTabsEmulated(true);
        codeTextArea.setHighlightCurrentLine(true);
        if (Galaxy.isInBurp() && api.userInterface().currentTheme().equals(Theme.DARK)) {
            try {
                org.fife.ui.rsyntaxtextarea.Theme.load(getClass().getResourceAsStream("/org/fife/ui" +
                    "/rsyntaxtextarea/themes/dark.xml")).apply(codeTextArea);
                codeTextArea.setFont(api.userInterface().currentEditorFont());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public JavaFileHooker newHooker(CacheOption cache) {
        return new JavaFileHooker(cache);
    }

    @Override
    public String displayName() {
        return null;
    }
}