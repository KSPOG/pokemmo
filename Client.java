import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.lang.reflect.Method;

public class Client {
    private final Logger logger;
    private final List<ClientPlugin> plugins = new ArrayList<>();
    private final List<Object> plugins = new ArrayList<>();
    private final Path gamePath;
    private URLClassLoader injectionLoader;
    private Path injectedJar;

    public Client(Path gamePath) {
        this.gamePath = gamePath != null ? gamePath : defaultGamePath();
        this.logger = createLogger();
    }

    private Logger createLogger() {
        Logger log = Logger.getLogger("pokemmo.client");
        log.setUseParentHandlers(false);
        if (log.getHandlers().length == 0) {
            Handler handler = new ConsoleHandler();
            handler.setFormatter(new SimpleFormatter());
            log.addHandler(handler);
            log.setLevel(Level.INFO);
        }
        return log;
    }

    private Path defaultGamePath() {
        Path root = Paths.get("").toAbsolutePath();
        if (Files.exists(root.resolve("PokeMMO.exe"))) {
            return root.resolve("PokeMMO.exe");
        }
        return root.resolve("PokeMMO.sh");
    }

    public Logger getLogger() {
        return logger;
    }

    public void loadPlugins(Path directory) {
        Path dir = directory != null ? directory : Paths.get("plugins");
        if (!Files.exists(dir)) {
            logger.fine("Plugin directory " + dir + " does not exist");
            return;
        }
        ClassLoader loader = injectionLoader;
        try {
            if (loader == null) {
                loader = new URLClassLoader(new URL[]{dir.toUri().toURL()});
            }
            final ClassLoader pluginLoader = loader;
            try (Stream<Path> stream = Files.walk(dir)) {
                stream.filter(p -> p.toString().endsWith(".class")).forEach(entry -> {
                    String className = dir.relativize(entry).toString()
                            .replace(File.separatorChar, '.')
                            .replaceFirst("\\.class$", "");
                    try {
                        Class<?> cls = Class.forName(className, true, pluginLoader);
                        if (ClientPlugin.class.isAssignableFrom(cls)) {
                            ClientPlugin plugin = (ClientPlugin) cls.getDeclaredConstructor().newInstance();
                            plugins.add(plugin);
                            logger.info("Loaded plugin " + className);
                        }
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Failed to load plugin " + className, e);
                    }
                });
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.class")) {
                for (Path entry : stream) {
                    String className = entry.getFileName().toString().replaceFirst("\\.class$", "");
                    try {
                        Class<?> cls = Class.forName(className, true, loader);
                        Object plugin = cls.getDeclaredConstructor().newInstance();
                        plugins.add(plugin);
                        logger.info("Loaded plugin " + className);
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Failed to load plugin " + className, e);
                    }
                }
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error reading plugin directory", e);
        }
    }

    public void prepareInjection(Path pluginDir) {
        Path jar = findGameJar();
        if (jar == null) {
            logger.fine("No injectable game jar found, using external launch");
            return;
        }
        List<URL> urls = new ArrayList<>();
        try {
            urls.add(jar.toUri().toURL());
            Path dir = pluginDir != null ? pluginDir : Paths.get("plugins");
            if (Files.exists(dir)) {
                urls.add(dir.toUri().toURL());
            }
            injectionLoader = new URLClassLoader(urls.toArray(new URL[0]), ClassLoader.getSystemClassLoader());
            injectedJar = jar;
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to setup injection", e);
        }
    }

    private Path findGameJar() {
        Path base = gamePath.getParent();
        Path jar = searchJar(base);
        if (jar == null) {
            jar = searchJar(base.resolve("lib"));
        }
        return jar;
    }

    private Path searchJar(Path dir) {
        if (dir == null || !Files.isDirectory(dir)) {
            return null;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.jar")) {
            for (Path p : stream) {
                if (!p.getFileName().toString().equals("jrt-fs.jar")) {
                    return p;
                }
            }
        } catch (IOException e) {
            logger.log(Level.FINE, "Error searching jars in " + dir, e);
        }
        return null;
    }

    public void run() throws IOException {
        if (injectionLoader != null && injectedJar != null) {
            try {
                logger.info("Injecting into PokeMMO from " + injectedJar);
                Thread.currentThread().setContextClassLoader(injectionLoader);
                try (JarFile jar = new JarFile(injectedJar.toFile())) {
                    Manifest mf = jar.getManifest();
                    String mainClass = mf.getMainAttributes().getValue("Main-Class");
                    if (mainClass == null) {
                        throw new IllegalStateException("Main-Class not found in manifest");
                    }
                    Class<?> cls = Class.forName(mainClass, true, injectionLoader);
                    Method main = cls.getMethod("main", String[].class);
                    main.invoke(null, (Object) new String[0]);
                }
                return;
            } catch (Exception e) {
                logger.log(Level.WARNING, "Injection failed, falling back to external launch", e);
            }
        }
        logger.info("Launching PokeMMO from " + gamePath);
        ProcessBuilder pb;
        if (gamePath.toString().endsWith(".sh")) {
            pb = new ProcessBuilder("bash", gamePath.toString());
        } else {
            pb = new ProcessBuilder(gamePath.toString());
        }
        pb.directory(gamePath.getParent().toFile());
        pb.start();
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client(null);
        client.prepareInjection(null);
        client.loadPlugins(null);
        for (ClientPlugin plugin : client.plugins) {
            try {
                plugin.run(client);
        for (Object plugin : client.plugins) {
            try {
                plugin.getClass().getMethod("run", Client.class).invoke(plugin, client);
            } catch (NoSuchMethodException e) {
                // plugin doesn't have run(Client)
            } catch (Exception e) {
                client.logger.log(Level.WARNING, "Error running plugin", e);
            }
        }
        client.run();
    }
}
