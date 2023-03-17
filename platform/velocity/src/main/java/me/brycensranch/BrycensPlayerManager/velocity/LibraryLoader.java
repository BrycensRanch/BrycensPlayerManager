package me.brycensranch.BrycensPlayerManager.velocity;

import com.velocitypowered.api.plugin.PluginManager;
import net.byteflux.libby.Library;
import net.byteflux.libby.VelocityLibraryManager;
import net.byteflux.libby.relocation.Relocation;
import me.brycensranch.BrycensPlayerManager.common.Constants;


public class LibraryLoader {
    private final String platform = "velocity";
    private final String platformPackage = "me.brycensranch.BrycensPlayerManager.libs.platform_" + platform;
    public void loadDependencies(BPMPlugin plugin, PluginManager pluginManager) {
        plugin.logger.info("Loading libraries...");
        VelocityLibraryManager libraryManager = new VelocityLibraryManager(
                plugin.logger, plugin.dataDirectory, pluginManager, plugin, "libs");
        libraryManager.addMavenCentral();
//        libraryManager.addRepository("https://repo1.maven.org/maven2/");
        String geantyrefPackage = "io{}leangen{}geantyref";
        String commandCorePackage = "cloud{}commandframework";
        String commandVelocityPackage = "cloud{}commandframework{}velocity";
        Relocation geantyrefRelocation = relocate(geantyrefPackage);
        Relocation commandCoreRelocation = relocate(commandCorePackage);
        Relocation commandVelocityRelocation = relocate(commandVelocityPackage);
        Library geantyref = Library.builder()
                .groupId(geantyrefPackage)
                .artifactId("geantyref")
                .version(Constants.PluginMetadata.GEANTYREF)
                .id("geantyref")
                .relocate(geantyrefRelocation)
                .build();
        Library cloudCore = Library.builder()
                .groupId(commandCorePackage)
                .artifactId("cloud-core")
                .version("1.8.3")
                .id("cloud-core")
                .relocate(commandCoreRelocation)
                .build();
        Library cloudVelocity = Library.builder()
                .groupId(commandCorePackage)
                .artifactId("cloud-velocity")
                .version("1.8.3")
                .id("cloud-velocity")
                .relocate(commandVelocityRelocation)
                .build();
        libraryManager.loadLibrary(geantyref);
        libraryManager.loadLibrary(cloudCore);
        libraryManager.loadLibrary(cloudVelocity);
        plugin.logger.info("Libraries loaded!");


    }
    private Relocation relocate(String pkg) {
        return new Relocation(pkg, platformPackage + "." + pkg);
    }
 }
