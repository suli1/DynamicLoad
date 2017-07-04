package com.suli.dynamicload.hook;

import com.suli.libbase.L;
import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 由于应用程序使用的ClassLoader为PathClassLoader
 * 最终继承自 BaseDexClassLoader
 * 查看源码得知,这个BaseDexClassLoader加载代码根据一个叫做
 * dexElements的数组进行, 因此我们把包含代码的dex文件插入这个数组
 * 系统的classLoader就能帮助我们找到这个类
 *
 * 这个类用来进行对于BaseDexClassLoader的Hook
 */
public final class BaseDexClassLoaderHookHelper {

  public static void patchClassLoader(ClassLoader cl, File apkFile, File optDexFile,
      String nativeLibraryPath)
      throws IllegalAccessException, NoSuchMethodException, IOException, InvocationTargetException,
      InstantiationException, NoSuchFieldException {
    // 获取 BaseDexClassLoader : pathList
    Field pathListField = DexClassLoader.class.getSuperclass().getDeclaredField("pathList");
    pathListField.setAccessible(true);
    Object pathListObj = pathListField.get(cl);

    // 获取 PathList: Element[] dexElements
    Field dexElementArray = pathListObj.getClass().getDeclaredField("dexElements");
    dexElementArray.setAccessible(true);
    Object[] dexElements = (Object[]) dexElementArray.get(pathListObj);

    // 获取 PathList: Element[] nativeLibraryPathElements
    Field nativeLibraryPathElementArray =
        pathListObj.getClass().getDeclaredField("nativeLibraryPathElements");
    nativeLibraryPathElementArray.setAccessible(true);
    Object[] nativeLibraryPathElements = (Object[]) nativeLibraryPathElementArray.get(pathListObj);

    // Element 类型
    Class<?> elementClass = dexElements.getClass().getComponentType();

    // 创建一个数组, 用来替换原始的数组
    Object[] newElements = (Object[]) Array.newInstance(elementClass, dexElements.length + 1);
    Object[] newNativeElements =
        (Object[]) Array.newInstance(elementClass, nativeLibraryPathElements.length + 1);

    // 构造插件Element(File file, boolean isDirectory, File zip, DexFile dexFile) 这个构造函数
    Constructor<?> constructor =
        elementClass.getConstructor(File.class, boolean.class, File.class, DexFile.class);

    // dexElement
    Object dexElement = constructor.newInstance(apkFile, false, apkFile,
        DexFile.loadDex(apkFile.getCanonicalPath(), optDexFile.getAbsolutePath(), 0));
    Object[] toAddElementArray = new Object[] { dexElement };

    // nativeLibraryPathElement
    Object nativeLibraryPathElement =
        constructor.newInstance(new File(nativeLibraryPath), true, null, null);
    Object[] toAddNativeElementArray = new Object[] { nativeLibraryPathElement };

    // 把原始的elements复制进去
    System.arraycopy(dexElements, 0, newElements, 0, dexElements.length);
    System.arraycopy(nativeLibraryPathElements, 0, newNativeElements, 0,
        nativeLibraryPathElements.length);

    // 插件的那个element复制进去
    System.arraycopy(toAddElementArray, 0, newElements, dexElements.length,
        toAddElementArray.length);
    System.arraycopy(toAddNativeElementArray, 0, newNativeElements,
        nativeLibraryPathElements.length, toAddNativeElementArray.length);

    // 替换
    dexElementArray.set(pathListObj, newElements);
    nativeLibraryPathElementArray.set(pathListObj, newNativeElements);

    //// check
    //Method findLibraryMethod =
    //    pathListObj.getClass().getDeclaredMethod("findLibrary", String.class);
    //findLibraryMethod.setAccessible(true);
    //Object nativeLibrary = findLibraryMethod.invoke(pathListObj, "lib-sdk");
    //L.i("native library:" + nativeLibrary);
    //
    //Method findNativeLibraryMethod =
    //    elementClass.getDeclaredMethod("findNativeLibrary", String.class);
    //String filename = System.mapLibraryName("lib-sdk");
    //for (Object element : newNativeElements) {
    //  Object path = findNativeLibraryMethod.invoke(element, filename);
    //  if (path != null) {
    //    L.i("path:" + path);
    //    break;
    //  }
    //}
  }
}
