package org.dpytel.intellij.plugin.maventest.model;

import com.intellij.execution.Location;
import com.intellij.execution.junit2.info.MethodLocation;
import com.intellij.execution.junit2.info.PsiClassLocator;
import com.intellij.execution.junit2.info.TestInfo;
import com.intellij.execution.junit2.segments.ObjectReader;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.MethodSignature;
import com.intellij.psi.util.MethodSignatureUtil;

/**
 *
 */
public class TestMethodInfo extends TestInfo {

    private final PsiClassLocator myClass;
    private final String methodName;

    public TestMethodInfo(String classQualifiedName, String methodName) {
        this.methodName = methodName;
        myClass = PsiClassLocator.fromQualifiedName(classQualifiedName);
    }

    @Override
    public void readFrom(ObjectReader reader) {
        // do nothing
    }

    @Override
    public String getComment() {
        return "";
    }

    @Override
    public String getName() {
        return methodName;
    }

    @Override
    public Location getLocation(Project project) {
        Location<PsiClass> classLocation = myClass.getLocation(project);
        if (classLocation == null) {
            return null;
        }
        MethodSignature patternMethod = MethodSignatureUtil.createMethodSignature(methodName, PsiType.EMPTY_ARRAY,
            PsiTypeParameter.EMPTY_ARRAY, PsiSubstitutor.EMPTY);
        PsiMethod method = MethodSignatureUtil
            .findMethodBySignature(classLocation.getPsiElement(), patternMethod, true);
        if (method == null) {
            return null;
        }
        return new MethodLocation(project, method, classLocation);
    }

    @Override
    public boolean shouldRun() {
        return true;
    }

    @Override
    public int getTestsCount() {
        return 1;
    }
}
