package com.dao.rpc.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AnnotationScannerUtil {
    private static final Logger logger = LoggerFactory.getLogger(AnnotationScannerUtil.class);
    private static final String EXT = "class";

    /**
     * 加载类
     *
     * @param file
     * @param pkgPath
     * @param pkgName
     * @return
     * @throws ClassNotFoundException
     */
    private static Class<?> getClassObj(File file, String pkgPath, String pkgName) throws ClassNotFoundException {
        // 考虑class文件在子目录中的情况
        String absPath = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - EXT.length() - 1);
        String className = absPath.substring(pkgPath.length()).replace(File.separatorChar, '.');
        className = className.startsWith(".") ? pkgName + className : pkgName + "." + className;

        return Thread.currentThread().getContextClassLoader().loadClass(className);
    }

    /**
     * 遍历指定目录下所有扩展名为class的文件
     *
     * @param pkgPath   包目录
     * @param recursive 是否递归遍历子目录
     * @return
     */
    private static Collection<File> getAllClassFile(String pkgPath, boolean recursive) {
        File fPkgDir = new File(pkgPath);

        if (!(fPkgDir.exists() && fPkgDir.isDirectory())) {
            logger.error("the directory to package is empty: {}", pkgPath);

            return null;
        }
        return FileUtil.listFilesInDirWithFilter(fPkgDir, EXT, recursive);
    }

    /**
     * 获取含有某个指定注解的类集合
     *
     * @param packageName
     * @param annotationClass
     * @return
     */
    public static List<Class<?>> getClassesByAnnotation(String packageName, Class<? extends Annotation> annotationClass) {
        List<Class<?>> classList = getClasses(packageName);
        if (classList.isEmpty()) {
            return Collections.emptyList();
        }
        List<Class<?>> annotationClassList = new ArrayList<>();
        for (Class<?> claz : classList) {
            if (claz.isAnnotationPresent(annotationClass)) {
                annotationClassList.add(claz);
            }
        }
        return annotationClassList;
    }

    /**
     * 查找指定包下所有的类
     *
     * @param packageName
     * @return
     */
    public static List<Class<?>> getClasses(String packageName) {

        // 第一个class类的集合
        List<Class<?>> classes = new ArrayList<Class<?>>();
        // 是否循环迭代
        boolean recursive = true;
        // 获取包的名字 并进行替换
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    // 如果是jar包文件
                    // 定义一个JarFile
                    JarFile jar;
                    try {
                        // 获取jar
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        // 从此jar包 得到一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        // 同样的进行循环迭代
                        while (entries.hasMoreElements()) {
                            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            // 如果是以/开头的
                            if (name.charAt(0) == '/') {
                                // 获取后面的字符串
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    // 获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                // 如果可以迭代下去 并且是一个包
                                if ((idx != -1) || recursive) {
                                    // 如果是一个.class文件 而且不是目录
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        // 去掉后面的".class" 获取真正的类名
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            // 添加到classes
                                            classes.add(Class.forName(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }

    /**
     * 查找指定包下的所有类对象
     *
     * @param pkgName
     * @param pkgPath
     * @param recursive
     * @param classes
     */
    private static void findAndAddClassesInPackageByFile(String pkgName, String pkgPath, boolean recursive, List<Class<?>> classes) {
        Collection<File> allClassFile = getAllClassFile(pkgPath, recursive);

        for (File curFile : allClassFile) {
            try {
                classes.add(getClassObj(curFile, pkgPath, pkgName));
            } catch (ClassNotFoundException e) {
                logger.error("load class fail", e);
            }
        }
    }

}

