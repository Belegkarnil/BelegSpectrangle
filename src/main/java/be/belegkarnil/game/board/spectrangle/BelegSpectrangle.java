/*
 *  Copyright 2025 Belegkarnil
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 *  associated documentation files (the “Software”), to deal in the Software without restriction,
 *  including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
 *  so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial
 *  portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 *  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 *  OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package be.belegkarnil.game.board.spectrangle;

import be.belegkarnil.game.board.spectrangle.gui.SpectrangleFrame;
import be.belegkarnil.game.board.spectrangle.strategy.*;

import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * This class is the main class that run the game with GUI.
 *
 * @author Belegkarnil
 */
public class BelegSpectrangle {
    private static final LinkedList<Class<Strategy>> strategies = new LinkedList<Class<Strategy>>();
    static {
        try {
            loadDefaultStrategies();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadDefaultStrategies() throws IOException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final String defaultStrategiesPackage = "be.belegkarnil.game.board.spectrangle";
        Enumeration<URL> resources = classLoader.getResources(defaultStrategiesPackage.replace(".","/"));
        java.util.List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, defaultStrategiesPackage));
        }
        for(Class<?> klass:classes) {
            if(isStrategy(klass) && hasDefaultConstructor(klass)){
                strategies.add((Class<Strategy>) klass);
            }
        }
    }
    public static boolean isStrategy(Class klass){
        Class<?>[] interfaces = klass.getInterfaces();
        for(Class<?> inter:interfaces) {
            if (inter.getName().equals(Strategy.class.getName())) return true;
        }
        Class<?> superclass = klass.getSuperclass();
        if (superclass != null && superclass.getName().equals(StrategyAdapter.class.getName())) return true;
        return false;
    }
    public static java.util.List<Constructor<Strategy>> constructorOnlyWith(Class<Strategy> klass, java.util.List<Class> classes){
        Constructor<Strategy>[] constructors  = (Constructor<Strategy>[]) klass.getConstructors();
        java.util.List<Constructor<Strategy>> results   = new LinkedList<Constructor<Strategy>>();
        for(Constructor<Strategy> constructor:constructors){
            Class<?>[] types = constructor.getParameterTypes();
            boolean respect = true;
            for(Class<?> type:types){
                if(! classes.contains(type))
                    respect = false;
            }
            if(respect) results.add(constructor);
        }
        // More complex first
        results.sort(new Comparator<Constructor<Strategy>>() {
            @Override
            public int compare(Constructor<Strategy> a, Constructor<Strategy> b) {
                return - Integer.compare(a.getParameterCount(),b.getParameterCount());
            }
        });
        return results;
    }
    public static boolean hasDefaultConstructor(Class klass){
        try {
            Constructor<Strategy> constructor = klass.getConstructor();
            constructor.newInstance();
        } catch (NoSuchMethodException e) {
            return false;
        } catch (InvocationTargetException e) {
            return false;
        } catch (InstantiationException e) {
            return false;
        } catch (IllegalAccessException e) {
            return false;
        }
        return true;
    }

    private static java.util.List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        java.util.List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    public static java.util.List<Class<? extends Strategy>> listStrategies(){
        List<Class<? extends Strategy>> lst = new ArrayList();
        lst.add(HMIStrategy.class);
        lst.add(RandomStrategy.class);
        lst.add(SkipStrategy.class);
        return lst;
        //return (List<Class<Strategy>>) strategies.clone();
    }

    public static void loadStrategy(File path) throws MalformedURLException {
        URLClassLoader loader = new URLClassLoader(new URL[]{path.getAbsoluteFile().toURI().toURL()});
        //loader.
    }

    public static void main(String[] args) {
        Window window = new SpectrangleFrame();
        window.pack();
        window.setLocationRelativeTo(window.getParent());
        window.setVisible(true);
    }
}