package org.antlr.jetbrains.sample.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import org.antlr.jetbrains.sample.Icons;
import org.antlr.jetbrains.sample.SampleFileType;
import org.antlr.jetbrains.sample.SampleLanguage;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class SamplePSIFileRoot extends PsiFileBase {
    public SamplePSIFileRoot(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, SampleLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return SampleFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Sample Language file";
    }

    @Override
    public Icon getIcon(int flags) {
		return Icons.SAMPLE_ICON;
    }

	@NotNull
	@Override
	public PsiElement[] getChildren() {
		return super.getChildren();
	}
}
