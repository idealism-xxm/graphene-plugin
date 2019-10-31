package cn.idealismxxm.grapheneplugin.util.types;

import cn.idealismxxm.grapheneplugin.enums.pyclass.PyClassInfo;
import cn.idealismxxm.grapheneplugin.util.PyClassUtil;
import com.intellij.psi.PsiElement;
import com.intellij.util.Function;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyTypedElement;
import com.jetbrains.python.psi.types.PyClassType;
import com.jetbrains.python.psi.types.TypeEvalContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PyClassTypeUtil extends PyTypeUtil {

    /**
     * get python class type of psiElement
     *
     * @param element PsiElement
     * @param filter  filter pyClassType
     * @return null for Any type
     */
    @Nullable
    public static PyClassType getClassType(@NotNull PsiElement element, @Nullable Function<PyClassType, Boolean> filter) {
        return Optional.of(element)
                .filter(psiElement -> psiElement instanceof PyTypedElement)
                .map(psiElement -> TypeEvalContext.userInitiated(psiElement.getProject(), psiElement.getContainingFile()))
                .map(typeEvalContext -> typeEvalContext.getType((PyTypedElement) element))
                .filter(pyType -> pyType instanceof PyClassType)
                .map(pyType -> (PyClassType) pyType)
                .filter(pyClassType -> filter != null ? filter.fun(pyClassType) : true)
                .orElse(null);
    }

    /**
     * judge whether the type of element matches exact class or subclass described in pyClassInfos
     *
     * @param element     PsiElement
     * @param filter      filter pyClassType
     * @param pyClassInfo pyClassInfo
     * @return true / false
     */
    public static boolean typeMatchesClass(@NotNull PsiElement element, @Nullable Function<PyClassType, Boolean> filter, @NotNull PyClassInfo pyClassInfo) {
        return typeMatchesAnyClass(element, filter, pyClassInfo);
    }

    /**
     * judge whether the type of element matches exact class or subclass of anyone described in pyClassInfos
     *
     * @param element      PsiElement
     * @param filter       filter pyClassType
     * @param pyClassInfos pyClassInfo array
     * @return true / false
     */
    public static boolean typeMatchesAnyClass(@NotNull PsiElement element, @Nullable Function<PyClassType, Boolean> filter, @NotNull PyClassInfo... pyClassInfos) {
        // get pyClass first to avoid calling getClassType twice
        return Optional.ofNullable(getClassType(element, filter))
                .map(PyClassType::getPyClass)
                .map(pyClass -> PyClassUtil.matchesAnyClass(pyClass, pyClassInfos))
                .orElse(false);
    }

    /**
     * judge whether the type of element matches subclass described in pyClassInfo
     *
     * @param element     PsiElement
     * @param filter      filter pyClassType
     * @param pyClassInfo pyClassInfo
     * @return true / false
     */
    public static boolean typeMatchesSubclass(@NotNull PsiElement element, @Nullable Function<PyClassType, Boolean> filter, @NotNull PyClassInfo pyClassInfo) {
        return typeMatchesAnySubclass(element, filter, pyClassInfo);
    }

    /**
     * judge whether the type of element matches subclass of anyone described in pyClassInfos
     *
     * @param element      PsiElement
     * @param filter       filter pyClassType
     * @param pyClassInfos pyClassInfo array
     * @return true / false
     */
    public static boolean typeMatchesAnySubclass(@NotNull PsiElement element, @Nullable Function<PyClassType, Boolean> filter, @NotNull PyClassInfo... pyClassInfos) {
        return Optional.ofNullable(getClassType(element, filter))
                .map(PyClassType::getPyClass)
                .map(pyClass -> PyClassUtil.matchesAnyClass(pyClass, pyClassInfos))
                .orElse(false);
    }

    /**
     * judge whether the type of element matches exact class described in pyClassInfo
     *
     * @param element     PsiElement
     * @param filter      filter pyClassType
     * @param pyClassInfo pyClassInfo
     * @return true / false
     */
    public static boolean typeMatchesExactClass(@NotNull PsiElement element, @Nullable Function<PyClassType, Boolean> filter, @NotNull PyClassInfo pyClassInfo) {
        return typeMatchesAnyExactClass(element, filter, pyClassInfo);
    }

    /**
     * judge whether the type of element matches exact class of anyone described in pyClassInfos
     *
     * @param element      PsiElement
     * @param filter       filter pyClassType
     * @param pyClassInfos pyClassInfo array
     * @return true / false
     */
    public static boolean typeMatchesAnyExactClass(@NotNull PsiElement element, @Nullable Function<PyClassType, Boolean> filter, @NotNull PyClassInfo... pyClassInfos) {
        return Optional.ofNullable(getClassType(element, filter))
                .map(PyClassType::getPyClass)
                .map(pyClass -> PyClassUtil.matchesAnyExactClass(pyClass, pyClassInfos))
                .orElse(false);
    }
}