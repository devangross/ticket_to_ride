package strategy;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Class to contain the loader for a Strategy in the Strategy package.
 */
public class StrategyLoader {

    /**
     * Function to return an IStrategy object given an absolute filepath to a
     * .class file which implements IStrategy.
     *
     * @param path given path to the strategy class file
     *
     * @return an IStrategy object
     *
     * @throws IllegalArgumentException if the given class is not an IStrategy
     *                                  or the file path is not recognizable.
     */
    public static IPlayerStrategy loadStrategyFromPath(String path) {
        int folderLocation = path.lastIndexOf('/');
        try {
            URL[] url = {new File(
                    path.substring(0, folderLocation)).toURI().toURL()};
            URLClassLoader urlClassLoader = new URLClassLoader(url,
                    Thread.currentThread().getContextClassLoader());
            Class<?> strategyClass = urlClassLoader.loadClass(
                    path.substring(folderLocation + 1));
            Object strategyObject =
                    strategyClass.getDeclaredConstructor().newInstance();

            if (strategyObject instanceof IPlayerStrategy) {
                return (IPlayerStrategy) strategyObject;
            } else {
                throw new IllegalArgumentException(
                        "Unable to find class which implements IStrategy.");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Unable to load file path: " + e);
        }
    }

}
